package com.jarvislin.watermap

import com.jarvislin.watermap.data.LocalData
import com.jarvislin.watermap.data.remote.Api
import com.jarvislin.watermap.data.remote.NetworkService

open class BaseReposotory(protected val localData: LocalData = LocalData(), private val networkService: NetworkService = NetworkService()) {

    fun api() = networkService.create(Api::class.java)
}