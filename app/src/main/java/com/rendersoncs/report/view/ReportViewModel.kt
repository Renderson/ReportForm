package com.rendersoncs.report.view

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Pair
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.util.DetailState
import com.rendersoncs.report.infrastructure.util.ResumeState
import com.rendersoncs.report.infrastructure.util.SharePrefInfoUser
import com.rendersoncs.report.infrastructure.util.ViewState
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportItems
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
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

    fun getAllReports() = viewModelScope.launch {
        repository.getAllReports().collect { result ->
            if (result.isNullOrEmpty()) {
                _uiState.value = ViewState.Empty
            } else {
                _uiState.value = ViewState.Success(result)
            }
        }
    }

    fun insertReport(report: Report) = viewModelScope.launch {
        repository.insertReport(report)
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
        try {
            val arrayL = JSONArray(item.listJson)
            for (i in 0 until arrayL.length()) {
                val obj = arrayL.getJSONObject(i)
                val selected = obj.getString(ReportConstants.ITEM.PHOTO)
                val file = File(selected)
                file.delete()
            }
        } catch (e: JSONException) {
            FirebaseCrashlytics.getInstance().recordException(e)
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
                //_name.value = name.toString()
            }
            //_name.value = user.displayName.toString()
            _email.value = user.email.toString()
            //_photo.value = user.photoUrl.toString()
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
        val array = JSONArray(report.listJson)
        for (i in 0 until array.length()) {
            val jo = array.getJSONObject(i)
            val repoJson = ReportResumeItems(jo.getString(ReportConstants.ITEM.TITLE),
                    jo.getString(ReportConstants.ITEM.DESCRIPTION),
                    jo.getString(ReportConstants.ITEM.CONFORMITY),
                    jo.getString(ReportConstants.ITEM.NOTE),
                    jo.getString(ReportConstants.ITEM.PHOTO))
            repo.add(repoJson)
        }
        _resumeList.value = ResumeState.Success(repo)
    }
}