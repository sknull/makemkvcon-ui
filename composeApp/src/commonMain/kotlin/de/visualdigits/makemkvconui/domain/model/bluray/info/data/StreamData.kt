package de.visualdigits.makemkvconui.domain.model.bluray.info.data

import de.visualdigits.makemkvconui.domain.model.bluray.info.common.TypeIndicator
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.SInfoField

data class StreamData(
    val titleId: Int,
    val streamId: Int,
    val field: SInfoField,
    val typeIndicator: TypeIndicator,
    val value: String
): Data
