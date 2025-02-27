package com.rendersoncs.report.view.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Pair
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.pdf.PDFGenerator
import com.rendersoncs.report.common.util.*
import com.rendersoncs.report.model.*
import com.rendersoncs.report.repository.ReportRepository
import com.rendersoncs.report.util.ReportConstants.PACKAGE.FILE_PROVIDER
import com.rendersoncs.report.view.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
        application: Application,
        private val repository: ReportRepository,
        private val sharePref: SharePrefInfoUser
) : AndroidViewModel(application) {

    private val _uiState = SingleLiveEvent<ViewState>()
    val uiState: LiveData<ViewState> = _uiState

    private val _detailState = SingleLiveEvent<DetailState>()
    val detailState: LiveData<DetailState> = _detailState

    private val _resumeList = SingleLiveEvent<ResumeState>()
    val resumeList: LiveData<ResumeState> = _resumeList

    private var _reportResumeItems = SingleLiveEvent<ArrayList<ReportResumeItems>>()
    var reportResumeItems: LiveData<ArrayList<ReportResumeItems>> = _reportResumeItems

    private var _reportItemsUpdate = SingleLiveEvent<ArrayList<ReportItems>>()
    var reportItemsUpdate: LiveData<ArrayList<ReportItems>> = _reportItemsUpdate

    var checkReportItems = MutableLiveData<ArrayList<ReportItems>>()

    private var _uiStateScore = MutableStateFlow(10.0F)
    val uiStateScore: StateFlow<Float> = _uiStateScore

    private var _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private var _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private var _photo = MutableStateFlow("")
    val photo: StateFlow<String> = _photo

    var userUid = MutableLiveData("")

    var savedReport = SingleLiveEvent<Long>()
    var savedCheckList = SingleLiveEvent<Long>()
    val pdfCreated = SingleLiveEvent<Boolean>()

    fun getUserUid(user: FirebaseUser?) {
        userUid.value = user?.uid
    }

    fun getAllReports() = viewModelScope.launch {
        _uiState.value = ViewState.Loading
        val result = repository.getUserWithReport(userUid.value ?: "")
        if (result.isEmpty()) {
            _uiState.value = ViewState.Empty
        } else {
            result.forEach { data ->
                _uiState.value = ViewState.Success(data.reports)
            }
        }
    }

    fun insertReport(report: Report) = viewModelScope.launch {
        savedReport.value = repository.insertReport(report)
    }

    fun updateReport(
        id: Int,
        report: Report
    ) = viewModelScope.launch {
        repository.updateReport(id = id, report = report)
    }

    fun insertCheckList(reportCheckList: ReportCheckList) = viewModelScope.launch {
        savedCheckList.value = repository.insertCheckList(reportCheckList)
    }

    fun deleteReportByID(id: Int) = viewModelScope.launch {
        repository.deleteReportByID(id)
        getAllReports()
    }

    fun deleteCheckListById(id: Int) = viewModelScope.launch {
        repository.deleteCheckList(id)
    }

    fun getReportByID(id: Int) = viewModelScope.launch {
        _detailState.value = DetailState.Loading
        _detailState.value = DetailState.Success(repository.getReportById(id))
    }

    fun removeItem(reportItems: ReportItems, databaseReference: DatabaseReference?) = viewModelScope.launch {
        val query = databaseReference!!.orderByChild(ReportConstants.ITEM.KEY)
                .equalTo(reportItems.key)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    ds.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //toast(getString(R.string.label_error_update_list))
            }

        })
    }

    fun calculateScore(reportItems: ArrayList<ReportItems>) = viewModelScope.launch {
        val listMax = ArrayList<String>()
        listMax.clear()
        val startingPoints = 10f
        val losePoints = 0.7f
        for (i in reportItems.indices) {
            if (reportItems[i].isOpt3) {
                if (reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM3) {
                    val nc = R.string.not_according
                    listMax.add(nc.toString())
                }
            }
        }
        val listRadioMax = listMax.size
        val scoreList = listRadioMax * losePoints
        val score = startingPoints - scoreList
        _uiStateScore.value = score
    }

    fun getDocument(item: Report): Pair<String?, Uri?> {
        return try {
            val subject = String.format("Report-%s-%s", item.companyFormatter(), item.dateFormatter())
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Report/$subject.pdf")

            val uri = FileProvider.getUriForFile(getApplication(), FILE_PROVIDER, file)
            return Pair(subject, uri)
        } catch (exception: Exception) {
            Pair(null, null)
        }
    }

    fun deletePhotosDirectory(item: Report) = viewModelScope.launch {
        val list = repository.getReportWithChecklist(item.id.toString())
        try {
            list.forEach { resume ->
                resume.checkList.forEach { table ->
                    val file = File(table.photo)
                    file.delete()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun saveThemeState(preferences: SharedPreferences, position: Int) = viewModelScope.launch {
        val editor = preferences.edit()
        editor.putInt(ReportConstants.THEME.KEY_THEME, position)
        editor.apply()
    }

    fun getInfoUserFireBase(
        user: FirebaseUser,
        databaseReference: DatabaseReference
    ) = viewModelScope.launch {

        if (sharePref.getKey(ReportConstants.FIREBASE.FIRE_NAME)
                && sharePref.getKey(ReportConstants.FIREBASE.FIRE_PHOTO)) {
            sharePref.getUserSharePref(_name, _photo, _email)
        } else {
            for (profile in user.providerData) {
                val name = profile.displayName
                val photoUri = profile.photoUrl

                if (name.isNullOrEmpty()) {
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val nameCurrentUser = dataSnapshot.child(ReportConstants.FIREBASE.FIRE_USERS)
                                    .child(user.uid)
                                    .child(ReportConstants.FIREBASE.FIRE_CREDENTIAL)
                                    .child(ReportConstants.FIREBASE.FIRE_NAME)
                                    .value as String?
                            _name.value = nameCurrentUser.toString()
                            sharePref.saveUserSharePref(nameCurrentUser)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
                if (photoUri == null) {
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val currentPhoto = dataSnapshot.child(ReportConstants.FIREBASE.FIRE_USERS)
                                    .child(user.uid)
                                    .child(ReportConstants.FIREBASE.FIRE_PHOTO)
                                    .value as String?
                            _photo.value = currentPhoto.toString()
                            sharePref.savePhotoSharePref(currentPhoto)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }
            _email.value = user.email.toString()
            sharePref.saveEmailSharePref(user.email.toString())
        }
    }

    fun getNameShared() {
        _name.value = sharePref.getUser()
    }

    fun deletePreference() = viewModelScope.launch {
        sharePref.deleteSharePref()
    }

    fun getListReportResume(report: Report) = viewModelScope.launch {
        _resumeList.value = ResumeState.Loading
        val repo = ArrayList<ReportResumeItems>()
        val result = repository.getReportWithChecklist(report.id.toString())
        result.forEach { items ->
            items.checkList.forEach { resume ->
                repo.add(createdReportItems(resume))
            }
        }
        _resumeList.value = ResumeState.Success(repo)
        _reportResumeItems.value = repo
    }

    fun getCheckListForEdit(id: Int) = viewModelScope.launch {
        val checkList = ArrayList<ReportItems>()
        val result = repository.getReportWithChecklist(id.toString())
        result.forEach { items ->
            items.checkList.forEach { resume ->
                val reportItems = ReportItems()
                reportItems.key = resume.key
                reportItems.note = resume.note
                reportItems.photo = resume.photo
                reportItems.selectedAnswerPosition = resume.conformity
                checkList.add(reportItems)
            }
        }
        _reportItemsUpdate.value = checkList
    }

    fun generatePDF(id: Long) = viewModelScope.launch {
        val checkList = ArrayList<ReportResumeItems>()
        val report = repository.getReportById(id.toInt())
        val result = repository.getReportWithChecklist(id.toString())
        result.forEach { items ->
            items.checkList.forEach { resume ->
                checkList.add(createdReportItems(resume))
            }
        }
        pdfCreated.value = PDFGenerator().generatePDF(report, checkList)

    }

    private fun createdReportItems(resume: ReportCheckList): ReportResumeItems {
        return ReportResumeItems(
            key = resume.key,
            title = resume.title,
            description = resume.description,
            conformity = resume.conformity,
            note = resume.note,
            photo = resume.photo
        )
    }
}