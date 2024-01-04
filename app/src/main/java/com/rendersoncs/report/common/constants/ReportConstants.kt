package com.rendersoncs.report.common.constants

import java.util.Properties

class ReportConstants {
    object PACKAGE {
        const val FILE_PROVIDER = "com.rendersoncs.report.FileProvider"
    }

    object FIREBASE {
        val URL: String by lazy {
            val properties = Properties()
            try {
                val inputStream = javaClass.classLoader?.getResourceAsStream("secrets.properties")
                properties.load(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            properties.getProperty("FIREBASE_URL")
        }

        const val FIRE_USERS = "users"
        const val FIRE_CREDENTIAL = "credential"
        const val FIRE_PHOTO = "photoUrl"
        const val FIRE_LIST = "list"
        const val FIRE_NAME = "name"
        const val FIRE_EMAIL = "email"
    }

    object REPORT {
        const val REPORT_ID = "reportId"
    }

    object CHARACTERS {
        const val LIMITS_TEXT = 14
    }

    object ITEM {
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val KEY = "key"
        const val CONFORMITY = "conformity"
        const val NOTE = "note"
        const val PHOTO = "photo"
        const val OPT_NUM1 = 1
        const val OPT_NUM2 = 2
        const val OPT_NUM3 = 3
    }

    object PHOTO {
        const val REQUEST_CODE_GALLERY = 2006
        const val REQUEST_CODE_CAMERA = 2000
        const val REQUEST_CAMERA_X = 1816
        const val RESULT_CAMERA_X = "result"
        const val NOT_PHOTO = "notPhoto"
    }

    object THEME {
        const val MY_PREFERENCE_THEME = "preference_theme"
        const val KEY_THEME = "theme"
    }
}