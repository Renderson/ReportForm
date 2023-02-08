package com.rendersoncs.report.view

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Pair
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.util.*
import com.rendersoncs.report.model.*
import com.rendersoncs.report.repository.ReportRepository
import com.rendersoncs.report.view.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
        application: Application,
        private val repository: ReportRepository
) : AndroidViewModel(application) {
    private val sharePref = SharePrefInfoUser()

    private val _uiState = MutableStateFlow<ViewState>(ViewState.Loading)
    val uiState: StateFlow<ViewState> = _uiState

    private val _detailState = MutableStateFlow<DetailState>(DetailState.Loading)
    val detailState: StateFlow<DetailState> = _detailState

    private val _resumeList = MutableStateFlow<ResumeState>(ResumeState.Loading)
    val resumeList: StateFlow<ResumeState> = _resumeList

    private var _uiStateScore = MutableStateFlow(10.0F)
    val uiStateScore: StateFlow<Float> = _uiStateScore

    private var _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private var _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private var _photo = MutableStateFlow("")
    val photo: StateFlow<String> = _photo

    var userUid = MutableLiveData("")

    var id = MutableLiveData<Long>()

    var reportResumeItems = SingleLiveEvent<ArrayList<ReportResumeItems>>()

    fun getUserUid(user: FirebaseUser?) {
        userUid.value = user?.uid
    }

    fun getAllReports() = viewModelScope.launch {
        repository.getUserWithReport(userUid.value ?: "").collect { result ->
            if (result.isEmpty()) {
                _uiState.value = ViewState.Empty
            } else {
                result.forEach { data ->
                    _uiState.value = ViewState.Success(data.reports)
                }
            }
        }
    }

    fun insertReport(report: Report) = viewModelScope.launch {
        id.value = repository.insertReport(report)
    }

    fun insertCheckList(reportCheckList: ReportCheckList) = viewModelScope.launch {
        repository.insertCheckList(reportCheckList)
    }

    fun deleteReportByID(id: Int) = viewModelScope.launch {
        repository.deleteReportByID(id)
    }

    fun getReportByID(id: Int) = viewModelScope.launch {
        _detailState.value = DetailState.Loading
        repository.getReportById(id).collect { result: Report? ->
            if (result != null) {
                _detailState.value = DetailState.Success(result)
            }
        }
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

    fun getDocument(item: Report): Pair<String, Uri> {
        val subject = String.format("Report-%s-%s", item.companyFormatter(), item.dateFormatter())
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Report/$subject.pdf")

        /*val file = File(activity?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "Report/$subject.pdf")*/

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(getApplication(), ReportConstants.PACKAGE.FILE_PROVIDER, file)
        } else {
            Uri.fromFile(file)
        }
        return Pair(subject, uri)
    }

    fun deletePhotosDirectory(item: Report) = viewModelScope.launch {
        repository.getReportWithChecklist(item.id.toString()).collect { list ->
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
    }

    fun saveThemeState(preferences: SharedPreferences, position: Int) = viewModelScope.launch {
        val editor = preferences.edit()
        editor.putInt(ReportConstants.THEME.KEY_THEME, position)
        editor.apply()
    }

    fun getInfoUserFireBase(user: FirebaseUser,
                            databaseReference: DatabaseReference,
                            pref: SharedPreferences) = viewModelScope.launch {

        if (pref.contains(ReportConstants.FIREBASE.FIRE_NAME)
                && pref.contains(ReportConstants.FIREBASE.FIRE_PHOTO)) {
            sharePref.getUserSharePref(pref, _name, _photo, _email)
        } else {
            for (profile in user.providerData) {
                val name = profile.displayName
                val photoUri = profile.photoUrl

                if (name == null || name.isEmpty()) {
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val nameCurrentUser = dataSnapshot.child(ReportConstants.FIREBASE.FIRE_USERS)
                                    .child(user.uid)
                                    .child(ReportConstants.FIREBASE.FIRE_CREDENTIAL)
                                    .child(ReportConstants.FIREBASE.FIRE_NAME)
                                    .value as String?
                            _name.value = nameCurrentUser.toString()
                            sharePref.saveUserSharePref(pref, nameCurrentUser)
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
                            sharePref.savePhotoSharePref(pref, currentPhoto)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }
            _email.value = user.email.toString()
            sharePref.saveEmailSharePref(pref, user.email.toString())
        }
    }

    fun getNameShared(pref: SharedPreferences) {
        _name.value = sharePref.getUser(pref)
    }

    fun deletePreference(pref: SharedPreferences) = viewModelScope.launch {
        sharePref.deleteSharePref(pref)
    }

    fun getListReportResume(report: Report) = viewModelScope.launch {
        val repo = ArrayList<ReportResumeItems>()
        repository.getReportWithChecklist(report.id.toString()).collect {
            it.forEach { list ->
                list.checkList.forEach { resume ->
                    val repoJson = ReportResumeItems(
                        title = resume.title,
                        description = resume.description,
                        conformity = resume.conformity,
                        note = resume.note,
                        photo = resume.photo
                    )
                    repo.add(repoJson)
                }
            }
            _resumeList.value = ResumeState.Success(repo)
            reportResumeItems.value = repo
        }
    }
}