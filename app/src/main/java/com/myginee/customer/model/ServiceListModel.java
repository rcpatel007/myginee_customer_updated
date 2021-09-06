package com.myginee.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ServiceListModel implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable
    {

        @SerializedName("service_list")
        @Expose
        private List<ServiceList> serviceList = null;
        @SerializedName("description")
        @Expose
        private List<String> description = null;
        @SerializedName("image_urls")
        @Expose
        private List<String> imageUrls = null;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("service_category_id")
        @Expose
        private String serviceCategoryId;
        @SerializedName("service_sub_category_id")
        @Expose
        private String serviceSubCategoryId;
        @SerializedName("__v")
        @Expose
        private Integer v;


        public List<ServiceList> getServiceList() {
            return serviceList;
        }

        public void setServiceList(List<ServiceList> serviceList) {
            this.serviceList = serviceList;
        }

        public List<String> getDescription() {
            return description;
        }

        public void setDescription(List<String> description) {
            this.description = description;
        }

        public List<String> getImageUrls() {
            return imageUrls;
        }

        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
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

        public String getServiceCategoryId() {
            return serviceCategoryId;
        }

        public void setServiceCategoryId(String serviceCategoryId) {
            this.serviceCategoryId = serviceCategoryId;
        }

        public String getServiceSubCategoryId() {
            return serviceSubCategoryId;
        }

        public void setServiceSubCategoryId(String serviceSubCategoryId) {
            this.serviceSubCategoryId = serviceSubCategoryId;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

    }

    public static class ServiceList implements Serializable
    {

        @SerializedName("service_id")
        @Expose
        private String serviceId;

        @SerializedName("is_selected")
        @Expose
        private Boolean is_selected = false;

        @SerializedName("service_name")
        @Expose
        private String serviceName;

        @SerializedName("measurement")
        @Expose
        private String measurement;

        @SerializedName("price")
        @Expose
        private String price;

        @SerializedName("mrp")
        @Expose
        private String mrp;

        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("image_url")
        @Expose
        private String image_url = "";

        @SerializedName("is_added_to_cart")
        @Expose
        private Boolean is_added_to_cart;

        public ServiceList(String serviceId, String serviceName, String measurement, String price,
                           Boolean is_selected, String mrp, String description,
                           Boolean is_added_to_cart) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.measurement = measurement;
            this.price = price;
            this.is_selected = is_selected;
            this.mrp = mrp;
            this.description = description;
            this.is_added_to_cart = is_added_to_cart;
        }

        public ServiceList(String serviceId, String serviceName, String measurement, String price,
                           Boolean is_selected, String mrp, String description, String image_url,
                           Boolean is_added_to_cart) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.measurement = measurement;
            this.price = price;
            this.is_selected = is_selected;
            this.mrp = mrp;
            this.description = description;
            this.image_url = image_url;
            this.is_added_to_cart = is_added_to_cart;
        }

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

        public Boolean getIs_selected() {
            return is_selected;
        }

        public void setIs_selected(Boolean is_selected) {
            this.is_selected = is_selected;
        }

        public String getMrp() {
            return mrp;
        }

        public void setMrp(String mrp) {
            this.mrp = mrp;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public Boolean getIs_added_to_cart() {
            return is_added_to_cart;
        }

        public void setIs_added_to_cart(Boolean is_added_to_cart) {
            this.is_added_to_cart = is_added_to_cart;
        }
    }
}
