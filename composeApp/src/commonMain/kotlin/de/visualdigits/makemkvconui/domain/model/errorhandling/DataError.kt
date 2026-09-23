package de.visualdigits.makemkvconui.domain.model.errorhandling

import de.visualdigits.common.domain.model.errorhandling.AppError

sealed interface DataError: AppError {

    enum class Remote: DataError {
        CONNECTION_ERROR,
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local: DataError {
        FILE_NOT_FOUND,
        DISK_FULL,
        SERIALIZATION,
        UNKNOWN_FIELD,
        UNKNOWN
    }
}
