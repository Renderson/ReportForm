package com.rendersoncs.reportform.view.services.constants

class ReportConstants {
    object PACKAGE {
        const val FILE_PROVIDER = "com.rendersoncs.reportform.FileProvider"
    }

    object FIREBASE {
        const val URL = "https://reportform-20b2a.firebaseio.com/users"
        const val FIRE_USERS = "users"
        const val FIRE_CREDENTIAL = "credential"
        const val FIRE_PHOTO = "photoUrl"
        const val FIRE_LIST = "list"
        const val FIRE_NAME = "name"
    }

    object REPORT {
        const val REPORT_ID = "reportId"
    }

    object CHARACTERS {
        const val LIMITS_TEXT = 14
    }

    object ITEM {
        const val POSITION = "position"
        const val COMPANY = "company"
        const val EMAIL = "email"
        const val CONTROLLER = "controller"
        const val DATE = "date"
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
        const val REQUEST_PERMISSIONS_CAMERA = 0
        const val NOT_PHOTO = "notPhoto"
    }
}