package com.rendersoncs.reportform.itens;

import java.util.ArrayList;
import java.util.Calendar;

public class Repo {

    private int id, conformed;

    private String company, email, date, photo, listJson, contents;
    private String title, text;
    private String title_list;
    private ArrayList<String> CheckList;

    public Repo(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Repo() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getConformed() {
        return conformed;
    }

    public void setConformed(int conformed) {
        this.conformed = conformed;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContents() {
        return contents;
    }

    public Repo(String contents) {
        this.contents = contents;
    }

    public ArrayList<String> getCheckList() {
        return CheckList;
    }

    public void setCheckList(ArrayList<String> checkList) {
        CheckList = checkList;
    }

    public String getListJson() {
        return listJson;
    }

    public void setListJson(String listJson) {
        this.listJson = listJson;
    }

    public String getTitle_list() {
        return title_list;
    }

    public void setTitle_list(String title_list) {
        this.title_list = title_list;
    }
}