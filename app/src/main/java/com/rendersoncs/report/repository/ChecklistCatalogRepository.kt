package com.rendersoncs.report.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.util.ReportFiles
import com.rendersoncs.report.ui.login.util.LibraryClass
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class ChecklistCatalogItem(
    val key: String,
    val title: String,
    val description: String
)

@Singleton
class ChecklistCatalogRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val cacheMutex = Mutex()

    suspend fun loadCatalog(): List<ChecklistCatalogItem> {
        val uid = firebaseAuth.currentUser?.uid ?: return emptyList()
        return withContext(Dispatchers.IO) {
            if (hasInternet()) {
                try {
                    val remote = fetchRemote(uid)
                    persistCache(uid, remote)
                    remote
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    readCache(uid)
                }
            } else {
                readCache(uid)
            }
        }
    }

    suspend fun addItem(title: String, description: String): ChecklistCatalogItem {
        return withContext(Dispatchers.IO) {
            val uid = requireUid()
            val ref = listRef(uid).push()
            val key = ref.key ?: throw IllegalStateException("Unable to create item")
            val item = ChecklistCatalogItem(key = key, title = title, description = description)
            ref.setValue(
                mapOf(
                    ReportConstants.ITEM.TITLE to title,
                    ReportConstants.ITEM.DESCRIPTION to description,
                    ReportConstants.ITEM.KEY to key
                )
            ).await()
            upsertCache(uid, item)
            item
        }
    }

    suspend fun updateItem(key: String, title: String, description: String) {
        withContext(Dispatchers.IO) {
            val uid = requireUid()
            val snapshot = listRef(uid).orderByChild(ReportConstants.ITEM.KEY).equalTo(key).get().await()
            snapshot.children.forEach { child ->
                child.ref.child(ReportConstants.ITEM.TITLE).setValue(title)
                child.ref.child(ReportConstants.ITEM.DESCRIPTION).setValue(description)
            }
            upsertCache(uid, ChecklistCatalogItem(key = key, title = title, description = description))
        }
    }

    suspend fun removeItem(key: String) {
        withContext(Dispatchers.IO) {
            val uid = requireUid()
            val snapshot = listRef(uid).orderByChild(ReportConstants.ITEM.KEY).equalTo(key).get().await()
            snapshot.children.forEach { child -> child.ref.removeValue() }
            cacheMutex.withLock {
                persistCacheUnlocked(uid, readCacheUnlocked(uid).filterNot { it.key == key })
            }
        }
    }

    fun hasInternet(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    private suspend fun fetchRemote(uid: String): List<ChecklistCatalogItem> {
        val snapshot = listRef(uid).get().await()
        return snapshot.children.mapNotNull { child ->
            val title = child.child(ReportConstants.ITEM.TITLE).getValue(String::class.java).orEmpty()
            if (title.isBlank()) return@mapNotNull null
            ChecklistCatalogItem(
                key = child.child(ReportConstants.ITEM.KEY).getValue(String::class.java)
                    ?: child.key.orEmpty(),
                title = title,
                description = child.child(ReportConstants.ITEM.DESCRIPTION)
                    .getValue(String::class.java)
                    .orEmpty()
            )
        }
    }

    private suspend fun persistCache(uid: String, items: List<ChecklistCatalogItem>) {
        cacheMutex.withLock { persistCacheUnlocked(uid, items) }
    }

    private suspend fun readCache(uid: String): List<ChecklistCatalogItem> {
        return cacheMutex.withLock { readCacheUnlocked(uid) }
    }

    private suspend fun upsertCache(uid: String, item: ChecklistCatalogItem) {
        cacheMutex.withLock {
            val merged = readCacheUnlocked(uid)
                .associateBy { it.key }
                .toMutableMap()
            merged[item.key] = item
            persistCacheUnlocked(uid, merged.values.toList())
        }
    }

    private fun persistCacheUnlocked(uid: String, items: List<ChecklistCatalogItem>) {
        val list = JSONObject()
        items.forEach { item ->
            list.put(
                item.key,
                JSONObject()
                    .put(ReportConstants.ITEM.KEY, item.key)
                    .put(ReportConstants.ITEM.TITLE, item.title)
                    .put(ReportConstants.ITEM.DESCRIPTION, item.description)
            )
        }
        ReportFiles.checklistJson(context, uid).writeText(
            JSONObject().put("list", list).toString()
        )
    }

    private fun readCacheUnlocked(uid: String): List<ChecklistCatalogItem> {
        val file = ReportFiles.checklistJson(context, uid)
        if (!file.exists()) return emptyList()
        return try {
            val root = JSONObject(file.readText())
            val list = root.optJSONObject("list") ?: return emptyList()
            val items = mutableListOf<ChecklistCatalogItem>()
            val keys = list.keys()
            while (keys.hasNext()) {
                val dynamicKey = keys.next()
                val obj = list.optJSONObject(dynamicKey) ?: continue
                val title = obj.optString(ReportConstants.ITEM.TITLE)
                if (title.isBlank()) continue
                items.add(
                    ChecklistCatalogItem(
                        key = obj.optString(ReportConstants.ITEM.KEY, dynamicKey),
                        title = title,
                        description = obj.optString(ReportConstants.ITEM.DESCRIPTION)
                    )
                )
            }
            items
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    private fun listRef(uid: String) = LibraryClass.getFirebase()
        .child(ReportConstants.FIREBASE.FIRE_USERS)
        .child(uid)
        .child(ReportConstants.FIREBASE.FIRE_LIST)

    private fun requireUid(): String {
        return firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not found")
    }
}
