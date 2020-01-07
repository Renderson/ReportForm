package com.rendersoncs.reportform.itens;

import android.graphics.Bitmap;

public class ReportItems {

    private int id, conformed, position;

    private String company, email, controller, date, photo, note, score, listJson;
    private String title, description, header;

    private Bitmap photoId;

    private boolean opt1, opt2, opt3;
    private int selectedAnswerPosition = -1;
    private boolean shine;

    public ReportItems(String title, String description, String header) {
        this.header = header;
        this.title = title;
        this.description = description;
    }

    public ReportItems() { }

//    protected ReportItems(Parcel in) {
//        note = in.readString();
//        id = in.readInt();
//        conformed = in.readInt();
//        position = in.readInt();
//        company = in.readString();
//        email = in.readString();
//        date = in.readString();
//        photo = in.readString();
//        listJson = in.readString();
//        title = in.readString();
//        description = in.readString();
//        header = in.readString();
//        photoId = in.readParcelable(Bitmap.class.getClassLoader());
//        opt1 = in.readByte() != 0;
//        opt2 = in.readByte() != 0;
//        opt3 = in.readByte() != 0;
//        selectedAnswerPosition = in.readInt();
//        shine = in.readByte() != 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(this.note);
//        parcel.writeValue(this.id);
//        parcel.writeValue(this.conformed);
//        parcel.writeValue(this.position);
//        parcel.writeString(this.company);
//        parcel.writeString(this.email);
//        parcel.writeString(this.date);
//        parcel.writeString(this.photo);
//        parcel.writeString(this.listJson);
//        parcel.writeString(this.title);
//        parcel.writeString(this.description);
//        parcel.writeString(this.header);
//        parcel.writeValue(this.photoId);
//        parcel.writeValue(this.opt1);
//        parcel.writeValue(this.opt2);
//        parcel.writeValue(this.opt3);
//        parcel.writeValue(this.selectedAnswerPosition);
//        parcel.writeValue(this.shine);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<ReportItems> CREATOR = new Creator<ReportItems>() {
//        @Override
//        public ReportItems createFromParcel(Parcel in) {
//            return new ReportItems(in);
//        }
//
//        @Override
//        public ReportItems[] newArray(int size) {
//            return new ReportItems[size];
//        }
//    };

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

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
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
            setOpt3(false);
        }
    }

    public boolean isOpt2() {
        return opt2;
    }

    public void setOpt2(boolean opt2) {
        this.opt2 = opt2;
        if (opt2){
            setOpt1(false);
            setOpt3(false);
        }
    }

    public boolean isOpt3() {
        return opt3;
    }

    public void setOpt3(boolean opt3) {
        this.opt3 = opt3;
        if (opt3){
            setOpt1(false);
            setOpt2(false);
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}