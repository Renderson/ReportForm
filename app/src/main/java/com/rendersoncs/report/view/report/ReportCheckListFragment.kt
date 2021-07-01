package com.rendersoncs.report.view.report

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentReportCheckListBinding
import com.rendersoncs.report.infrastructure.animated.animatedView
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.pdf.CreateReportInPDF
import com.rendersoncs.report.infrastructure.util.*
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportDetailPhoto
import com.rendersoncs.report.model.ReportItems
import com.rendersoncs.report.view.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.cameraX.CameraXMainActivity
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_list_empty.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@AndroidEntryPoint
class ReportCheckListFragment : BaseFragment<FragmentReportCheckListBinding, ReportViewModel>(), ReportListener {
    override val viewModel: ReportViewModel by activityViewModels()
    private val args: ReportCheckListFragmentArgs by navArgs()
    private var uiStateJobScore: Job? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var resultScore: String
    private lateinit var resultController: String
    private lateinit var reportPosition: ReportItems

    private val reportItems = ArrayList<ReportItems>()
    private val mKeys = ArrayList<String?>()

    private val listTitle = ArrayList<String?>()
    private val listDescription = ArrayList<String?>()
    private val listRadio = ArrayList<String?>()
    private val listNotes = ArrayList<String?>()
    private val listPhoto = ArrayList<String?>()
    private val checkList = JSONArray()

    private var mAdapter = ReportCheckListAdapter(reportItems)
    private val jsonListModeOff = DownloadJson()

    private val snackBarHelper = SnackBarHelper()
    private val checkAnswerList = ReportCheckAnswer()
    private var dialog: AlertDialog? = null
    private val alertDialog = AlertDialogUtil()

    private var databaseReference: DatabaseReference? = null
    private val user = User()

