package de.visualdigits.makemkvconui.domain.model.bluray.info.data.drive

import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity

data class DriveData(
    val index: Int,
    val visible: Int,
    val enabled: Int,
    val flags: DriveFlags,
    val driveName: String,
    val discName: String,
    val devicePath: String
) : Data, Entity, Comparable<DriveData> {
    override fun compareTo(other: DriveData): Int {
        return compareBy<DriveData> { it.index }.compare(this, other)
    }
}
