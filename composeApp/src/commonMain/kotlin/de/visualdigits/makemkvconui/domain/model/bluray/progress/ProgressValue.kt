package de.visualdigits.makemkvconui.domain.model.bluray.progress

import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity
import kotlin.math.roundToInt

data class ProgressValue(
    val currentVal: Int,
    val totalVal: Int,
    val maxVal: Int,
): ProgressData, Entity {

    val progressCurrentStep: Float
        get() = (currentVal / maxVal.toDouble().coerceAtLeast(0.0001)).toFloat()

    val progressTotal: Float
        get() = (totalVal / maxVal.toDouble().coerceAtLeast(0.0001)).toFloat()

    override fun toString(): String {
        return "current [$currentVal]: $progressCurrentStep, total [$totalVal]: $progressTotal, max [$maxVal]"
    }
}
