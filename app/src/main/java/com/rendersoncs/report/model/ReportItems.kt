package com.rendersoncs.report.model

class ReportItems {
    var id = 0
    var position = 0
    var company: String? = null
    var email: String? = null
    var controller: String? = null
    var date: String? = null
    var photo: String? = null
    var note: String? = null
    var score: String? = null
    var result: String? = null
    var listJson: String? = null
    var title: String? = null
    var description: String? = null
    var header: String? = null
        private set
    var key: String? = null
    var photoPath: String? = null

    var isOpt1 = false
        set(opt1) {
            field = opt1
            if (opt1) {
                isOpt2 = false
                isOpt3 = false
            }
        }
    var isOpt2 = false
        set(opt2) {
            field = opt2
            if (opt2) {
                isOpt1 = false
                isOpt3 = false
            }
        }
    var isOpt3 = false
        set(opt3) {
            field = opt3
            if (opt3) {
                isOpt1 = false
                isOpt2 = false
            }
        }
    var selectedAnswerPosition = -1
    var isShine = false

    constructor(title: String?, description: String?, header: String?, key: String?) {
        this.header = header
        this.title = title
        this.description = description
        this.key = key
    }

    constructor()

    constructor(header: String?) {
        this.header = header
    }

    fun companyFormatter(): String {
        return this.company!!.replace(" ", "-")
    }
}
