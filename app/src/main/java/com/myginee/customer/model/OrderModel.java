package com.myginee.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

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

        public Datum(ArrayList<OrderModel.ProductList> productList, ArrayList<String> imageUrls, String id, String name, String imageUrl,
                String description, String modelNo, Integer price,
                Integer ratings, String createdAt, String updatedAt, String productCategoryId,
                String productSubCategoryId, boolean isDispatched, String address) {
            this.imageUrls = imageUrls;
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
            this.description = description;
            this.modelNo = modelNo;
            this.price = price;
            this.ratings = ratings;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.productCategoryId = productCategoryId;
            this.productSubCategoryId = productSubCategoryId;
            this.isDispatched = isDispatched;
            this.address = address;
            this.productList = productList;
        }

        @SerializedName("product_list")
        @Expose
        private List<ProductList> productList = null;
        @SerializedName("total_price")
        @Expose
        private Integer totalPrice;
        @SerializedName("wallet_price")
        @Expose
        private Integer walletPrice;
        @SerializedName("is_active")
        @Expose
        private Boolean isActive;
        @SerializedName("is_dispatched")
        @Expose
        private Boolean isDispatched;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image_url")
        @Expose
        private String imageUrl;
        @SerializedName("image_url")
        @Expose
        private ArrayList<String> imageUrls;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("model_no")
        @Expose
        private String modelNo;
        @SerializedName("price")
        @Expose
        private Integer price;
        @SerializedName("ratings")
        @Expose
        private Integer ratings;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("product_category_id")
        @Expose
        private String productCategoryId;
        @SerializedName("product_sub_category_id")
        @Expose
        private String productSubCategoryId;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("__v")
        @Expose
        private Integer v;





        public List<String> getImageUrls() {
            return imageUrls;
        }

        public void setImageUrls(ArrayList<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getModelNo() {
            return modelNo;
        }

        public void setModelNo(String modelNo) {
            this.modelNo = modelNo;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }
        public List<ProductList> getProductList() {
            return productList;
        }

        public void setProductList(List<ProductList> productList) {
            this.productList = productList;
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

        public Boolean getDispatched() { return isDispatched; }

        public void setDispatched(Boolean isDispatched) { this.isDispatched = isDispatched; }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(String productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public String getProductSubCategoryId() {
            return productSubCategoryId;
        }

        public void setProductSubCategoryId(String productSubCategoryId) {
            this.productSubCategoryId = productSubCategoryId;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

    }
    public static class ProductList implements Serializable
    {

        public ProductList(String productId, Integer price, String name, int quantity) {
            this.productId = productId;
            this.price = price;
            this.name = name;
            this.quantity = quantity;
        }

        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("quantity")
        @Expose
        private int quantity;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("price")
        @Expose
        private Integer price;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
    }

