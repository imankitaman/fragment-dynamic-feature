package ankitaman.`in`.dynamicfeature.utils

class Utils {

    companion object {
        fun isClassAvailableInProject(className: String): Boolean {
            return try {
                Class.forName(className)
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }
}