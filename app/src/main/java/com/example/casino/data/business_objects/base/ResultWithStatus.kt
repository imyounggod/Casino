package com.example.casino.data.business_objects.base

class ResultWithStatus<T>(val data: T?, val status: ResultStatus) {

    fun isFailure(): Boolean {
        return this.status.status == ResultStatus.StateList.FAILURE
    }

    fun isSuccess(): Boolean {
        return this.status.status == ResultStatus.StateList.SUCCESS
    }
}