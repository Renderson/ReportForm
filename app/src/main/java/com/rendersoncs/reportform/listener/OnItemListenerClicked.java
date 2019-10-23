package com.rendersoncs.reportform.listener;

public interface OnItemListenerClicked {
    void radioItemChecked(int itemPosition, int optNum);

    void takePhoto(int position);

    void fullPhoto(int position);

    void insertNote(int position);

    void updateList(int position);

    void removeItem(int position);
}
