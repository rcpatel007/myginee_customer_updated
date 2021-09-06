package com.myginee.customer.model;

public class Notification {
    public String name;
    public String desc;
    public String date;
    public String imgUrl;
    public String orderId;
    public String status;

    public Notification(String name, String desc, String date, String imgURl, String orderId, String status) {
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.imgUrl = imgURl;
        this.orderId = orderId;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
