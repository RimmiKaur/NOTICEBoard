package com.example.noticeboard.Model;

public class Post_Information {

    private String post_edit_text, post_image,post_username,post_userId,post_audio="null",profile_pic;

    public Post_Information() {
    }

    public Post_Information(String post_edit_text, String post_image, String post_username, String post_userId, String post_audio, String profile_pic) {
        this.post_edit_text = post_edit_text;
        this.post_image = post_image;
        this.post_username = post_username;
        this.post_userId = post_userId;
        this.post_audio = post_audio;
        this.profile_pic = profile_pic;
    }



    public String getProfile_pic() { return profile_pic; }

    public void setProfile_pic(String profile_pic) { this.profile_pic = profile_pic; }

    public String getPost_audio() { return this.post_audio; }

    public void setPost_audio(String post_audio) { this.post_audio = post_audio; }

    public String getPost_userId() { return this.post_userId; }

    public void setPost_userId(String post_userId) { this.post_userId = post_userId; }

    public String getPost_username() { return this.post_username; }

    public void setPost_username(String post_username) { this.post_username = post_username; }

    public String getPost_image() { return this.post_image; }

    public void setPost_image(String post_image) { this.post_image = post_image; }

    public String getPost_edit_text() {
        return this.post_edit_text;
    }

    public void setPost_edit_text(String post_edit_text) {
        this.post_edit_text = post_edit_text;
    }



}
