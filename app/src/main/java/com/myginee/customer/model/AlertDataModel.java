package com.myginee.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AlertDataModel {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    private final static long serialVersionUID = 4982185470477892932L;

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

        public Datum(String type, String id, String createdAt, String updatedAt, String orderId,
                     String customerId, String agentId, Order order, String title, String description) {
            this.type = type;
            this.id = id;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.orderId = orderId;
            this.customerId = customerId;
            this.agentId = agentId;
            this.order = order;
            this.title = title;
            this.description = description;
        }

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("agent_id")
        @Expose
        private String agentId;
        @SerializedName("__v")
        @Expose
        private Integer v;
        @SerializedName("order")
        @Expose
        private Order order;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        private final static long serialVersionUID = 630265703846828014L;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public class Order implements Serializable
    {

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
        @SerializedName("agent_id")
        @Expose
        private String agentId;
        @SerializedName("is_accepted")
        @Expose
        private Boolean isAccepted;
        @SerializedName("latitude")
        @Expose
        private Double latitude;
        @SerializedName("longitude")
        @Expose
        private Double longitude;
        @SerializedName("payment_mode")
        @Expose
        private Integer paymentMode;
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
        private Integer serviceProvisionDate;
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
        private Integer v;
        @SerializedName("product_list")
        @Expose
        private List<ProductList> productList = null;
        @SerializedName("is_dispatched")
        @Expose
        private Boolean isDispatched;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        private final static long serialVersionUID = -3224714015697947893L;

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

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
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

        public Integer getServiceProvisionDate() {
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

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public List<ProductList> getProductList() {
            return productList;
        }

        public void setProductList(List<ProductList> productList) {
            this.productList = productList;
        }

        public Boolean getIsDispatched() {
            return isDispatched;
        }

        public void setIsDispatched(Boolean isDispatched) {
            this.isDispatched = isDispatched;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

    }
    public class ProductList implements Serializable
    {

        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("price")
        @Expose
        private Integer price;
        private final static long serialVersionUID = -7765292511733933280L;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

    }
    public class ServiceList implements Serializable
    {

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
        private final static long serialVersionUID = 8444550788785225526L;

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
