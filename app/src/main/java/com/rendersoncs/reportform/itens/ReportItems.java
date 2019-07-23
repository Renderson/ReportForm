package com.rendersoncs.reportform.itens;

import java.util.ArrayList;

public class ReportItems {

    private int id, conformed;

    private String company, email, date, photo, listJson;
    private String title, text, header;
    private ArrayList checkList;

    private boolean opt1, opt2;
    private int selectedAnswerPosition = -1;
    private boolean shine;

    public ReportItems(String title, String text, String header) {
        this.header = header;
        this.title = title;
        this.text = text;
    }

    public ReportItems() {

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

    public String getHeader() {
        return header;
    }

    public ReportItems(String header) {
        this.header = header;
    }

    public String getListJson() {
        return listJson;
    }

    public void setListJson(String listJson) {
        this.listJson = listJson;
    }

    public boolean isOpt1() {
        return opt1;
    }

    public void setOpt1(boolean opt1) {
        this.opt1 = opt1;
        if (opt1){
            setOpt2(false);
        }
    }

    public boolean isOpt2() {
        return opt2;
    }

    public void setOpt2(boolean opt2) {
        this.opt2 = opt2;
        if (opt2){
            setOpt1(false);
        }
    }

    public int getSelectedAnswerPosition() {
        return selectedAnswerPosition;
    }

    public void setSelectedAnswerPosition(int selectedAnswerPosition) {
        this.selectedAnswerPosition = selectedAnswerPosition;
    }

    public boolean isShine() {
        return shine;
    }

    public void setShine(boolean shine) {
        this.shine = shine;
    }

    public ArrayList getCheckList() {
        return checkList;
    }

    public void setCheckList(ArrayList checkList) {
        this.checkList = checkList;
    }
}