package tech.pixelw.castrender.utils.report

interface IThirdPartyReport {
    fun preInit()

    fun init()

    fun submitException(exception: Throwable)

    fun identityUser(id: String)

    fun testCrash()
}
