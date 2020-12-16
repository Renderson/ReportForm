package com.rendersoncs.report.view.report

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.animated.AnimatedView
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.pdf.AsyncCreatePDF
import com.rendersoncs.report.infrastructure.util.AlertDialogUtil
import com.rendersoncs.report.infrastructure.util.DownloadJson
import com.rendersoncs.report.infrastructure.util.ReportEmptyObserve
import com.rendersoncs.report.infrastructure.util.SnackBarHelper
import com.rendersoncs.report.model.ReportDetailPhoto
import com.rendersoncs.report.model.ReportItems
import com.rendersoncs.report.repository.dao.ReportDataBaseAsyncTask
import com.rendersoncs.report.repository.dao.business.ReportBusiness
import com.rendersoncs.report.view.cameraX.CameraXMainActivity
import com.rendersoncs.report.view.fragment.BottomSheetDetailPhotoFragment
import com.rendersoncs.report.view.fragment.NewItemFireBaseFragment
import com.rendersoncs.report.view.fragment.ReportNoteFragment
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_report_list_empty.*
import kotlinx.android.synthetic.main.content_report.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class ReportActivity : AppCompatActivity(), ReportListener {

    private lateinit var recyclerView: RecyclerView

    private var mReportBusiness: ReportBusiness? = null
    private val jsonListModeOff = DownloadJson()
    private var reportTakePhoto: ReportItems? = null
    private var mAdapter: ReportAdapter? = null
    private var emptyLayout: View? = null

    private var resultCompany: TextView? = null
    private var resultEmail: TextView? = null
    private var resultDate: TextView? = null
    private var showScore: TextView? = null
    private val pdfCreateAsync = AsyncCreatePDF(this@ReportActivity)

    private val reportItems = ArrayList<ReportItems>()
    private val mKeys = ArrayList<String?>()

    private val listTitle = ArrayList<String?>()
    private val listDescription = ArrayList<String?>()
    private val listRadio = ArrayList<String?>()
    private val listNotes = ArrayList<String?>()
    private val listPhoto = ArrayList<String?>()
    private val jsArray = JSONArray()

    private var scores: TextView? = null
    private var resultScore = ""
    private var resultController: String? = ""
    private var mReportId = 0

    private var databaseReference: DatabaseReference? = null
    private var mFireBaseAnalytics: FirebaseAnalytics? = null
    private val user = User()

    private val snackBarHelper = SnackBarHelper()
    private val checkAnswerList = ReportCheckAnswer()
    private val animated = AnimatedView()
    private var dialog: AlertDialog? = null
    private val alertDialog = AlertDialogUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        setSupportActionBar(toolbar)
        setTitle(R.string.title_report)

        progressBar.visibility = View.VISIBLE

        val mAuth = FirebaseAuth.getInstance()
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this)
        user.id = mAuth.currentUser!!.uid

        mReportBusiness = ReportBusiness(this)

        initViews()

        loadListFire()
    }

    private fun initViews() {
        resultCompany = findViewById(R.id.result_company)
        resultEmail = findViewById(R.id.result_email)
        resultDate = findViewById(R.id.result_date)
        scores = findViewById(R.id.score)
        showScore = findViewById(R.id.showScore)
        emptyLayout = findViewById(R.id.layout_report_list_empty)

        mAdapter = ReportAdapter(reportItems)

        recyclerView = recycler_view_form.apply {
            layoutManager = LinearLayoutManager(applicationContext,
                    RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }

        mAdapter!!.setOnItemListenerClicked(this)

        fab_new_item.setOnClickListener { startNewItemListFireBase() }
        action_add_item.setOnClickListener { startNewItemListFireBase() }

        // Animated View
        animated.animatedView(recyclerView, showScore!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun keyboardCloseTouchListener() {
        recyclerView.setOnTouchListener { v: View, _: MotionEvent? ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    private fun loadListFire() {
        this.isConnected
        this.bundleReportFromDialog
    }

    // get text from DialogFragment
    private val bundleReportFromDialog: Unit
        get() {
            // get text from DialogFragment
            val intent = intent
            val bundle = intent.extras!!

            val company = bundle.getString(ReportConstants.ITEM.COMPANY)
            resultCompany!!.text = company

            val email = bundle.getString(ReportConstants.ITEM.EMAIL)
            resultEmail!!.text = email

            resultController = bundle.getString(ReportConstants.ITEM.CONTROLLER)
            val date = bundle.getString(ReportConstants.ITEM.DATE)

            resultDate!!.text = date
        }

    private fun startNewItemListFireBase() {
        val newItemListFirebase = NewItemFireBaseFragment()
        newItemListFirebase.show(supportFragmentManager, newItemListFirebase.tag)
    }

    private val isConnected: Unit
        get() {
            val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false

                    if (connected) {
                        addItemsFromFireBase()
                    } else {
                        addItemsFromListOFF()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,
                            getString(R.string.label_error_return), Toast.LENGTH_SHORT).show()
                }
            })
        }

    private fun addItemsFromListOFF() {
        progressBar.visibility = View.GONE
        action_add_item.visibility = View.GONE
        fab_new_item.visibility = View.GONE
        jsonListModeOff.addItemsFromJsonList(reportItems)
        mAdapter!!.notifyDataSetChanged()
    }

    // Sync RecyclerView with FireBase
    private fun addItemsFromFireBase() {
        fab_new_item.visibility = View.VISIBLE

        databaseReference = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS)
                .child(user.id).child(ReportConstants.FIREBASE.FIRE_LIST)

        databaseReference!!.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                progressBar.visibility = View.GONE
                dataSnapshot.getValue(ReportItems::class.java)?.let { reportItems.add(it) }
                val key = dataSnapshot.key
                mKeys.add(key)

                mAdapter!!.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val reportItems = dataSnapshot.getValue(ReportItems::class.java)
                val key = dataSnapshot.key
                val index = mKeys.indexOf(key)

                if (reportItems != null) {
                    this@ReportActivity.reportItems[index] = reportItems
                }
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val key = dataSnapshot.key
                val index = mKeys.indexOf(key)
                reportItems.removeAt(index)
                mKeys.removeAt(index)
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, getString(R.string.label_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLayoutEmpty() {
        progressBar.visibility = View.GONE
        mAdapter!!.registerAdapterDataObserver(ReportEmptyObserve(recyclerView, emptyLayout))
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
                    alertDialog.showDialog(this@ReportActivity,
                            resources.getString(R.string.alert_empty_report),
                            resources.getString(R.string.alert_empty_report_text),
                            resources.getString(R.string.back),
                            { _: DialogInterface?, _: Int -> },
                            null, null, false)
                    clearList()
                } else if (listRadio.size > listPhoto.size) {
                    alertDialog.showDialog(this@ReportActivity,
                            resources.getString(R.string.alert_check_list),
                            resources.getString(R.string.alert_check_list_text),
                            resources.getString(R.string.back),
                            { _: DialogInterface?, _: Int -> },
                            null, null, false)
                    clearList()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(this@ReportActivity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@ReportActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
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
                    snackBarHelper.showSnackBar(this@ReportActivity, R.id.fab_new_item, R.string.label_empty_list)
                } else {
                    alertDialog.showDialog(this@ReportActivity,
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
        alertDialog.showDialogScore(this@ReportActivity,
                resources.getString(R.string.alert_punctuation),
                resources.getString(R.string.alert_punctuation_label1, resultScore) + " " +
                        listRadio.size + " " +
                        resources.getString(R.string.alert_punctuation_label2, scores!!.text.toString()),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int -> handleSave() },
                resources.getString(R.string.cancel), { _: DialogInterface?, _: Int -> clearList() }, false)
    }

    private fun alertDialogClose() {
        alertDialog.showDialog(this@ReportActivity,
                resources.getString(R.string.alert_leave_the_report),
                resources.getString(R.string.alert_leave_the_report_text),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int ->
                    /*Intent intent = new Intent(this, SignatureActivityMain.class);
                    startActivity(intent);*/
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
        ReportDataBaseAsyncTask(this@ReportActivity,
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
        isConnected
        snackBarHelper.showSnackBar(this@ReportActivity, R.id.fab_new_item, R.string.label_empty_list)
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
                checkAnswerList.checkAnswerNote(this@ReportActivity, i, reportItems, listNotes)
                checkAnswerList.checkAnswerRadiosButtons(this@ReportActivity, i, reportItems, listRadio)
                checkAnswerList.checkAnswerPhoto(i, reportItems, listPhoto)
            }
        }
    }

    // Update Item List FireBase
    override fun updateList(reportItems: ReportItems) {
        val nFrag = NewItemFireBaseFragment()
        val bundle = Bundle()
        bundle.putString(ReportConstants.ITEM.TITLE, reportItems.title)
        bundle.putString(ReportConstants.ITEM.DESCRIPTION, reportItems.description)
        bundle.putString(ReportConstants.ITEM.KEY, reportItems.key)
        nFrag.arguments = bundle
        nFrag.show(this@ReportActivity.supportFragmentManager, nFrag.tag)
    }

    // Remove Item List FireBase
    override fun removeItem(reportItems: ReportItems) {
        alertDialog.showDialog(this@ReportActivity,
                resources.getString(R.string.remove),
                resources.getString(R.string.label_remove_item_list),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int ->
                    val query = databaseReference!!.orderByChild(ReportConstants.ITEM.KEY)
                            .equalTo(reportItems.key)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds in dataSnapshot.children) {
                                ds.ref.removeValue()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(applicationContext, resources
                                    .getString(R.string.label_error_update_list), Toast.LENGTH_SHORT).show()
                        }
                    })
                },
                resources.getString(R.string.cancel), null, false)
    }

    // Insert Note
    override fun insertNote(reportItems: ReportItems) {
        val fragNote = ReportNoteFragment(mAdapter!!, reportItems)
        val bundle = Bundle()
        bundle.putString(ReportConstants.ITEM.NOTE, reportItems.note)
        fragNote.arguments = bundle
        fragNote.show(this@ReportActivity.supportFragmentManager, fragNote.tag)
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

    // OPen Camera
    override fun takePhoto(reportItems: ReportItems) {
        reportTakePhoto = reportItems
        val items = arrayOf(
                this.getString(R.string.msg_take_image),
                this.getString(R.string.msg_select_from_gallery)
        )
        val builder = AlertDialog.Builder(this@ReportActivity)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_READ_WHITE) {
            if (grantResults.size > REQUEST_PERMISSIONS && grantResults[REQUEST_PERMISSIONS] == PackageManager.PERMISSION_GRANTED && grantResults[REQUEST_PERMISSIONS_GRANTED] == PackageManager.PERMISSION_GRANTED) {
                handleSave()
            } else {
                snackBarHelper.showSnackBar(this, R.id.fab_new_item, R.string.label_permission_read)
            }
        }
    }

    private fun closeMethods() {
        if (mReportBusiness != null) {
            mReportBusiness!!.close()
        }
        databaseReference = LibraryClass.closeFireBase()
    }

    override fun onBackPressed() {
        alertDialogClose()
    }

    override fun onResume() {
        super.onResume()
        keyboardCloseTouchListener()
        if (reportItems.isEmpty()) {
            showLayoutEmpty()
        }
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
}