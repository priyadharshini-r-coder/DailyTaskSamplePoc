package com.example.dailytasksamplepoc.model;


import java.util.HashMap;
import java.util.Map;

public class Entry {

    private String api;

    private String description;

    private String auth;

    private Boolean https;

    private String cors;

    private String link;
    private String category;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Entry() {
    }

    /**
     *
     * @param cors
     * @param auth
     * @param link
     * @param description
     * @param api
     * @param https
     * @param category
     */
    public Entry(String api, String description, String auth, Boolean https, String cors, String link, String category) {
        super();
        this.api = api;
        this.description = description;
        this.auth = auth;
        this.https = https;
        this.cors = cors;
        this.link = link;
        this.category = category;
    }


    public String getApi() {
        return api;
    }


    public void setApi(String api) {
        this.api = api;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }


    public Boolean getHttps() {
        return https;
    }

    public void setHttps(Boolean https) {
        this.https = https;
    }


    public String getCors() {
        return cors;
    }


    public void setCors(String cors) {
        this.cors = cors;
    }


    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
