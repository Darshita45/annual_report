package com.example.annual_report;

public class arsluploadpdf {
    public String name;
    public String url;

    public arsluploadpdf(){
        name = null;
        url = null;
    }

    public arsluploadpdf(String name, String url){
        this.name = name;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }
}
