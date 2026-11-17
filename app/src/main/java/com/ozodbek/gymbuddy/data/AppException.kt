package com.ozodbek.gymbuddy.data

/** An error whose message is safe to show to the user. The data layer turns backend errors into this. */
class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)
