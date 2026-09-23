package de.visualdigits.makemkvconui.domain.model.bluray.info.data.message

enum class MessageSeverity(
    val id: Int
) {
    INFO(0x0001),
    WARN(0x0002),
    ERROR(0x0004),
    INTERACTION_REQUIRED(0x0008),
    ;

    companion object {

        fun fromId(id: Int): List<MessageSeverity> {
            return entries.mapNotNull { entry ->
                val i = id and entry.id
                if (i != 0) entry else null
            }
        }
    }
}
