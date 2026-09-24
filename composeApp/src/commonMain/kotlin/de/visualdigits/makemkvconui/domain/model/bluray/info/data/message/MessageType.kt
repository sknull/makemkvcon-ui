package de.visualdigits.makemkvconui.domain.model.bluray.info.data.message

import co.touchlab.kermit.Severity

enum class MessageType(
    val id: Int,
    val severity: Severity
) {

    // critical error
    ERR_READ_FILE(1003, Severity.Error),
    ERR_WRITE_FILE(1004, Severity.Error),
    ERR_AACS_FAIL(2003, Severity.Error),
    ERR_BD_PLUS_FAIL(2004, Severity.Error),
    ERR_RPC_ZONE(5010, Severity.Error),

    // analysis and titles
    INFO_MAKEMKVCON(1005, Severity.Info),
    INFO_LIBREDRIVE(1011, Severity.Info),
    INFO_SCANNING(3001, Severity.Info),
    INFO_DIRECT_ACCESS(3007, Severity.Info),
    INFO_TITLE_SKIPPED_TIME_TOO_SHORT(3025, Severity.Info),
    INFO_TITLE_SKIPPED_TIME(3028, Severity.Info),
    INFO_TITLE_ADDED(3307, Severity.Info),
    INFO_TILTE_SKIPPED_DUPLICATE(3309, Severity.Info),
    INFO_OPERATION_COMPLETED(5011, Severity.Info),
    INFO_MAIN_TITLE(5053, Severity.Info),
    INFO_HASH_LOADED(5085, Severity.Info),

    // status and process updates
    PROG_START(4001, Severity.Info),
    PROG_FINISH(4002, Severity.Info),
    PROG_FAIL(4003, Severity.Info),
    INFO_SPEED(5014, Severity.Info),
    INFO_TIME_REMAIN(5038, Severity.Info),

    UNKNOWN(9999, Severity.Warn)
    ;

    companion object {

        fun fromId(id: Int): MessageType = entries.find { id == it.id } ?: MessageType.UNKNOWN
    }
}
