package de.visualdigits.makemkvconui.domain.model.bluray.progress

import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity
import kotlin.math.roundToInt

data class ProgressValue(
    val currentVal: Int,
    val totalVal: Int,
    val maxVal: Int,
): Data, Entity {

    val progressCurrentStep: Int
        get() = (currentVal / maxVal.toDouble().coerceAtLeast(0.0001) * 100.0).roundToInt()

    val progressTotal: Int
        get() = (totalVal / maxVal.toDouble().coerceAtLeast(0.0001) * 100.0).roundToInt()

    override fun toString(): String {
        return "current [$currentVal]: $progressCurrentStep, total [$totalVal]: $progressTotal, max [$maxVal]"
    }
}
