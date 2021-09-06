package com.myginee.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("service_list")
    @Expose
    private List<ServiceListAlert> serviceList = null;
    @SerializedName("total_price")
    @Expose
    private double totalPrice;
    @SerializedName("wallet_price")
    @Expose
    private double walletPrice;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("agent_id")
    @Expose
    private String agentId;
    @SerializedName("is_accepted")
    @Expose
    private Boolean isAccepted;
    @SerializedName("latitude")
    @Expose
    private Object latitude;
    @SerializedName("longitude")
    @Expose
    private Object longitude;
    @SerializedName("payment_mode")
    @Expose
    private int paymentMode;
    @SerializedName("transaction_id")
    @Expose
    private Object transactionId;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("subcategory_id")
    @Expose
    private String subcategoryId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("service_provision_date")
    @Expose
    private double serviceProvisionDate;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("__v")
    @Expose
    private double v;

    public List<ServiceListAlert> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceListAlert> serviceList) {
        this.serviceList = serviceList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getWalletPrice() {
        return walletPrice;
    }

    public void setWalletPrice(Integer walletPrice) {
        this.walletPrice = walletPrice;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public Integer getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Integer paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getServiceProvisionDate() {
        return serviceProvisionDate;
    }

    public void setServiceProvisionDate(Integer serviceProvisionDate) {
        this.serviceProvisionDate = serviceProvisionDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}

