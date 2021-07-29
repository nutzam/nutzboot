package io.nutz.demo.simple.bean;

import java.util.Date;

/**
 * @author wizzer@qq.com
 */
public class Device {
    private Date ts;
    private Double temperature;
    private String no;

    public Device() {
    }

    public Device(Date ts, Double temperature, String no) {
        super();
        this.ts = ts;
        this.temperature = temperature;
        this.no = no;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
