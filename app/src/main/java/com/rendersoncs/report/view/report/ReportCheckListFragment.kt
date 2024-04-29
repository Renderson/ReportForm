package com.rendersoncs.report.view.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.rendersoncs.report.BuildConfig
import com.rendersoncs.report.R
import com.rendersoncs.report.common.animated.animatedView
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.util.*
import com.rendersoncs.report.databinding.FragmentReportCheckListBinding
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.model.ReportDetailPhoto
import com.rendersoncs.report.model.ReportItems
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.cameraX.CameraXMainActivity
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
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

    private var mInterstitialAd: InterstitialAd? = null
    private val reportItems = ArrayList<ReportItems>()
    private val mKeys = ArrayList<String?>()

    private val listTitle = ArrayList<String?>()
    private val listDescription = ArrayList<String?>()
    private val listRadio = ArrayList<String?>()
    private val listNotes = ArrayList<String?>()
    private val listPhoto = ArrayList<String?>()

    private var mAdapter = ReportCheckListAdapter(reportItems)
    private val jsonListModeOff = DownloadJson()

    private val snackBarHelper = SnackBarHelper()
    private val checkAnswerList = ReportCheckAnswer()
    private var clear = false
    private var concluded = false
    private var canFinish = false
    private var startSaveAutomatic = false

    private var databaseReference: DatabaseReference? = null
    private val user = User()

    private var mainHandler: Handler? = null

    private val automaticSave = object : Runnable {
        override fun run() {
            concluded = false
            if (startSaveAutomatic) {
                saveReport()
                toast(getString(R.string.txt_information_saved_automatically))
            }
            startSaveAutomatic = true
            mainHandler?.postDelayed(this, (1 * 60 * 1000))
        }
    }

    private val multiplePermissionNameList = if (Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val requestLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    toast(getString(R.string.label_permission_camera_granted))
                } else {
                    toast(getString(R.string.label_permission_camera_denied))
                }
            }

    private val networkChecker by lazy {
        NetworkChecker(ContextCompat.getSystemService(requireContext(), ConnectivityManager::class.java))
    }

    @SuppressLint("SourceLockedOrientationActivity")
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
        user.id = FirebaseAuth.getInstance().currentUser?.uid
        viewModel.checkReportItems.value = reportItems
        loadCheckList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contentReport.progressBar.show()
        initViews()
        setupRV()
        setObservers()
        showAdMob()
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReportCheckListBinding.inflate(inflater, container, false)

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() = with(binding) {
        this.resultCompany.text = args.report.company
        this.resultEmail.text = args.report.email
        this.resultDate.text = args.report.date
        resultController = args.report.controller.orEmpty()

        this.contentReport.coordinator.isEnabled = false

        this.contentReport.contentFloating.fabNewItem.setOnClickListener {
            stateNormalFloatingButtons()
            createItemCheckList()
        }

        this.contentReport.contentFloating.fabSave.setOnClickListener {
            stateNormalFloatingButtons()
            checkListToSave()
        }

        this.contentReport.contentFloating.fabMenu.setOnClickListener {
            if (this.contentReport.contentFloating.fabNewItem.isExtended) {
                this.contentReport.contentFloating.fabNewItem.shrink()
            }
            this.contentReport.contentFloating.fabNewItem.shrink()
            this.contentReport.contentFloating.fabSave.show()
            this.contentReport.contentFloating.fabMenu.visibility = View.GONE
            this.contentReport.contentFloating.txtSave.show()
            this.contentReport.contentFloating.txtCreateItem.show()
            this.contentReport.contentFloating.viewLine.hide()
            this.contentReport.showScore.hide()
            this.contentReport.coordinator.isEnabled = true

            this.contentReport.coordinator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opacity90))
        }

        this.contentReport.coordinator.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    stateNormalFloatingButtons()
                }
            }
            true
        }

        binding.contentReport.emptyViewReport.actionAddItem.setOnClickListener {
            createItemCheckList()
        }

        this.contentReport.progressBar.visibility = View.GONE
    }

    private fun createItemCheckList() {
        CommonEditDialog(requireContext())
            .showEditTextDialog(
                msg = {
                    toast(resources.getString(R.string.txt_new_item_list))
                },
                error = {
                    toast(resources.getString(R.string.label_error_update_list))
                },
            )
    }

    private fun setupRV() = with(binding) {
        recyclerView = contentReport.recyclerViewForm.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        mAdapter.setOnItemListenerClicked(this@ReportCheckListFragment)
        animatedView(recyclerView, this.contentReport.showScore, contentReport.contentFloating.fabNewItem)
    }

    private fun FragmentReportCheckListBinding.stateNormalFloatingButtons() {
        this.contentReport.contentFloating.fabSave.hide()
        this.contentReport.contentFloating.fabMenu.visibility = View.VISIBLE
        this.contentReport.contentFloating.txtSave.hide()
        this.contentReport.contentFloating.txtCreateItem.hide()
        this.contentReport.contentFloating.viewLine.show()
        this.contentReport.coordinator.isEnabled = false

        this.contentReport.coordinator.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )
    }

    private fun loadCheckList() {
        networkChecker.performActionIfConnected {
            checkListFireBase()
        }

        networkChecker.performActionNoConnected {
            checkListLocal()
        }
    }

    private fun checkListFireBase() {
        databaseReference = user.id?.let { id ->
            LibraryClass.getFirebase()?.child(ReportConstants.FIREBASE.FIRE_USERS)
                    ?.child(id)?.child(ReportConstants.FIREBASE.FIRE_LIST)
        }

        databaseReference?.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.getValue(ReportItems::class.java)?.let { reportItems.add(it) }
                val key = dataSnapshot.key
                mKeys.add(key)
                viewModel.checkReportItems.value = reportItems
                notifyDataSetChanged()
                checkIfUpdateReport()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val reportItems = dataSnapshot.getValue(ReportItems::class.java)
                val key = dataSnapshot.key
                val index = mKeys.indexOf(key)

                if (reportItems != null) {
                    this@ReportCheckListFragment.reportItems[index] = reportItems
                }
                viewModel.checkReportItems.value = arrayListOf(reportItems!!)
                notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val key = dataSnapshot.key
                val index = mKeys.indexOf(key)
                reportItems.removeAt(index)
                mKeys.removeAt(index)
                viewModel.checkReportItems.value = reportItems
                notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                //toast(getString(R.string.label_failed))
            }
        })
    }

    private fun notifyDataSetChanged() = mAdapter.notifyDataSetChanged()

    private fun checkIfUpdateReport() {
        args.report.id?.let { id ->
            if (id != -1 && clear.not()) viewModel.getCheckListForEdit(id)
        }
    }

    private fun checkListLocal() {
        jsonListModeOff.addItemsFromJsonList(reportItems) {
            viewModel.checkReportItems.value = it
        }
        notifyDataSetChanged()
        checkIfUpdateReport()
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
            R.id.save -> { checkListToSave() }
            R.id.close -> closeReport()
            R.id.clear -> {
                checkAnswers()
                if (listRadio.isEmpty()) {
                    snackBarHelper.showSnackBar(requireActivity(), R.id.fab_new_item, R.string.label_empty_list)
                } else {
                    CommonDialog(requireContext()).showDialog(
                        title = getString(R.string.alert_clear_list),
                        description = getString(R.string.alert_clear_list_text),
                        buttonConfirm = getString(R.string.confirm),
                        buttonCancel = getString(R.string.cancel),
                        confirmListener = {
                            clearCheckList()
                        }
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkListToSave() {
        checkAnswers()
        when {
            listPhoto.isEmpty() -> {
                CommonDialog(requireContext()).showDialog(
                    title = getString(R.string.alert_empty_report),
                    description = getString(R.string.alert_empty_report_text),
                    buttonConfirm = getString(R.string.back),
                    confirmListener = {
                        clearList()
                    }
                )
            }
            listRadio.size > listPhoto.size -> {
                CommonDialog(requireContext()).showDialog(
                    title = getString(R.string.alert_check_list),
                    description = getString(R.string.alert_check_list_text),
                    buttonConfirm = getString(R.string.back),
                    confirmListener = {
                        clearList()
                    }
                )
            }
            else -> {
                checkPermission(REQUEST_SAVE)
            }
        }
    }

    private fun checkPermission(request: Int) {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(multiplePermissionNameList.toString())
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
        CommonDialog(requireContext()).showDialog(
            title = getString(R.string.alert_punctuation),
            description = getString(R.string.alert_punctuation_label1, resultScore) + " " +
                    listRadio.size + " " + getString(R.string.alert_punctuation_label2, this.score.text.toString()),
            buttonConfirm = getString(R.string.confirm),
            buttonCancel = getString(R.string.cancel),
            confirmListener = {
                concluded = true
                canFinish = true
                saveReport()
            }
        )
    }

    private fun saveReport() {
        binding.apply {
            val report = Report(
                company = resultCompany.text.toString(),
                email = resultEmail.text.toString(),
                date = resultDate.text.toString(),
                controller = resultController,
                score = this.contentReport.showScore.text.toString(),
                result = resultScore,
                concluded = concluded,
                userId = viewModel.userUid.value.orEmpty()
            )
            viewModel.updateReport(id = args.id, report = report)
            viewModel.savedReport.value = args.id.toLong()
        }
    }

    private fun setObservers() {
        var reportCheckList: ReportCheckList
        viewModel.savedReport.observe(viewLifecycleOwner) { reportId ->
            viewModel.deleteCheckListById(reportId.toInt())
            reportItems.forEach {
                if (it.isOpt1 || it.isOpt2 || it.isOpt3) {
                    val conformity = when (it.selectedAnswerPosition) {
                        ReportConstants.ITEM.OPT_NUM1 -> {
                            ReportConstants.ITEM.OPT_NUM1
                        }

                        ReportConstants.ITEM.OPT_NUM2 -> {
                            ReportConstants.ITEM.OPT_NUM2
                        }

                        else -> {
                            ReportConstants.ITEM.OPT_NUM3
                        }
                    }
                    reportCheckList = ReportCheckList(
                        reportId = reportId.toInt(),
                        key = it.key.orEmpty(),
                        title = it.title.orEmpty(),
                        description = it.description.orEmpty(),
                        note = it.note.orEmpty(),
                        conformity = conformity,
                        photo = it.photoPath ?: ReportConstants.PHOTO.NOT_PHOTO
                    )
                    viewModel.insertCheckList(reportCheckList)
                }
            }
        }
        viewModel.savedCheckList.observe(viewLifecycleOwner) {
            if (it != null && concluded) {
                viewModel.savedReport.value?.let { reportId ->
                    viewModel.generatePDF(reportId)
                }
            } else if (canFinish) {
                navigateUp()
            }
        }
        viewModel.pdfCreated.observe(viewLifecycleOwner) { result ->
            if (result) {
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    navigateUp()
                }
            }
        }

        viewModel.reportItemsUpdate.observe(viewLifecycleOwner) { report ->
            reportItems.forEach { reportItems ->
                report.forEach { items ->
                    if (items.key == reportItems.key) {
                        reportItems.photoPath = items.photo
                        reportItems.note = items.note
                        when (items.selectedAnswerPosition) {
                            1 -> radioItemChecked(reportItems, 1)
                            2 -> radioItemChecked(reportItems, 2)
                            3 -> radioItemChecked(reportItems, 3)
                        }
                    }
                }
            }
            notifyDataSetChanged()
        }

        viewModel.checkReportItems.observe(viewLifecycleOwner) {
            binding.contentReport.progressBar.hide()
            if (it.isEmpty()) {
                binding.contentReport.emptyViewReport.layoutReportListEmpty.show()
                binding.contentReport.recyclerViewForm.hide()
            } else {
                binding.contentReport.emptyViewReport.layoutReportListEmpty.hide()
                binding.contentReport.recyclerViewForm.show()
            }
        }
    }

    private fun showAdMob() {
        val admobId = if (BuildConfig.BUILD_TYPE != "release") {
            ReportConstants.ADMOB.ADMOB_HLG
        } else {
            ReportConstants.ADMOB.ADMOB_PROD
        }

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            admobId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd

                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                findNavController().navigateUp()
                            }
                        }
                }
            }
        )
    }

    private fun closeReport() {
        CommonDialog(requireContext()).showDialog(
            title = getString(R.string.alert_leave_the_report),
            description = getString(R.string.alert_leave_the_report_text),
            buttonConfirm = getString(R.string.confirm),
            buttonCancel = getString(R.string.cancel),
            confirmListener = {
                closeMethods()
                navigateUp()
            }
        )
    }

    private fun closeMethods() {
        if (databaseReference != null)
            databaseReference = LibraryClass.closeFireBase()
    }

    private fun navigateUp() {
        findNavController().navigateUp()
    }

    // Clear every Lists and reload Adapter
    private fun clearCheckList() = with(binding) {
        reportItems.clear()
        viewModel.calculateScore(reportItems)
        clear = true
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
        notifyDataSetChanged()
    }

    override fun takePhoto(reportItems: ReportItems) {
        reportPosition = reportItems
        CommonBottomSheet(requireContext()).showBottomSheet(
            cameraListener = {
                openCamera()
            },
            galleryListener = {
                checkPermission(REQUEST_GALLERY)
            }
        )
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
        CommonEditDialog(requireContext())
            .showEditTextDialog(
                title = reportItems.title ?: "",
                description = reportItems.description ?: "",
                key = reportItems.key ?: "",
                msg = { toast("Item atualizado!") },
            )
    }

    override fun removeItem(reportItems: ReportItems) {
        CommonDialog(requireContext()).showDialog(
            title = getString(R.string.alert_remove_report),
            description = getString(R.string.label_remove_item_list),
            buttonConfirm = getString(R.string.confirm),
            buttonCancel = getString(R.string.cancel),
            confirmListener = {
                postRemoveItem(reportItems)
            }
        )
    }

    private fun postRemoveItem(reportItems: ReportItems) {
        viewModel.removeItem(reportItems, databaseReference)
    }

    override fun resetItem(reportItems: ReportItems, position: Int) {
        mAdapter.setImageInItem(reportItems, null)
        mAdapter.insertNote(reportItems, null)
        reportItems.isOpt1 = false
        reportItems.isOpt2 = false
        reportItems.isOpt3 = false
        viewModel.calculateScore(this.reportItems)
        mAdapter.notifyItemChanged(position)
    }

    private fun openCamera() {
        val intent = Intent(requireContext(), CameraXMainActivity::class.java)
        launchCamera.launch(intent)
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

    private fun showScore(score: Float)  = with(binding) {
        resultScore = if (score >= 5.0) {
            getString(R.string.according).uppercase(Locale.ROOT)
        } else {
            getString(R.string.not_according).uppercase(Locale.ROOT)
        }
        this.score.text = score.toString()
        this.contentReport.showScore.text = getString(R.string.label_note_value_scroll, score.toString())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getReturnNote() =
            findNavController().currentBackStackEntry?.savedStateHandle
                    ?.getLiveData<String>("noteTest")?.observe(viewLifecycleOwner) { result ->
                        reportPosition.note = result
                        mAdapter.notifyDataSetChanged()
                    }

    private fun getResultScore() {
        uiStateJobScore = lifecycleScope.launchWhenResumed {
            viewModel.uiStateScore.collect { score ->
                showScore(score)
            }
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listPermissionNeeded.toTypedArray(),
                Build.VERSION_CODES.TIRAMISU
            )
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        mainHandler?.removeCallbacks(automaticSave)
    }

    override fun onResume() {
        mainHandler = Handler(Looper.getMainLooper())
        mainHandler?.post(automaticSave)

        getResultScore()
        keyboardCloseTouchListener(recyclerView)

        getReturnNote()
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>("noteTest")
        super.onResume()
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