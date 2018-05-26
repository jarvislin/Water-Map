package com.jarvislin.watermap

import com.jarvislin.watermap.data.LocalData
import com.jarvislin.watermap.data.remote.Api
import com.jarvislin.watermap.data.remote.NetworkService

open class BaseRepository(protected val localData: LocalData = LocalData(), private val networkService: NetworkService = NetworkService()) {

    protected fun api() = networkService.create(Api::class.java)
}