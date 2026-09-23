package de.visualdigits.makemkvconui.domain.model.bluray.info.data

import de.visualdigits.makemkvconui.domain.model.bluray.info.common.TypeIndicator
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.CInfoField

data class DiscData(
    val field: CInfoField,
    val typeIndicator: TypeIndicator,
    val value: String
): Data, Entity
