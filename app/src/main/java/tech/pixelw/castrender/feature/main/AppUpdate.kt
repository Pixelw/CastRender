package tech.pixelw.castrender.feature.main


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class AppUpdate(
    @Json(name = "abis")
    val abis: List<String>,
    @Json(name = "alternativeApkUrl")
    val alternativeApkUrl: List<String>?,
    @Json(name = "changeLog")
    val changeLog: String,
    @Json(name = "minSdk")
    val minSdk: Int,
    @Json(name = "sizeInByte")
    val sizeInByte: Int,
    @Json(name = "storeUrls")
    val storeUrls: List<String>?,
    @Json(name = "url")
    val url: String,
    @Json(name = "versionCode")
    val versionCode: Int,
    @Json(name = "versionName")
    val versionName: String
) {
    companion object {
        const val PLACEHOLDER_ABI = "_abi_"
        const val ABI_UNIVERSAL = "all"
//        const val ABI_ARMV7 = "armeabi-v7a"
//        const val ABI_ARMV8 = "arm64-v8a"
//        const val ABI_X86 = "x86"
    }
}