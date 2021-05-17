package de.nycode.slpf

data class SemVerVersion(val major: Int, val minor: Int, val patch: Int) {

    companion object {

        private val regex = "([0-9]+)\\.([0-9]+)\\.([0-9]+)".toRegex()

        fun parseOrNull(versionString: String): SemVerVersion? {
            val (major, minor, patch) = regex.find(versionString)?.destructured ?: return null
            return SemVerVersion(major.toInt(), minor.toInt(), patch.toInt())
        }
    }

    operator fun compareTo(other: SemVerVersion): Int {
        when {
            other == this -> {
                return 0
            }
            other.major > this.major -> {
                return -1
            }
            other.major < this.major -> {
                return 1
            }
            other.minor > this.minor -> {
                return -1
            }
            other.minor < this.minor -> {
                return 1
            }
            other.patch > this.patch -> {
                return -1
            }
            other.patch < this.patch -> {
                return 1
            }
            else -> return 0
        }
    }

    override fun toString() = "$major.$minor.$patch"

}
