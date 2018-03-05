package com.kustomer.kustomersdk.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSCustomerDescription {

    //region Properties
    private String email;
    private String phone;
    private String twitter;
    private String facebook;
    private String instagram;
    private String linkedin;
    private JSONObject custom;
    //endregion

    //region Methods
    public HashMap<String, Object> formData(){
        HashMap<String, Object> formData = new HashMap<>();

        if(email != null){
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("email",email);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            formData.put("emails", array );
        }

        if(phone != null){
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("phone",phone);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            formData.put("phones", array);
        }

        List<JSONObject> socials = new ArrayList<>();
        if(twitter != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","twitter");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(facebook != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","facebook");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(instagram != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","instagram");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(linkedin != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","linkedin");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(socials.size() > 0){
            formData.put("socials",socials);
        }

        if(custom != null)
            formData.put("custom",custom);

        return formData;
    }
    //endregion

    //region Accessors

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public JSONObject getCustom() {
        return custom;
    }

    public void setCustom(JSONObject custom) {
        this.custom = custom;
    }

    //endregion
}
