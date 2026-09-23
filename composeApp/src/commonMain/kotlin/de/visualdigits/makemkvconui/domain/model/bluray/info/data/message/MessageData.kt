package de.visualdigits.makemkvconui.domain.model.bluray.info.data.message

import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity

data class MessageData(
    val messageType: MessageType,
    val flags: List<MessageSeverity>,
    val argsCount: Int,
    val localizedText: String,
    val formatString: String,
    val args: List<String>
): Data, Entity
