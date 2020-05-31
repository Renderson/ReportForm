package com.rendersoncs.reportform.view.adapter.listener

interface OnItemListenerClicked {
    /* Click choice radio button*/
    fun radioItemChecked(position: Int, optNum: Int)

    /* Click for take photo*/
    fun takePhoto(position: Int)

    /* Click for show details photo*/
    fun fullPhoto(position: Int)

    /* Click for insert note*/
    fun insertNote(position: Int)

    /* Click for update item list*/
    fun updateList(position: Int)

    /* Click for remove item list*/
    fun removeItem(position: Int)

    /* Click for reset list*/
    fun resetItem(position: Int)
}