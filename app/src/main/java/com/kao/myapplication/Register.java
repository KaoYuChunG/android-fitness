package com.kao.myapplication;

public class Register {

    public int id;
    public String type;
    public double response;
    public String createdDate;

    @Override
    public String toString() {
        return "Register{" +
                "type='" + type + '\'' +
                ", response=" + response +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
