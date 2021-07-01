/*
package com.rendersoncs.report.view.edit

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.pdf.AsyncCreatePDF
import com.rendersoncs.report.infrastructure.util.AlertDialogUtil
import com.rendersoncs.report.infrastructure.util.DownloadJson
import com.rendersoncs.report.infrastructure.util.SnackBarHelper
import com.rendersoncs.report.model.ReportDetailPhoto
import com.rendersoncs.report.model.ReportItems
import com.rendersoncs.report.repository.dao.ReportDataBaseAsyncTask
import com.rendersoncs.report.repository.dao.business.ReportBusiness
import com.rendersoncs.report.view.cameraX.CameraXMainActivity
import com.rendersoncs.report.view.fragment.BottomSheetDetailPhotoFragment
import com.rendersoncs.report.view.report.ReportAdapter
import com.rendersoncs.report.view.report.ReportCheckAnswer
import com.rendersoncs.report.view.report.ReportListener
import kotlinx.android.synthetic.main.activity_report_list_empty.*
import kotlinx.android.synthetic.main.content_report.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class ReportEdit: AppCompatActivity(), ReportListener {

    private lateinit var recyclerView: RecyclerView

    private var mReportBusiness: ReportBusiness? = null
    private val jsonListModeOff = DownloadJson()
    private var reportTakePhoto: ReportItems? = null
    private var mAdapter: ReportAdapter? = null

    private var resultCompany: TextView? = null
    private var resultEmail: TextView? = null
    private var resultDate: TextView? = null
    private var showScore: TextView? = null
    private val pdfCreateAsync = AsyncCreatePDF(this)

    private val reportItems = ArrayList<ReportItems>()

    private val listTitle = ArrayList<String?>()
    private val listDescription = ArrayList<String?>()
    private val listRadio = ArrayList<String?>()
    private val listNotes = ArrayList<String?>()
    private val listPhoto = ArrayList<String?>()
    private val jsArray = JSONArray()

    private val editConformity = ArrayList<String>()
    private val editNotes = ArrayList<String>()
    private val editPhoto = ArrayList<String>()

    private var scores: TextView? = null
    private var resultScore = ""
    private var resultController: String? = ""
    private var mReportId = 0

    private var mFireBaseAnalytics: FirebaseAnalytics? = null

    private val snackBarHelper = SnackBarHelper()
    private val checkAnswerList = ReportCheckAnswer()
    //private val animated = AnimatedView()
    private var dialog: AlertDialog? = null
    private val alertDialog = AlertDialogUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_report)

        //setSupportActionBar(toolbar)
        title = getString(R.string.title_edit_report)

        progressBar.visibility = View.VISIBLE

        mReportBusiness = ReportBusiness(this)

        initViews()

        val bundle = intent.extras
        if (bundle != null) {
            mReportId = bundle.getInt(ReportConstants.REPORT.REPORT_ID)
        }

        loadEditReport()
    }

    private fun initViews() {
        resultCompany = findViewById(R.id.result_company)
        resultEmail = findViewById(R.id.result_email)
        resultDate = findViewById(R.id.result_date)
        scores = findViewById(R.id.score)
        showScore = findViewById(R.id.showScore)

        mAdapter = ReportAdapter(reportItems)

        recyclerView = recycler_view_form.apply {
            layoutManager = LinearLayoutManager(applicationContext,
                    RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }

        mAdapter!!.setOnItemListenerClicked(this)

        // Animated View
        //animated.animatedView(recyclerView, showScore!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun keyboardCloseTouchListener() {
        recyclerView.setOnTouchListener { v: View, _: MotionEvent? ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    private fun loadEditReport() {

        progressBar.visibility = View.GONE
        fab_new_item.visibility = View.GONE

        val bundle = intent.extras

        if (bundle != null) {
            mReportId = bundle.getInt(ReportConstants.REPORT.REPORT_ID)
            val repoEntity = mReportBusiness!!.load(mReportId)
            resultCompany!!.text = repoEntity.company
            resultEmail!!.text = repoEntity.email
            resultDate!!.text = repoEntity.date
            resultController = repoEntity.controller

            try {
                val array = JSONArray(repoEntity.listJson)
                for (i in 0 until array.length()) {
                    val `object` = array.getJSONObject(i)
                    val conformity = `object`.getString(ReportConstants.ITEM.CONFORMITY)
                    editConformity.add(conformity)

                    val note = `object`.getString(ReportConstants.ITEM.NOTE)
                    editNotes.add(note)

                    val photo = `object`.getString(ReportConstants.ITEM.PHOTO)
                    editPhoto.add(photo)

                    val repoJson = ReportItems(`object`.getString(ReportConstants.ITEM.TITLE),
                            `object`.getString(ReportConstants.ITEM.DESCRIPTION),
                            null, null)

                    reportItems.add(repoJson)
                }
                for (i in editConformity.indices) {
                    if (editConformity[i] == resources.getString(R.string.according)) {
                        radioItemChecked(reportItems[i], ReportConstants.ITEM.OPT_NUM1)
                    }
                    if (editConformity[i] == resources.getString(R.string.not_applicable)) {
                        radioItemChecked(reportItems[i], ReportConstants.ITEM.OPT_NUM2)
                    }
                    if (editConformity[i] == resources.getString(R.string.not_according)) {
                        radioItemChecked(reportItems[i], ReportConstants.ITEM.OPT_NUM3)
                    }
                }
                for (i in editNotes.indices) {
                    val notes = editNotes[i]
                    if (notes == getString(R.string.label_not_observation)) {
                        mAdapter!!.insertNote(reportItems[i], null)
                    } else {
                        mAdapter!!.insertNote(reportItems[i], notes)
                    }
                }
                for (i in editPhoto.indices) {
                    val photos = editPhoto[i]
                    mAdapter!!.setImageInItem(reportItems[i], photos)
                }
                mAdapter = ReportAdapter(reportItems)
                mAdapter!!.setOnItemListenerClicked(this)
                recyclerView.adapter = mAdapter

            } catch (e: JSONException) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addItemsFromListOFF() {
        progressBar.visibility = View.GONE
        action_add_item.visibility = View.GONE
        fab_new_item.visibility = View.GONE
        jsonListModeOff.addItemsFromJsonList(reportItems)
        mAdapter!!.notifyDataSetChanged()
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_report_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.label_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mAdapter!!.filter.filter(query)
                return false
            }
        })
        searchView.isIconified = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> return true
            R.id.save -> {
                checkAnswers()
                if (listPhoto.isEmpty()) {
                    alertDialog.showDialog(this,
                            resources.getString(R.string.alert_empty_report),
                            resources.getString(R.string.alert_empty_report_text),
                            resources.getString(R.string.back),
                            { _: DialogInterface?, _: Int -> },
                            null, null, false)
                    clearList()
                } else if (listRadio.size > listPhoto.size) {
                    alertDialog.showDialog(this,
                            resources.getString(R.string.alert_check_list),
                            resources.getString(R.string.alert_check_list_text),
                            resources.getString(R.string.back),
                            { _: DialogInterface?, _: Int -> },
                            null, null, false)
                    clearList()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS_READ_WHITE)
                        } else {
                            alertDialogScore()
                        }
                    } else {
                        alertDialogScore()
                    }
                }
            }
            R.id.close -> alertDialogClose()
            R.id.clear -> {
                checkAnswers()
                if (listRadio.isEmpty()) {
                    snackBarHelper.showSnackBar(this, R.id.fab_new_item, R.string.label_empty_list)
                } else {
                    alertDialog.showDialog(this,
                            resources.getString(R.string.alert_clear_list),
                            resources.getString(R.string.alert_clear_list_text),
                            resources.getString(R.string.confirm),
                            { _: DialogInterface?, _: Int -> clearCheckList() },
                            resources.getString(R.string.cancel), null, false)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun alertDialogScore() {
        alertDialog.showDialogScore(this,
                resources.getString(R.string.alert_punctuation),
                resources.getString(R.string.alert_punctuation_label1, resultScore) + " " +
                        listRadio.size + " " +
                        resources.getString(R.string.alert_punctuation_label2, scores!!.text.toString()),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int -> handleSave() },
                resources.getString(R.string.cancel), { _: DialogInterface?, _: Int -> clearList() }, false)
    }

    private fun alertDialogClose() {
        alertDialog.showDialog(this,
                resources.getString(R.string.alert_leave_the_report),
                resources.getString(R.string.alert_leave_the_report_text),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int ->
                    */
