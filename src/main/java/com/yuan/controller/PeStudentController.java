package com.yuan.controller;

import com.github.pagehelper.PageInfo;
import com.yuan.model.PeStudent;
import com.yuan.service.PeStudentService;
import com.yuan.utils.ExcelUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sun.plugin2.os.windows.Windows;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 *
 * @author lujunjie
 * @date 2018/05/28
 */
@Controller
@RequestMapping(value = "/student")
public class PeStudentController {

    @Autowired
    private PeStudentService studentService;

    private static final Logger LOG = LogManager.getLogger(HttpClient.class);
    public  static CloseableHttpClient httpClient = null;
    public  static HttpClientContext context = null;
    public  static CookieStore cookieStore = null;
    public  static RequestConfig requestConfig = null;
    private static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

    static {
        init();
    }
    private static void init() {
        context = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        // 配置超时时间（连接服务端超时1秒，请求数据返回超时2秒）
        requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
                .setConnectionRequestTimeout(60000).build();
        // 设置默认跳转以及存储cookie
        httpClient = HttpClientBuilder.create()
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRedirectStrategy(new DefaultRedirectStrategy()).setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore).build();
    }

    @PostMapping("/batchAddStudent")
    public String batchAddStudent(@RequestParam("file") MultipartFile file){
        List<List<Object>> excelList = new ArrayList<>();
        if (!file.isEmpty()) {
            try {
                excelList = ExcelUtils.TransformExcelAllDataToList(file.getInputStream(),0);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (excelList.size() >0){
            List<PeStudent> studentList = new ArrayList<>();
            studentList = getPeStudentListByExcelList(excelList);
            studentService.insertPeStudents(studentList);
        }
        return "index";
    }
    @GetMapping("/findAllUser")
    public String findAllUser(
            Model model,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1")
                    int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "500")
                    int pageSize){
        PageInfo<PeStudent> studentList = new PageInfo<PeStudent>();
        studentList = studentService.findAllUser(pageNum,pageSize);
        model.addAttribute("studentList",studentList);
        return "index";
    }

    public void submitInfoAuto2(PeStudent student) throws Exception{
        /*
         * 第一次请求
         * grab login form page first
         * 获取登陆提交的表单信息，及修改其提交data数据（login，password）
         */
        // get the response, which we will post to the action URL(rs.cookies())

        String url = "http://kwpxdc.haedu.gov.cn/8e17b5e0e72e";
        // 获取connection
        Connection con = Jsoup.connect(url);
        // 配置模拟浏览器
        con.header(USER_AGENT, USER_AGENT_VALUE);
        // 获取响应
        Response rs = null;
        try {
            rs = con.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 转换为Dom树
        Document d1 = Jsoup.parse(rs.body());
        // 获取提交form表单，可以通过查看页面源码代码得知
        List<Element> eleList = d1.select("form");

        // 获取cooking和表单属性
        // lets make data map containing all the parameters and its values found in the form
        Map<String, String> datas = new HashMap<>();
        for (Element e : eleList.get(0).getAllElements()) {

            //所在年级
            if (e.attr("name").equals("ddlnj")) {
                e.attr("value", student.getClassCode());
            }
            //所在班级
            if (e.attr("name").equals("bj")) {
                e.attr("value", student.getClassName());
            }
            //学生姓名
            if (e.attr("name").equals("xm")) {
                e.attr("value", student.getName());
            }
            //身份证件号码
            if (e.attr("name").equals("sfzjh")) {
                e.attr("value", student.getIdCard());
            }
            //联系方式
            if (e.attr("name").equals("tel")) {
                e.attr("value", student.getPhone());
            }
            int classCode = Integer.valueOf(student.getClassCode());

            if ( classCode >= 3){
                //学校布置的家庭作业,每周累计，三年级以下不用填写
                if (e.attr("name").equals("jtzyz")) {
                    e.attr("value", "3.5");
                }
                //每年累计
                if (e.attr("name").equals("jtzyn")) {
                    e.attr("value", "45.5");
                }
            }else{
                //学校布置的家庭作业,每周累计，三年级以下不用填写
                if (e.attr("name").equals("jtzyz")) {
                    e.attr("value", "0");
                }
                //每年累计
                if (e.attr("name").equals("jtzyn")) {
                    e.attr("value", "0");
                }
            }
            // 排除空值表单属性
            if (e.attr("name").length() > 0) {
                datas.put(e.attr("name"), e.attr("value"));
            }
        }

        /*
         * 第二次请求，以post方式提交表单数据以及cookie信息
         */
        Connection con2 = Jsoup.connect("http://kwpxdc.haedu.gov.cn/default.aspx?id=8e17b5e0e72e");
        con2.header(USER_AGENT, USER_AGENT_VALUE);
        // 设置cookie和post上面的map数据
        Response response = null;
        response = con2.ignoreContentType(true).followRedirects(true).method(Method.POST)
                    .data(datas).cookies(rs.cookies()).execute();

        // 打印，登陆成功后的信息
        // parse the document from response
        System.out.println(response.body());

        // 登陆成功后的cookie信息，可以保存到本地，以后登陆时，只需一次登陆即可
        Map<String, String> map = response.cookies();
        for (String s : map.keySet()) {
            System.out.println(s + " : " + map.get(s));
        }
    }


    @GetMapping("/submitInfoAuto")
    public void submitInfoAuto(){
        List<PeStudent> studentList = new ArrayList<>();
        studentList = studentService.findListUserByClassCode("0");
        StringBuilder result = null;
        int i = 0;
        for (PeStudent student : studentList) {
            //result = getSubmit();
            //postSubmit(result, student);
            try {
                submitInfoAuto2(student);
                i ++;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        System.out.println("i======="+i);
    }

    private List<PeStudent> getPeStudentListByExcelList(List<List<Object>> excelList){
        List<PeStudent> peStudentList = new ArrayList<>();
        PeStudent peStudent = null;
        for (List<Object> list : excelList) {
            peStudent = new PeStudent();
            peStudent.setName(String.valueOf(list.get(0)));
            peStudent.setClassName(String.valueOf(list.get(1)));
            peStudent.setSex(String.valueOf(list.get(2)));
            peStudent.setIdCard(String.valueOf(list.get(3)));
            peStudent.setParentName(String.valueOf(list.get(4)));
            peStudent.setPhone(String.valueOf(list.get(5)));
            peStudentList.add(peStudent);
        }
        return peStudentList;
    }

    @GetMapping("/getAllRedirectLocations")
    public List<URI> getAllRedirectLocations(){
        String link = "http://kwpxdc.haedu.gov.cn/default.aspx?id=8e17b5e0e72e";
        List<URI> redirectLocations = null;
        HttpResponse response = null;
        HttpClient client = HttpClientBuilder.create().build();
        StringBuilder result = new StringBuilder();
        try{
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpGet = new HttpGet(link);
            response = client.execute(httpGet, context);
            // 获取所有的重定向位置
            redirectLocations = context.getRedirectLocations();
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
        return redirectLocations;
    }
    @GetMapping("/postTest")
    public void postTest(){
        String url = "http://127.0.0.1:9090/system/loginAction!login";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("user.userName", "admin"));
        urlParameters.add(new BasicNameValuePair("user.password", "111111"));
        StringBuilder result = new StringBuilder();
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/getTest")
    public void getTest(){
        String url = "http://kwpxdc.haedu.gov.cn/msg.aspx?id=1";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        StringBuilder result = new StringBuilder();
        try {
            response = client.execute(request);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            InputStream is = response.getEntity().getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    private StringBuilder getSubmit(){
        String url = "http://kwpxdc.haedu.gov.cn/8e17b5e0e72e";
        HttpGet httpget  = new HttpGet(url);
        CloseableHttpResponse  response = null;
        StringBuilder result = new StringBuilder();
        try {
            response = httpClient.execute(httpget, context);
            cookieStore = context.getCookieStore();
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                System.out.println("key:" + cookie.getName() + "  value:" + cookie.getValue());
            }
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            InputStream is = response.getEntity().getContent();
            Header[] headers = response.getHeaders("Content-Encoding");
            for (Header h : headers) {
                if (h.getValue().indexOf("gzip") > -1) {
                    //For GZip response
                    is = new GZIPInputStream(is);
                    break;
                }
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    private void postSubmit(StringBuilder result,PeStudent student){
        //使用jsoup解析返回的html，获取隐藏文本域的值__VIEWSTATE，__VIEWSTATEGENERATOR，__EVENTVALIDATION
        Document doc = Jsoup.parseBodyFragment(result.toString());
        String __VIEWSTATE = doc.select("#__VIEWSTATE").val();
        String __VIEWSTATEGENERATOR = doc.select("#__VIEWSTATEGENERATOR").val();
        String __EVENTVALIDATION = doc.select("#__EVENTVALIDATION").val();

        //String url = "http://kwpxdc.haedu.gov.cn/8e17b5e0e72e";
        //url = "http://kwpxdc.haedu.gov.cn/7d04f66b-d4de-48ad-9dcb-829282b5a5c2";
        String url = "http://kwpxdc.haedu.gov.cn/8e17b5e0e72e";

        HttpPost post = new HttpPost(url);
        post.setHeader(new BasicHeader("Content-Type", "text/html; charset=utf-8"));
        post.setHeader(new BasicHeader(USER_AGENT,USER_AGENT_VALUE));
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));
        urlParameters.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR));
        urlParameters.add(new BasicNameValuePair("__EVENTVALIDATION", __EVENTVALIDATION));
        urlParameters.add(new BasicNameValuePair("HidXXMC", "鹿邑县张店镇红源小学"));

        //所在年级
        urlParameters.add(new BasicNameValuePair("ddlnj", student.getClassCode()));
        //所在班级
        urlParameters.add(new BasicNameValuePair("bj", student.getClassName()));
        //学生姓名
        urlParameters.add(new BasicNameValuePair("xm", student.getName()));
        //身份证件号码
        urlParameters.add(new BasicNameValuePair("sfzjh", student.getIdCard()));
        //联系方式
        urlParameters.add(new BasicNameValuePair("tel", student.getPhone()));

        int classCode = Integer.valueOf(student.getClassCode());

        if ( classCode >= 3){
            //学校布置的家庭作业,每周累计，三年级以下不用填写
            urlParameters.add(new BasicNameValuePair("jtzyz", "3.5"));
            //每年累计
            urlParameters.add(new BasicNameValuePair("jtzyn", "45.5"));
            //培训班布置的作业，每周累计
            urlParameters.add(new BasicNameValuePair("pxzyz", ""));
            //每年累计
            urlParameters.add(new BasicNameValuePair("pxzyn", ""));
        }else{
            //学校布置的家庭作业,每周累计，三年级以下不用填写
            urlParameters.add(new BasicNameValuePair("jtzyz", "0"));
            //每年累计
            urlParameters.add(new BasicNameValuePair("jtzyn", "0"));
            //培训班布置的作业，每周累计
            urlParameters.add(new BasicNameValuePair("pxzyz", ""));
            //每年累计
            urlParameters.add(new BasicNameValuePair("pxzyn", ""));
        }
        urlParameters.add(new BasicNameValuePair("pxjgmc", ""));
        urlParameters.add(new BasicNameValuePair("SelectCS", "1"));
        urlParameters.add(new BasicNameValuePair("SelectSC", "1"));
        urlParameters.add(new BasicNameValuePair("myjfbz", ""));
        urlParameters.add(new BasicNameValuePair("nljsc", ""));
        urlParameters.add(new BasicNameValuePair("nljjf", ""));
        urlParameters.add(new BasicNameValuePair("hidlist", ""));
        urlParameters.add(new BasicNameValuePair("hidispx", "0"));
        urlParameters.add(new BasicNameValuePair("HidQHDM", "411628000000"));
        urlParameters.add(new BasicNameValuePair("hidpcount", "0"));
        urlParameters.add(new BasicNameValuePair("btnSave", "没有了，提交答卷"));
        CloseableHttpResponse  response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters,"UTF-8"));
            List<URI> redirectLocations = null;
            response = httpClient.execute(post, context);
            cookieStore = context.getCookieStore();
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                System.out.println("key:" + cookie.getName() + "  value:" + cookie.getValue());
            }
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            result.setLength(0);
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
