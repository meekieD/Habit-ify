package com.dyusov.core.common.utils

typealias RootError = MyError
typealias EmptyResult<E> = MyResult<Unit, E>

sealed interface MyResult<out T, out E : RootError> {
    data class Success<out T>(val data: T) : MyResult<T, Nothing>
    data class Error<out E : RootError>(val error: E) : MyResult<Nothing, E>
}

inline fun <T, E : MyError, R> MyResult<T, E>.map(map: (T) -> R): MyResult<R, E> {
    return when (this) {
        is MyResult.Error -> MyResult.Error(error)
        is MyResult.Success -> MyResult.Success(map(data))
    }
}

inline fun <T, E : MyError> MyResult<T, E>.onSuccess(action: (T) -> Unit): MyResult<T, E> {
    return when (this) {
        is MyResult.Error -> this
        is MyResult.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E : MyError> MyResult<T, E>.onError(action: (E) -> Unit): MyResult<T, E> {
    return when (this) {
        is MyResult.Error -> {
            action(error)
            this
        }

        is MyResult.Success -> this
    }
}

fun <T, E : MyError> MyResult<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}