package com.example.noticeboard.Model;

public class UserInformation {
    private String name;
    private String gender="None";
    private String phone_num;
    private String occupation;
    private String country;
    private String mImageUrl;
    private String DOB="12/11/1999";


    public UserInformation(){

    }

    public UserInformation(String name, String gender, String phone_num, String occupation, String country, String mImageUrl, String DOB) {
        this.name = name;
        this.gender = gender;
        this.phone_num = phone_num;
        this.occupation = occupation;
        this.country = country;
        this.mImageUrl = mImageUrl;
        this.DOB = DOB;
    }


    public String getDOB() { return DOB; }

    public void setDOB(String DOB) { this.DOB = DOB; }

    public String getOccupation() { return occupation; }

    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getGender() {
        return gender;
    }

    public void setGender(String email) {
        this.gender = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getImageUrl() { return mImageUrl; }

    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}
