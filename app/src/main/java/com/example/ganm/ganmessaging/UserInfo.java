package com.example.ganm.ganmessaging;

public class UserInfo {


    private  String first;
    private  String email;
    private String last;
    private String avatarurl;
    private String phone;
    private String nick;
    private String user_id;

    public UserInfo(String first, String email, String last, String avatarurl, String phone, String nick, String user_id) {
        this.first = first;
        this.email = email;
        this.last = last;
        this.avatarurl = avatarurl;
        this.phone = phone;
        this.nick = nick;
        this.user_id = user_id;
    }

    public UserInfo() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "first='" + first + '\'' +
                ", email='" + email + '\'' +
                ", last='" + last + '\'' +
                ", avatarurl='" + avatarurl + '\'' +
                ", phone='" + phone + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
