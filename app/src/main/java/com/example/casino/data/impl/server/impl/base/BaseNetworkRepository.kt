package com.example.casino.data.impl.server.impl.base

import com.example.casino.R
import com.example.casino.app.App
import com.example.casino.data.business_objects.base.ResultStatus
import com.example.casino.data.business_objects.base.ResultWithStatus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class BaseNetworkRepository {
    fun <Type : Any?> parseResult(
        response: Response<Type>
    ): ResultWithStatus<Type> {
        var resultData: Type? = null
        var resultStatus = ResultStatus.failure()

        if (response.body() != null) {
            response.body()?.let {
                resultStatus = if (response.code() == 200) {
                    try {
                        resultData = it
                        ResultStatus.success()
                    } catch (e: Exception) {
                        ResultStatus.failure(e)
                    }
                } else {
                    ResultStatus.failure(
                        App.instance.getString(R.string.request_error_code, response.code())
                    )
                }
            }
        } else {
            //something get wrong
            resultStatus = parseError(response)
        }
        return ResultWithStatus(resultData, resultStatus)
    }

    private fun <Type : Any?> parseError(
        response: Response<Type>
    ): ResultStatus {
        return if (response.errorBody() != null) {
            try {
                val jObjError = JSONObject(response.errorBody()!!.string())
                val error = jObjError.getString("message")
                if (!error.isNullOrEmpty()) {
                    ResultStatus.failure(error)
                } else {
                    ResultStatus.failure(
                        App.instance.getString(R.string.request_error_code, response.code())
                    )
                }
            } catch (e: Exception) {
                if (response.code() == 401) {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    val error = jObjError.getString("message")
                    ResultStatus.failure(
                        App.instance.getString(R.string.request_error_unauthorised, error)
                    )
                } else {
                    ResultStatus.failure(
                        App.instance.getString(R.string.request_error_code, response.code())
                    )
                }
            }

        } else {
            ResultStatus.failure(
                App.instance.getString(R.string.request_error_code, response.code())
            )
        }
    }

    fun <Type : Any> parseResponse(response: Response<Type>): ResultStatus {
        if (response.body() != null) {
            response.body()!!.let {
                return if (response.code() == 200) {
                    ResultStatus.success()
                } else {
                    ResultStatus.failure(
                        App.instance.getString(R.string.request_error_code, response.code())
                    )
                }
            }
        } else {
            //something get wrong
            return parseError(response)
        }
    }

    private fun <Type : Any?> enqueueCallResultWithStatus(
        call: Call<Type>,
        continuation: Continuation<ResultWithStatus<Type>>,
        throwErrorIfDataIsNull: Boolean = false,
        errorMsgIfDataIsNull: String? = null
    ) {
        call.enqueue(object : Callback<Type> {
            override fun onResponse(
                call: Call<Type>,
                response: Response<Type>
            ) {
                val result = parseResult(response)
                if (throwErrorIfDataIsNull) {
                    if (result.data == null) {
                        continuation.resume(
                            ResultWithStatus(
                                null,
                                ResultStatus.failure(
                                    errorMsgIfDataIsNull
                                        ?: App.instance.getString(R.string.request_error_data_is_null)
                                )
                            )
                        )
                    } else {
                        continuation.resume(result)
                    }
                } else {
                    continuation.resume(result)
                }
            }

            override fun onFailure(
                call: Call<Type>,
                t: Throwable
            ) {
                continuation.resume(ResultWithStatus(null, ResultStatus.failure(t)))
            }
        })
    }

    suspend fun <Type : Any?> enqueueCallResultWithStatusSuspended(
        call: Call<Type>,
        throwErrorIfDataIsNull: Boolean = false,
        errorMsgIfDataIsNull: String? = null
    ): ResultWithStatus<Type> {
        return suspendCoroutine { continuation ->
            enqueueCallResultWithStatus(
                call,
                continuation,
                throwErrorIfDataIsNull,
                errorMsgIfDataIsNull
            )
        }
    }
}