/*Intent intent = new Intent(this, SignatureActivityMain.class);
                    startActivity(intent);*//*

                    closeMethods()
                    finish()
                },
                resources.getString(R.string.cancel), null, false)
    }

    // Save Company, Email, Data, List
    private fun handleSave() {
        val reportItems = ReportItems()
        reportItems.company = resultCompany!!.text.toString()
        reportItems.email = resultEmail!!.text.toString()
        reportItems.controller = resultController
        reportItems.score = scores!!.text.toString()
        reportItems.result = resultScore
        reportItems.date = resultDate!!.text.toString()

        // Convert ArrayList in Json Object
        var i = 0
        while (i < listRadio.size
                && i < listTitle.size
                && i < listDescription.size
                && i < listNotes.size
                && i < listPhoto.size) {
            val jsObject = JSONObject()
            try {
                jsObject.put(ReportConstants.ITEM.TITLE, listTitle[i])
                jsObject.put(ReportConstants.ITEM.DESCRIPTION, listDescription[i])
                jsObject.put(ReportConstants.ITEM.CONFORMITY, listRadio[i])
                jsObject.put(ReportConstants.ITEM.NOTE, listNotes[i])
                jsObject.put(ReportConstants.ITEM.PHOTO, listPhoto[i])
            } catch (e: JSONException) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            jsArray.put(jsObject)
            i++
        }
        reportItems.listJson = jsArray.toString()
        Log.i("log", "Item: $jsArray jsArray")
        // Finish JsonObject

        // Save Report in SQLite
        ReportDataBaseAsyncTask(this,
                pdfCreateAsync,
                mReportId,
                mReportBusiness,
                reportItems,
                mFireBaseAnalytics) { finish() }.execute()
    }

    // Clear list
    private fun clearList() {
        listRadio.clear()
        listPhoto.clear()
        listTitle.clear()
        listDescription.clear()
        listNotes.clear()
    }

    // Clear every Lists and reload Adapter
    private fun clearCheckList() {
        //mAdapter.expandState.clear();
        reportItems.clear()
        clearList()
        score
        addItemsFromListOFF()
        snackBarHelper.showSnackBar(this, R.id.fab_new_item, R.string.label_empty_list)
    }

    private val score: Unit
        get() {
            val listMax = ArrayList<String>()
            listMax.clear()
            val startingPoints = 10f
            val losePoints = 0.7f
            val average = 5.0f
            for (i in reportItems.indices) {
                if (reportItems[i].isOpt3) {
                    if (reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM3) {
                        val nc = resources.getString(R.string.not_according)
                        listMax.add(nc)
                    }
                }
            }
            val listRadioMax = listMax.size
            val scoreList = listRadioMax * losePoints
            val score = startingPoints - scoreList
            resultScore = if (score >= average) {
                getString(R.string.according).toUpperCase(Locale.ROOT)
            } else {
                getString(R.string.not_according).toUpperCase(Locale.ROOT)
            }
            scores!!.text = score.toString()
            showScore!!.text = getString(R.string.label_note_value_scroll, score.toString())
        }

    override fun radioItemChecked(reportItems: ReportItems, optNum: Int) {
        reportItems.selectedAnswerPosition = optNum
        reportItems.isShine = false
        when (optNum) {
            1 -> {
                reportItems.isOpt1 = true
                score
            }
            2 -> {
                reportItems.isOpt2 = true
                score
            }
            3 -> {
                reportItems.isOpt3 = true
                score
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    private fun checkAnswers() {
        for (i in reportItems.indices) {
            if (reportItems[i].isOpt1 || reportItems[i].isOpt2 || reportItems[i].isOpt3) {
                checkAnswerList.checkAnswerList(i, reportItems, listTitle, listDescription)
                checkAnswerList.checkAnswerNote(this, i, reportItems, listNotes)
                checkAnswerList.checkAnswerRadiosButtons(this, i, reportItems, listRadio)
                checkAnswerList.checkAnswerPhoto(i, reportItems, listPhoto)
            }
        }
    }

    // Insert Note
    override fun insertNote(reportItems: ReportItems) {
        */
