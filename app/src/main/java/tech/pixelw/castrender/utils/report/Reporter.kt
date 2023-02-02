package tech.pixelw.castrender.utils.report

object Reporter : IThirdPartyReport {

    var solution: IThirdPartyReport? = null
        set(value) {
            require(value !is Reporter) {
                "solution can not be Reporter itself"
            }
            field = value
        }

    fun isInitialized() = solution != null && isInit

    private var isInit = false
    override fun preInit() {
        solution?.preInit()
    }

    override fun init() {
        solution?.let {
            it.init()
            isInit = true
        }
    }

    override fun submitException(exception: Throwable) {
        solution?.submitException(exception)
    }

    override fun identityUser(id: String) {
        solution?.identityUser(id)
    }

    override fun testCrash() {
        solution?.testCrash()
    }


}


