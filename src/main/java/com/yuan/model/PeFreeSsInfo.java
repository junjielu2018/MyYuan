package com.yuan.model;

/**
 * @author lujunjie
 * @date 2018/6/21 16:45
 */
public class PeFreeSsInfo {

    private int id;

    private String vtum;

    private String address;

    private String port;

    private String password;

    /**
     * 加密方式
     */
    private String method;

    /**
     * 更新时间
     */
    private String time;

    /**
     * 服务器地址
     */
    private String city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVtum() {
        return vtum;
    }

    public void setVtum(String vtum) {
        this.vtum = vtum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
