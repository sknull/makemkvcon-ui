package de.visualdigits.makemkvconui.domain.model.bluray.info.data

import de.visualdigits.makemkvconui.domain.model.bluray.info.common.TypeIndicator
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.TInfoField

data class TrackData(
    val titleId: Int,
    val field: TInfoField,
    val typeIndicator: TypeIndicator,
    val value: String
): Data
