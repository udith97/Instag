package com.hit.instag.model;

import java.util.Date;

public class Post {
    String image, caption, user;
    private Date time;

    public String getImage() {
        return image;
    }

    public String getCaption() {
        return caption;
    }

    public String getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }
}
