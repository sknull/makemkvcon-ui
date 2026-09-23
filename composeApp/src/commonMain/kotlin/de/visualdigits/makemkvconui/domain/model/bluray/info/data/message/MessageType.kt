package de.visualdigits.makemkvconui.domain.model.bluray.info.data.message

enum class MessageType(
    val id: Int
) {

    // critical error
    ERR_READ_FILE(1003),
    ERR_WRITE_FILE(1004),
    ERR_AACS_FAIL(2003),
    ERR_BD_PLUS_FAIL(2004),
    ERR_RPC_ZONE(5010),

    // analysis and titles
    INFO_MAKEMKVCON(1005),
    INFO_LIBREDRIVE(1011),
    INFO_SCANNING(3001),
    INFO_DIRECT_ACCESS(3007),
    INFO_TITLE_SKIPPED(3025),
    INFO_TITLE_SKIPPED_TIME(3028),
    INFO_TITLE_ADDED(3307),
    INFO_OPERATION_COMPLETED(5011),
    INFO_MAIN_TITLE(5053),
    INFO_HASH_LOADED(5085),

    // status and process updates
    PROG_START(4001),
    PROG_FINISH(4002),
    PROG_FAIL(4003),
    INFO_SPEED(5014),
    INFO_TIME_REMAIN(5038),

    UNKNOWN(9999)
    ;

    companion object {

        fun fromId(id: Int): MessageType = entries.find { id == it.id } ?: MessageType.UNKNOWN
    }
}
