package com.example.rssreader;

public class News {
    private String title;
    private String date;
    private String image;
    private String link;
    private String content;

    public News (){

    }

    public News(String title, String date, String image, String link, String content) {
        this.title = title;
        this.date = date;
        this.image = image;
        this.link = link;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }
}
