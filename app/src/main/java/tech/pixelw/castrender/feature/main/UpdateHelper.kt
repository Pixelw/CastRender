package tech.pixelw.castrender.feature.main

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.text.Html
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.pixelw.castrender.BuildConfig
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.R
import tech.pixelw.castrender.utils.ApkUtil
import tech.pixelw.castrender.utils.LogUtil
import kotlin.random.Random


object UpdateHelper {

    private var downloadApkId: Long? = null
    private val mDownloadManager by lazy { CastRenderApp.appContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager }

    fun check(coroutineScope: CoroutineScope, activity: Activity) {
        try {
            checkInternal(coroutineScope, activity)
        } catch (t: Throwable) {
            LogUtil.e(TAG, "check Update failed", t)
        }
    }

    private fun checkInternal(coroutineScope: CoroutineScope, activity: Activity) {
        coroutineScope.launch(Dispatchers.Main) {
            val result = kotlin.runCatching {
                UpdateApi.INSTANCE.getUpdate()
            }.onFailure {
                LogUtil.e(TAG, "error on check updates", it)
            }
            val appVersion = result.getOrNull() ?: return@launch
            if (appVersion.versionCode <= BuildConfig.VERSION_CODE) {
                return@launch
            }
            val abi = matchBestAbi(appVersion.abis)
            if (appVersion.minSdk > Build.VERSION.SDK_INT || abi == null) {
                Toast.makeText(activity, R.string.device_doesnt_support, Toast.LENGTH_LONG).show();
                return@launch
            }

            val s = "<b>${appVersion.versionName}</b><br>${appVersion.changeLog}"
            val updateDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.update_available)
                .setMessage(Html.fromHtml(s))
                .setCancelable(true)
                .setNegativeButton(R.string.later) { _, _ -> }
            if (appVersion.url.isNotEmpty()) {
                updateDialog.setPositiveButton(R.string.download) { _, _ ->
                    downloadAndInstallApk(appVersion, activity, abi)
                }
            }
            if (appVersion.storeUrls?.isNotEmpty() == true) {
                updateDialog.setNeutralButton(R.string.store) { _, _ ->
                    appVersion.storeUrls.first().let { activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }
                }
            }
            updateDialog.show()
        }
    }

    private fun matchBestAbi(abis: List<String>): String? {
        if (abis.size == 1 && AppUpdate.ABI_UNIVERSAL == abis.first()) {
            return AppUpdate.ABI_UNIVERSAL
        }
        if (abis.contains(Build.CPU_ABI)) {
            return Build.CPU_ABI
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS.forEach { cpu ->
                abis.forEach {
                    if (cpu == it) return it
                }
            }
        }
        return null
    }

    private fun downloadAndInstallApk(appVersion: AppUpdate, context: Context, targetAbi: String) {
        val set = mutableSetOf<String>()
        set.add(appVersion.url)
        if (appVersion.alternativeApkUrl?.isNotEmpty() == true) {
            set.addAll(appVersion.alternativeApkUrl)
        }
        val url: String = getRandomUrl(set).replace(AppUpdate.PLACEHOLDER_ABI, targetAbi)
        context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        downloadApkId = downloadFile(mDownloadManager, url, "Update")
        Toast.makeText(context, R.string.update_prompt, Toast.LENGTH_LONG).show()
    }

    private fun getRandomUrl(set: MutableSet<String>): String {
        if (set.size == 1) return set.elementAt(0)
        val i = Random.nextInt(set.size)
        return set.elementAt(i)
    }

    private fun downloadFile(mDownloadManager: DownloadManager, url: String, title: String): Long {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDescription(title)
        return mDownloadManager.enqueue(request)
    }

    private val receiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    try {
                        downloadApkId?.let {
                            val uriForDownloadedFile: Uri? = mDownloadManager.getUriForDownloadedFile(it)
                            require(uriForDownloadedFile != null) { "null apk uri for install" }
                            ApkUtil.install(uriForDownloadedFile, context)
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, "Updated failed, and we reported this problem", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                    context.unregisterReceiver(this)
                }
            }
        }
    }

    private const val TAG = "UpdateHelper"

}