    private val requestLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted)
                    toast(getString(R.string.label_permission_camera_granted))
                else
                    toast(getString(R.string.label_permission_camera_denied))
            }

    private val networkChecker by lazy {
        NetworkChecker(ContextCompat.getSystemService(requireContext(), ConnectivityManager::class.java))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        closeReport()
                    }
                })
        user.id = FirebaseAuth.getInstance().currentUser!!.uid
        loadCheckList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupRV()
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReportCheckListBinding.inflate(inflater, container, false)

    private fun initViews() = with(binding) {
        this.resultCompany.text = args.reportNew.company
        this.resultEmail.text = args.reportNew.email
        this.resultDate.text = args.reportNew.date
        resultController = args.reportNew.controller

        this.contentReport.fabNewItem.setOnClickListener {
            createItemCheckList()
        }
        this.contentReport.progressBar.visibility = View.GONE
    }

    private fun createItemCheckList() {
        val bundle = Bundle().apply {
            putString(ReportConstants.ITEM.TITLE, "")
            putString(ReportConstants.ITEM.DESCRIPTION, "")
            putString(ReportConstants.ITEM.KEY, "")
        }
        findNavController().navigate(
                R.id.action_reportActivity_to_newItemFireBaseFragment, bundle
        )
    }

    private fun setupRV() = with(binding) {
        recyclerView = contentReport.recyclerViewForm.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        mAdapter.setOnItemListenerClicked(this@ReportCheckListFragment)
        animatedView(recyclerView, this.contentReport.showScore)
    }

    private fun loadCheckList() {
        networkChecker.performActionIfConnected {
            checkListFireBase()
        }

        networkChecker.performActionNoConnected {
            checkListLocal()
        }
            /*val connect = FirebaseDatabase.getInstance().getReference(".info/connected")
            connect.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false

                    if (connected) {
                        checkListFireBase()
                    } else {
                        checkListLocal()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toast(getString(R.string.label_error_return))
                }
            })*/
        }

    private fun checkListFireBase() {
        //this.contentReport.fabNewItem.visibility = View.VISIBLE
        //this.contentReport.progressBar.visibility = View.GONE

        databaseReference = user.id?.let { id ->
            LibraryClass.getFirebase()?.child(ReportConstants.FIREBASE.FIRE_USERS)
                    ?.child(id)?.child(ReportConstants.FIREBASE.FIRE_LIST)
        }

        databaseReference!!.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.getValue(ReportItems::class.java)?.let { reportItems.add(it) }
                val key = dataSnapshot.key
                mKeys.add(key)
                mAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val reportItems = dataSnapshot.getValue(ReportItems::class.java)
                val key = dataSnapshot.key
                val index = mKeys.indexOf(key)

                if (reportItems != null) {
                    this@ReportCheckListFragment.reportItems[index] = reportItems
                }
                mAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val key = dataSnapshot.key
                val index = mKeys.indexOf(key)
                reportItems.removeAt(index)
                mKeys.removeAt(index)
                mAdapter.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                toast(getString(R.string.label_failed))
            }
        })
    }

    private fun checkListLocal() {
        //this.contentReport.progressBar.visibility = View.GONE
        //this.contentReport.fabNewItem.visibility = View.GONE
        //action_add_item.visibility = View.GONE
        jsonListModeOff.addItemsFromJsonList(reportItems)
        mAdapter.notifyDataSetChanged()
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_report_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.label_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mAdapter.filter.filter(query)
                return false
            }
        })
        searchView.isIconified = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> return true
            R.id.save -> {
                checkAnswers()
                when {
                    listPhoto.isEmpty() -> {
                        alertDialog.showDialog(requireActivity(),
                                resources.getString(R.string.alert_empty_report),
                                resources.getString(R.string.alert_empty_report_text),
                                resources.getString(R.string.back),
                                { _: DialogInterface?, _: Int -> },
                                null, null, false)
                        clearList()
                    }
                    listRadio.size > listPhoto.size -> {
                        alertDialog.showDialog(requireActivity(),
                                resources.getString(R.string.alert_check_list),
                                resources.getString(R.string.alert_check_list_text),
                                resources.getString(R.string.back),
                                { _: DialogInterface?, _: Int -> },
                                null, null, false)
                        clearList()
                    }
                    else -> {
                        checkPermission(REQUEST_SAVE)
                    }
                }
            }
            R.id.close -> closeReport()
            R.id.clear -> {
                checkAnswers()
                if (listRadio.isEmpty()) {
                    snackBarHelper.showSnackBar(requireActivity(), R.id.fab_new_item, R.string.label_empty_list)
                } else {
                    alertDialog.showDialog(requireActivity(),
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

    private fun checkPermission(request: Int) {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        } else if (request == REQUEST_SAVE) {
            showScoreAndSave()
        } else {
            openGallery.launch("image/*")
        }
    }

    private fun checkAnswers() {
        for (i in reportItems.indices) {
            if (reportItems[i].isOpt1 || reportItems[i].isOpt2 || reportItems[i].isOpt3) {
                checkAnswerList.checkAnswerList(i, reportItems, listTitle, listDescription)
                checkAnswerList.checkAnswerNote(requireActivity(), i, reportItems, listNotes)
                checkAnswerList.checkAnswerRadiosButtons(requireActivity(), i, reportItems, listRadio)
                checkAnswerList.checkAnswerPhoto(i, reportItems, listPhoto)
            }
        }
    }

    // Clear list
    private fun clearList() {
        listRadio.clear()
        listPhoto.clear()
        listTitle.clear()
        listDescription.clear()
        listNotes.clear()
    }

    private fun showScoreAndSave() = with(binding) {
        alertDialog.showDialogScore(requireActivity(),
                resources.getString(R.string.alert_punctuation),
                resources.getString(R.string.alert_punctuation_label1, resultScore) + " " +
                        listRadio.size + " " +
                        resources.getString(R.string.alert_punctuation_label2, this.score.text.toString()),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int -> saveReport() },
                resources.getString(R.string.cancel), { _: DialogInterface?, _: Int -> clearList() }, false)
    }

    private fun saveReport() = with(binding) {
        checkList()

        val report = Report(
                company = resultCompany.text.toString(),
                email = resultEmail.text.toString(),
                date = resultDate.text.toString(),
                controller = resultController,
                score = this.contentReport.showScore.text.toString(),
                result = resultScore,
                listJson = checkList.toString()
        )
        viewModel.insertReport(report)
        CreateReportInPDF()
            .write(requireContext(), report)
        findNavController().navigateUp()
    }

    private fun checkList() = lifecycleScope.launch {
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
            checkList.put(jsObject)
            i++
        }
        Log.i("log", "Item: $checkList checkList")
    }

    private fun closeReport() {
        alertDialog.showDialog(requireActivity(),
                resources.getString(R.string.alert_leave_the_report),
                resources.getString(R.string.alert_leave_the_report_text),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int ->
                    closeMethods()
                    findNavController().navigateUp()
                },
                resources.getString(R.string.cancel), null, false)
    }

    private fun closeMethods() {
        if (databaseReference != null)
            databaseReference = LibraryClass.closeFireBase()
    }

    // Clear every Lists and reload Adapter
    private fun clearCheckList() = with(binding) {
        reportItems.clear()
        viewModel.calculateScore(reportItems)
        clearList()
        loadCheckList()
        snackBarHelper.showSnackBar(requireActivity(), R.id.fab_new_item, R.string.label_empty_list)
    }

    override fun radioItemChecked(reportItems: ReportItems, optNum: Int) {
        reportItems.selectedAnswerPosition = optNum
        reportItems.isShine = false
        when (optNum) {
            1 -> {
                reportItems.isOpt1 = true
                viewModel.calculateScore(this.reportItems)
            }
            2 -> {
                reportItems.isOpt2 = true
                viewModel.calculateScore(this.reportItems)
            }
            3 -> {
                reportItems.isOpt3 = true
                viewModel.calculateScore(this.reportItems)
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun takePhoto(reportItems: ReportItems) {
        reportPosition = reportItems
        val items = arrayOf(
                getString(R.string.msg_take_image),
                getString(R.string.msg_select_from_gallery)
        )
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(items) { _: DialogInterface?, i: Int ->
            if (i == 0) {
                openCamera()
                dialog!!.dismiss()
            } else {
                checkPermission(REQUEST_GALLERY)
                //openGallery.launch("image/*")
                dialog!!.dismiss()
            }
        }
        dialog = builder.create()
        dialog!!.show()
    }

    override fun fullPhoto(reportItems: ReportItems) {
        val detailPhoto = ReportDetailPhoto(
                reportItems.photoPath,
                reportItems.title,
                reportItems.description,
                reportItems.note,
                ""
        )

        if (reportItems.photoPath == null || reportItems.photoPath == ReportConstants.PHOTO.NOT_PHOTO) {
            toast(resources.getString(R.string.label_nothing_image))
            return
        }
        val bundle = Bundle().apply {
            putSerializable("modelDetail", detailPhoto)
        }
        findNavController().navigate(
                R.id.action_reportActivity_to_detailPhotoFragment, bundle
        )
    }

    override fun insertNote(reportItems: ReportItems) {
        reportPosition = reportItems
        val bundle = Bundle().apply {
            putString(ReportConstants.ITEM.NOTE, reportItems.note.toString())
        }
        findNavController().navigate(
                R.id.action_reportActivity_to_reportNoteFragmentV2, bundle
        )
    }

    override fun updateList(reportItems: ReportItems) {
        val bundle = Bundle().apply {
            putString(ReportConstants.ITEM.TITLE, reportItems.title)
            putString(ReportConstants.ITEM.DESCRIPTION, reportItems.description)
            putString(ReportConstants.ITEM.KEY, reportItems.key.toString())
        }
        findNavController().navigate(
                R.id.action_reportActivity_to_newItemFireBaseFragment, bundle
        )
    }

    override fun removeItem(reportItems: ReportItems) {
        alertDialog.showDialog(requireActivity(),
                resources.getString(R.string.remove),
                resources.getString(R.string.label_remove_item_list),
                resources.getString(R.string.confirm),
                { _: DialogInterface?, _: Int ->
                    viewModel.removeItem(reportItems, databaseReference)
                },
                resources.getString(R.string.cancel), null, false)

    }

    override fun resetItem(reportItems: ReportItems) {
        mAdapter.setImageInItem(reportItems, null)
        mAdapter.insertNote(reportItems, null)
        reportItems.isOpt1 = false
        reportItems.isOpt2 = false
        reportItems.isOpt3 = false
        viewModel.calculateScore(this.reportItems)
        mAdapter.notifyDataSetChanged()
    }

    private fun openCamera() {
        ActivityNavigator(requireContext())
                .createDestination().intent = Intent(requireContext(), CameraXMainActivity::class.java).apply {
            launchCamera.launch(this)
        }
    }

    private var launchCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ReportConstants.PHOTO.REQUEST_CAMERA_X) {

            val file = result.data!!.extras!![ReportConstants.PHOTO.RESULT_CAMERA_X] as File?
            reportPosition.photoPath = file!!.path
            radioItemChecked(reportPosition, ReportConstants.ITEM.OPT_NUM1)
        }
    }

    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        try {
            val file = File(getRealPathFromURI(requireContext(), uri))
            reportPosition.photoPath = file.path
            radioItemChecked(reportPosition, ReportConstants.ITEM.OPT_NUM1)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    override fun onResume() {
        super.onResume()
        getResultScore()
        keyboardCloseTouchListener(recyclerView)

        getReturnNote()
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>("noteTest")
    }

    private fun getResultScore() {
        uiStateJobScore = lifecycleScope.launchWhenResumed {
            viewModel.uiStateScore.collect { score ->
                showScore(score)
            }
        }
    }

    private fun showScore(score: Float)  = with(binding) {
        resultScore = if (score >= 5.0) {
            getString(R.string.according).toUpperCase(Locale.ROOT)
        } else {
            getString(R.string.not_according).toUpperCase(Locale.ROOT)
        }
        this.score.text = score.toString()
        this.contentReport.showScore.text = getString(R.string.label_note_value_scroll, score.toString())
    }

    private fun getReturnNote() =
            findNavController().currentBackStackEntry?.savedStateHandle
                    ?.getLiveData<String>("noteTest")?.observe(viewLifecycleOwner) { result ->
                        reportPosition.note = result
                        mAdapter.notifyDataSetChanged()
                    }

    override fun onDetach() {
        uiStateJobScore?.cancel()

        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        super.onDetach()
    }

    companion object {
        const val REQUEST_SAVE = 0
        const val REQUEST_GALLERY = 1
    }
}