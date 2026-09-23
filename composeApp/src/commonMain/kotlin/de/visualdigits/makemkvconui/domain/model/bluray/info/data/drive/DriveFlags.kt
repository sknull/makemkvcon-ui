package de.visualdigits.makemkvconui.domain.model.bluray.info.data.drive

enum class DriveFlags(
    val id: Int
) {

    EMPTY(0),
    DVD(1),
    BLURAY(12)
    ;

    companion object {

        fun fromId(id: Int): DriveFlags = entries.find { id == it.id } ?: DriveFlags.EMPTY
    }
}