/*val fragNote = ReportNoteFragment(mAdapter!!, reportItems)
        val bundle = Bundle()
        bundle.putString(ReportConstants.ITEM.NOTE, reportItems.note)
        fragNote.arguments = bundle
        fragNote.show(this.supportFragmentManager, fragNote.tag)*//*

    }

    // Show Photo Full
    override fun fullPhoto(reportItems: ReportItems) {
        val detailPhoto: ReportDetailPhoto
        val fragment = BottomSheetDetailPhotoFragment()
        val bundle = Bundle()
        val photo = reportItems.photoPath
        val title = reportItems.title
        val description = reportItems.description
        val note = reportItems.note
        detailPhoto = ReportDetailPhoto(
                photo,
                title,
                description,
                note,
                ""
        )
        if (photo == null || photo == ReportConstants.PHOTO.NOT_PHOTO) {
            Toast.makeText(applicationContext, resources.getString(R.string.label_nothing_image), Toast.LENGTH_SHORT).show()
            return
        }
        bundle.putSerializable("modelDetail", detailPhoto)
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, fragment.tag)
    }

    // Reset Item Adapter
    override fun resetItem(reportItems: ReportItems) {
        mAdapter!!.setImageInItem(reportItems, null)
        mAdapter!!.insertNote(reportItems, null)
        reportItems.isOpt1 = false
        reportItems.isOpt2 = false
        reportItems.isOpt3 = false
        score
        mAdapter!!.notifyDataSetChanged()
    }

    override fun updateList(reportItems: ReportItems) {
    }

    override fun removeItem(reportItems: ReportItems) {
    }

    // OPen Camera
    override fun takePhoto(reportItems: ReportItems) {
        reportTakePhoto = reportItems
        val items = arrayOf(
                this.getString(R.string.msg_take_image),
                this.getString(R.string.msg_select_from_gallery)
        )
        val builder = AlertDialog.Builder(this)
        builder.setItems(items) { _: DialogInterface?, i: Int ->
            if (i == 0) {
                openCamera()
            } else {
                openGallery()
            }
        }
        dialog = builder.create()
        dialog!!.show()
    }

    private fun openGallery() {
        val it = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(it, ReportConstants.PHOTO.REQUEST_CODE_GALLERY)
    }

    private fun openCamera() {
        val it = Intent(this, CameraXMainActivity::class.java)
        startActivityForResult(it, ReportConstants.PHOTO.REQUEST_CAMERA_X)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_CAMERA ||
                    resultCode == ReportConstants.PHOTO.REQUEST_CAMERA_X) {

                val file = data!!.extras!![ReportConstants.PHOTO.RESULT_CAMERA_X] as File?
                mAdapter!!.setImageInItem(reportTakePhoto!!, file!!.path)
                Log.i("LOG", "CAMERA " + " " + file.path)
            }
            radioItemChecked(reportTakePhoto!!, ReportConstants.ITEM.OPT_NUM1)
        }
        if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            try {
                val file = File(getRealPathFromURI(uri))
                mAdapter!!.setImageInItem(reportTakePhoto!!, file.path)
                Log.i("LOG", "GALLERY " + " " + file.path)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var realPath = String()
        uri!!.path?.let { path ->

            val databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?
            if (path.contains("/document/image:")) { // files selected from "Documents"
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else { // files selected from all other sources, especially on Samsung devices
                databaseUri = uri
                selection = null
                selectionArgs = null
            }
            try {
                val column = "_data"
                val projection = arrayOf(column)
                val cursor = this.contentResolver.query(
                        databaseUri,
                        projection,
                        selection,
                        selectionArgs,
                        null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(column)
                        realPath = cursor.getString(columnIndex)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        return realPath
    }

    */
/*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_READ_WHITE) {
            if (grantResults.size > REQUEST_PERMISSIONS && grantResults[REQUEST_PERMISSIONS] == PackageManager.PERMISSION_GRANTED && grantResults[REQUEST_PERMISSIONS_GRANTED] == PackageManager.PERMISSION_GRANTED) {
                handleSave()
            } else {
                snackBarHelper.showSnackBar(this, R.id.fab_new_item, R.string.label_permission_read)
            }
        }
    }*//*


    private fun closeMethods() {
        if (mReportBusiness != null) {
            mReportBusiness!!.close()
        }
    }

    override fun onBackPressed() {
        alertDialogClose()
    }

    override fun onResume() {
        super.onResume()
        keyboardCloseTouchListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeMethods()
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 0
        private const val REQUEST_PERMISSIONS_GRANTED = 1
        private const val REQUEST_PERMISSIONS_READ_WHITE = 128
    }
}*/
