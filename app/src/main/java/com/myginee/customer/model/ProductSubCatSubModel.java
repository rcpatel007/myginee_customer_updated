package com.myginee.customer.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ProductSubCatSubModel {

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

    public static class Data implements Serializable
    {

        public Data(ArrayList<String> imageUrls, String id, String name, String imageUrl, String description, Integer modelNo, Integer price,
                    Integer ratings, String createdAt, String updatedAt, String productCategoryId, String productSubCategoryId) {
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
        }

        @SerializedName("image_urls")
        @Expose
        private ArrayList<String> imageUrls = null;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image_url")
        @Expose
        private String imageUrl;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("model_no")
        @Expose
        private Integer modelNo;
        @SerializedName("price")
        @Expose
        private Integer price;
        @SerializedName("ratings")
        @Expose
        private Integer ratings;
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
        @SerializedName("__v")
        @Expose
        private Integer v;
        private final static long serialVersionUID = 7742086368274836955L;

        public List<String> getImageUrls() {
            return imageUrls;
        }

        public void setImageUrls(ArrayList<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public Integer getModelNo() {
            return modelNo;
        }

        public void setModelNo(Integer modelNo) {
            this.modelNo = modelNo;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getRatings() {
            return ratings;
        }

        public void setRatings(Integer ratings) {
            this.ratings = ratings;
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
}
