package tech.pixelw.castrender.feature.main

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
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
    private val mDownloadManager by lazy { CastRenderApp.getAppContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager }

    fun check(coroutineScope: CoroutineScope) {
        try {
            checkInternal(coroutineScope)
        } catch (t: Throwable) {
            LogUtil.e(TAG, "check Update failed", t)
        }
    }

    private fun checkInternal(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main) {
            val context = CastRenderApp.getAppContext()
            val appVersion = UpdateApi.INSTANCE.getUpdate()

            if (appVersion.versionCode <= BuildConfig.VERSION_CODE) {
                return@launch
            }
            val abi = matchBestAbi(appVersion.abis)
            if (appVersion.minSdk > Build.VERSION.SDK_INT || abi == null) {
                Toast.makeText(context, R.string.device_doesnt_support, Toast.LENGTH_LONG).show();
                return@launch
            }

            val updateDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                .setTitle(R.string.update_available)
                .setMessage("${appVersion.versionName}\n${appVersion.changeLog}")
                .setCancelable(true)
                .setNegativeButton(R.string.later) { _, _ -> }
            if (appVersion.url.isNotEmpty()) {
                updateDialog.setPositiveButton(R.string.download) { _, _ ->
                    downloadAndInstallApk(appVersion, context, abi)
                }
            }
            if (appVersion.storeUrls.isNotEmpty()) {
                updateDialog.setNeutralButton(R.string.store) { _, _ ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appVersion.storeUrls[0])))
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
        if (appVersion.alternativeApkUrl.isNotEmpty()) {
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