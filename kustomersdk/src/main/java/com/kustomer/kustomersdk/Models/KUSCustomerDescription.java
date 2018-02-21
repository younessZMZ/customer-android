package com.kustomer.kustomersdk.Models;

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
    private HashMap <String, Object> custom;
    //endregion

    //region Methods
    public HashMap<String, Object> formData(){
        HashMap<String, Object> formData = new HashMap<>();

        if(email != null){
            formData.put("emails", new ArrayList<HashMap<String,Object>>(){{
                add(new HashMap<String, Object>(){{
                    put("email",email);
                }});
            }});
        }

        if(phone != null){
            formData.put("phones", new ArrayList<HashMap<String,Object>>(){{
                add(new HashMap<String, Object>(){{
                    put("phone",phone);
                }});
            }});
        }

        List<HashMap<String,String>> socials = new ArrayList<>();
        if(twitter != null){
            socials.add(new HashMap<String, String>(){{
                put("username",twitter);
                put("type","twitter");
            }});
        }

        if(facebook != null){
            socials.add(new HashMap<String, String>(){{
                put("username",facebook);
                put("type","facebook");
            }});
        }

        if(instagram != null){
            socials.add(new HashMap<String, String>(){{
                put("username",instagram);
                put("type","instagram");
            }});
        }

        if(linkedin != null){
            socials.add(new HashMap<String, String>(){{
                put("username",linkedin);
                put("type","linkedin");
            }});
        }

        if(socials.size() > 0){
            formData.put("socials",socials);
        }

        if(custom.size() > 0)
            formData().put("custom",custom);

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

    public HashMap<String, Object> getCustom() {
        return custom;
    }

    public void setCustom(HashMap<String, Object> custom) {
        this.custom = custom;
    }

    //endregion
}
