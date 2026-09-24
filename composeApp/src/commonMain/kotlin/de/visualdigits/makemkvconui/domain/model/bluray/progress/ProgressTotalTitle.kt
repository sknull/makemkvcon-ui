package de.visualdigits.makemkvconui.domain.model.bluray.progress

import de.visualdigits.makemkvconui.domain.model.bluray.info.common.TypeIndicator
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity

data class ProgressTotalTitle(
    val code: ProgressCode,
    val typeIndicator: TypeIndicator,
    val value: String
): ProgressData, Entity
