package com.rendersoncs.reportform.itens;

import android.graphics.Bitmap;
import android.net.Uri;

public class ReportItems {

    private int id, conformed;

    private String company, email, date, photo, listJson;
    private String title, description, header;

    private Bitmap photoId;
    private Uri photoUri;

    private boolean opt1, opt2;
    private int selectedAnswerPosition = -1;
    private boolean shine;

    public ReportItems(String title, String description, String header) {
        this.header = header;
        this.title = title;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Bitmap getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Bitmap photoId) {
        this.photoId = photoId;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}