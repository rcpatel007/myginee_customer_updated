package com.myginee.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WalletTrans {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    private final static long serialVersionUID = -3767710693340865221L;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public static class Datum implements Serializable
    {

        public Datum(Boolean received, Boolean sent, String id,
                     String orderId, String customerId, Integer walletAmount, String createdAt,
                     String updatedAt, Integer v) {
            this.received = received;
            this.sent = sent;
            this.id = id;
            this.orderId = orderId;
            this.customerId = customerId;
            this.walletAmount = walletAmount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.v = v;
        }

        @SerializedName("received")
        @Expose
        private Boolean received;
        @SerializedName("sent")
        @Expose
        private Boolean sent;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("wallet_amount")
        @Expose
        private Integer walletAmount;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("__v")
        @Expose
        private Integer v;
        private final static long serialVersionUID = 8860938154970534913L;

        public Boolean getReceived() {
            return received;
        }

        public void setReceived(Boolean received) {
            this.received = received;
        }

        public Boolean getSent() {
            return sent;
        }

        public void setSent(Boolean sent) {
            this.sent = sent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public Integer getWalletAmount() {
            return walletAmount;
        }

        public void setWalletAmount(Integer walletAmount) {
            this.walletAmount = walletAmount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

    }

}
