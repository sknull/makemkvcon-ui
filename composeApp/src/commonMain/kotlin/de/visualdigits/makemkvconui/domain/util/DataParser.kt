package de.visualdigits.makemkvconui.domain.util

import de.visualdigits.makemkvconui.domain.model.bluray.info.common.TypeIndicator
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.DiscData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.StreamData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.TrackCountData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.TrackData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.drive.DriveData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.drive.DriveFlags
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageType
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.CInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.SInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.TInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageSeverity
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressCode
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressCurrentTitle
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressTotalTitle
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressValue

object DataParser {

    fun parseData(parts: List<String>): Data {
        return when (parts[0]) {
            //
            // info command
            //
            "MSG" -> MessageData(
                messageType = MessageType.fromId(parts[1].toInt()),
                flags = MessageSeverity.fromId(parts[2].toInt()),
                argsCount = parts[3].toInt(),
                localizedText = parts[4],
                formatString = parts[5],
                args = parts.drop(6)
            )
            "DRV" -> DriveData(
                index = parts[1].toInt(),
                visible = parts[2].toInt(),
                enabled = parts[3].toInt(),
                flags = DriveFlags.fromId(parts[4].toInt()),
                driveName = parts[5],
                discName = parts[6],
                devicePath = parts[7]
            )
            "TCOUNT" -> TrackCountData(
                trackCount = parts[1].toInt()
            )
            "TINFO" -> TrackData(
                titleId = parts[1].toInt(),
                field = TInfoField.fromId(parts[2].toInt()),
                typeIndicator = TypeIndicator.fromId(parts[3].toInt()),
                value = parts[4]
            )
            "SINFO" -> StreamData(
                titleId = parts[1].toInt(),
                streamId = parts[2].toInt(),
                field = SInfoField.fromId(parts[3].toInt()),
                typeIndicator = TypeIndicator.fromId(parts[4].toInt()),
                value = parts[5]
            )
            "CINFO" -> DiscData(
                field = CInfoField.fromId(parts[1].toInt()),
                typeIndicator = TypeIndicator.fromId(parts[2].toInt()),
                value = parts[3]
            )

            //
            // progress
            //
            "PRGT" -> ProgressTotalTitle(
                code = ProgressCode.fromId(parts[1].toInt()),
                typeIndicator = TypeIndicator.fromId(parts[2].toInt()),
                value = parts[3]
            )
            "PRGC" -> ProgressCurrentTitle(
                code = ProgressCode.fromId(parts[1].toInt()),
                typeIndicator = TypeIndicator.fromId(parts[2].toInt()),
                value = parts[3]
            )
            "PRGV" -> ProgressValue(
                currentVal = parts[1].toInt(),
                totalVal = parts[2].toInt(),
                maxVal = parts[3].toInt(),
            )

            else -> error("Unknown data row: ${parts.first()}")
        }
    }
}
