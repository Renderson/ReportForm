package com.rendersoncs.reportform.itens;

import java.util.ArrayList;

public class Repo {

    private int id, conformed;

    private String company, email, date, photo, listJson, contents, buttonLayout;
    private String title, text;
    private String radioConformed, radioNotApplicable, radioNotConformed;
    private ArrayList<String> CheckList;

    public Repo(String title, String text) {
        this.title = title;
        this.text = text;
        this.buttonLayout = buttonLayout;
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

    public String getButtonLayout() {
        return buttonLayout;
    }

    public void setButtonLayout(String buttonLayout) {
        this.buttonLayout = buttonLayout;
    }

    public String getRadioConformed() {
        return radioConformed;
    }

    public void setRadioConformed(String radioConformed) {
        this.radioConformed = radioConformed;
    }

    public String getRadioNotApplicable() {
        return radioNotApplicable;
    }

    public void setRadioNotApplicable(String radioNotApplicable) {
        this.radioNotApplicable = radioNotApplicable;
    }

    public String getRadioNotConformed() {
        return radioNotConformed;
    }

    public void setRadioNotConformed(String radioNotConformed) {
        this.radioNotConformed = radioNotConformed;
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
}