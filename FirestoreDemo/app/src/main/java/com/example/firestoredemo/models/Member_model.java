package com.example.firestoredemo.models;

import java.io.Serializable;
import java.util.Date;

public class Member_model implements Serializable {

    private String documentId;
    private String first_name;
    private String middle_name;
    private String surname;
    private String mobile_no;
    private String email_address;
    /*private int age;
    private Date applied_date;
    private String area;
    private Date birth_date;
    private String blood_group;
    private String candidate_status;
    private String country;
    private String state;
    private String city;
    private String landmark;
    private String marital_status;
    private String pincode;
    private String select_post;
    private String sort_gender;
    private String street_address;*/


    public Member_model() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }


   /* public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getCandidate_status() {
        return candidate_status;
    }

    public void setCandidate_status(String candidate_status) {
        this.candidate_status = candidate_status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getSelect_post() {
        return select_post;
    }

    public void setSelect_post(String select_post) {
        this.select_post = select_post;
    }

    public String getSort_gender() {
        return sort_gender;
    }

    public void setSort_gender(String sort_gender) {
        this.sort_gender = sort_gender;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setApplied_date(Date applied_date) {
        this.applied_date = applied_date;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }*/
}
