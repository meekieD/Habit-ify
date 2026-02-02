package com.dyusov.core.data.utils

import com.dyusov.core.common.utils.GeneralError
import com.dyusov.core.common.utils.MyResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal suspend fun throwCancellationExceptionAndGeneralError(): MyResult.Error<GeneralError> {
    currentCoroutineContext().ensureActive()
    return MyResult.Error(GeneralError.UNKNOWN)
}