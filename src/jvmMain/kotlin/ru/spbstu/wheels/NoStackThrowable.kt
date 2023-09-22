package ru.spbstu.wheels

open class NoStackThrowable
constructor(message: String?, cause: Throwable?) :
        Throwable(message, cause, false, false) {
    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(cause: Throwable) : this(cause.message, cause)

    override fun fillInStackTrace(): NoStackThrowable = this
}

