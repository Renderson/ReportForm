package com.rendersoncs.reportform.itens

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailPhoto (
        @SerializedName("photo") val photo: String?,
        @SerializedName("title")val title: String?,
        @SerializedName("description")val description: String?,
        @SerializedName("note")val note: String?,
        @SerializedName("conformed")val conformed: String?
) : Serializable