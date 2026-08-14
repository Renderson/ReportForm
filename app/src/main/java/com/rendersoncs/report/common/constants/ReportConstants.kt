package com.rendersoncs.report.common.constants

import java.util.Properties

class ReportConstants {

    object FIREBASE {
        const val FIRE_USERS = "users"
        const val FIRE_CREDENTIAL = "credential"
        const val FIRE_PHOTO = "photoUrl"
        const val FIRE_LIST = "list"
        const val FIRE_NAME = "name"
        const val FIRE_EMAIL = "email"
    }

    object ADMOB {
        val ADMOB_HLG: String by lazy {
            val properties = Properties()
            try {
                val inputStream = javaClass.classLoader?.getResourceAsStream("secrets.properties")
                properties.load(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            properties.getProperty("ADMOB_HLG_PUB")
        }

        val ADMOB_PROD: String by lazy {
            val properties = Properties()
            try {
                val inputStream = javaClass.classLoader?.getResourceAsStream("secrets.properties")
                properties.load(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            properties.getProperty("ADMOB_PROD_PUB")
        }
    }

    object ITEM {
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val KEY = "key"
        const val OPT_NUM1 = 1
        const val OPT_NUM2 = 2
        const val OPT_NUM3 = 3
    }

    object PHOTO {
        const val REQUEST_CAMERA_X = 1816
        const val RESULT_CAMERA_X = "result"
        const val NOT_PHOTO = "notPhoto"
    }

    object THEME {
        const val MY_PREFERENCE_THEME = "preference_theme"
        const val KEY_THEME = "theme"
    }
}