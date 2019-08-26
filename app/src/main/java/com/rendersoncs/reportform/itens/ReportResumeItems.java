package com.rendersoncs.reportform.itens;

public class ReportResumeItems {

    private String title_list, description_list, radio_tx;

    public ReportResumeItems(String title_list, String description_list, String radio_tx){
        this.title_list = title_list;
        this.description_list = description_list;
        this.radio_tx = radio_tx;
    }

    public String getTitle_list() {
        return title_list;
    }

    public void setTitle_list(String title_list) {
        this.title_list = title_list;
    }

    public String getRadio_tx() {
        return radio_tx;
    }

    public void setRadio_tx(String radio_tx) {
        this.radio_tx = radio_tx;
    }

    public String getDescription_list() {
        return description_list;
    }

    public void setDescription_list(String description_list) {
        this.description_list = description_list;
    }
}
