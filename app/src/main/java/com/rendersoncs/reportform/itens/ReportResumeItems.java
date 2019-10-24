package com.rendersoncs.reportform.itens;

public class ReportResumeItems {

    private String title, description, conformity, note, photo;

    public ReportResumeItems(String title, String description, String conformity, String note, String photo){
        this.title = title;
        this.description = description;
        this.conformity = conformity;
        this.note = note;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConformity() {
        return conformity;
    }

    public void setConformity(String conformity) {
        this.conformity = conformity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
