package com.yuan.controller;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.yuan.model.PeFreeSsInfo;
import com.yuan.service.ReptilianFreeSsInfoService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lujunjie
 * @date 2018/6/21 11:16
 */
@Controller
@RequestMapping(value = "/reptilianFreeSsInfo")
public class ReptilianFreeSsInfoController {

    @Autowired
    private ReptilianFreeSsInfoService freeSsInfoService;

    @RequestMapping("/toGetFreeInfo")
    public ModelAndView toGetFreeInfo(){
        ModelAndView result = new ModelAndView("getFreeSsPage");
        return result;
    }

    @RequestMapping("/getFreeInfo")
    public void getFreeInfo(){
        //ModelAndView result = new ModelAndView("redirect:/reptilianFreeSsInfo/toGetFreeInfo");
        String url = "http://free-ss.cf/";
        //构造一个webClient 模拟Chrome 浏览器
        WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.getOptions().setCssEnabled(false);
        //支持JavaScript
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setRedirectEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(false);
        webClient.getOptions().setTimeout(10000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage rootPage = null;
        try {
            rootPage = webClient.getPage(url);
            //设置一个运行JavaScript的时间
            webClient.waitForBackgroundJavaScript(8000);
        } catch (IOException e) {
        }
        String html = rootPage.asXml();
        Document document = Jsoup.parse(html);
        List<Element> eleList = document.select("tbody tr[role$='row']");
        List<String> resultList = new ArrayList<>();
        for (Element element : eleList) {
            resultList.add(element.text());
        }
        List<PeFreeSsInfo> freeSsInfoList = freeSsInfoService.getFreeSsInfoList(resultList);
        if (freeSsInfoList != null && freeSsInfoList.size() >0){
            freeSsInfoService.saveFreeSsInfoList(freeSsInfoList);
        }
    }
}
