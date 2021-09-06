package com.myginee.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AccServiceListModel {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    private final static long serialVersionUID = 3580705785962290256L;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public static class Datum implements Serializable
    {

        public Datum(List<ServiceList> serviceList, Integer totalPrice, Integer walletPrice, Boolean isActive,
                     Boolean isAccepted, String id, String categoryId, String subcategoryId, String description, Long serviceProvisionDate, String address,
                     String agentId, String createdAt, String updatedAt, String userId,
                     String subCategoryName, String subCategoryImageUrl) {
            this.serviceList = serviceList;
            this.totalPrice = totalPrice;
            this.walletPrice = walletPrice;
            this.isActive = isActive;
            this.isAccepted = isAccepted;
            this.id = id;
            this.categoryId = categoryId;
            this.subcategoryId = subcategoryId;
            this.description = description;
            this.serviceProvisionDate = serviceProvisionDate;
            this.address = address;
            this.agentId = agentId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.userId = userId;
            this.subCategoryName = subCategoryName;
            this.subCategoryImageUrl = subCategoryImageUrl;
        }

        @SerializedName("service_list")
        @Expose
        private List<ServiceList> serviceList = null;
        @SerializedName("total_price")
        @Expose
        private Integer totalPrice;
        @SerializedName("wallet_price")
        @Expose
        private Integer walletPrice;
        @SerializedName("is_active")
        @Expose
        private Boolean isActive;
        @SerializedName("is_accepted")
        @Expose
        private Boolean isAccepted;
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
        private Long serviceProvisionDate;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("agent_id")
        @Expose
        private String agentId;
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
        private Integer v;
        @SerializedName("sub_category_name")
        @Expose
        private String subCategoryName;
        @SerializedName("sub_category_image_url")
        @Expose
        private String subCategoryImageUrl;
        private final static long serialVersionUID = 2990828832339041170L;

        public List<ServiceList> getServiceList() {
            return serviceList;
        }

        public void setServiceList(List<ServiceList> serviceList) {
            this.serviceList = serviceList;
        }

        public Integer getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Integer totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getWalletPrice() {
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

        public Boolean getIsAccepted() {
            return isAccepted;
        }

        public void setIsAccepted(Boolean isAccepted) {
            this.isAccepted = isAccepted;
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

        public Long getServiceProvisionDate() {
            return serviceProvisionDate;
        }

        public void setServiceProvisionDate(Long serviceProvisionDate) {
            this.serviceProvisionDate = serviceProvisionDate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
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

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

        public String getSubCategoryImageUrl() {
            return subCategoryImageUrl;
        }

        public void setSubCategoryImageUrl(String subCategoryImageUrl) {
            this.subCategoryImageUrl = subCategoryImageUrl;
        }

    }

    public static class ServiceList implements Serializable
    {
        public ServiceList(String serviceId, String serviceName, String measurement, String price) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.measurement = measurement;
            this.price = price;
        }

        @SerializedName("service_id")
        @Expose
        private String serviceId;
        @SerializedName("service_name")
        @Expose
        private String serviceName;
        @SerializedName("measurement")
        @Expose
        private String measurement;
        @SerializedName("price")
        @Expose
        private String price;
        private final static long serialVersionUID = -4559758662123393550L;

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getMeasurement() {
            return measurement;
        }

        public void setMeasurement(String measurement) {
            this.measurement = measurement;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

    }
}
