package com.rendersoncs.reportform.itens;

public class ReportResumeItems {

    private String title_list, radio_tx;

    public ReportResumeItems(String title_list, String description_list){
        this.title_list = title_list;
        this.radio_tx = description_list;
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
}
