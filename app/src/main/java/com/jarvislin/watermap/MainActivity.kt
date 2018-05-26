package com.jarvislin.watermap

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.jarvislin.watermap.data.models.Feature
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var mapViewModel: MapViewModel

    companion object {
        private val TAIPEI = GeoPoint(25.0469077, 121.5363626)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // map config
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        initMap()

        mapViewModel.features.observe(this, Observer<List<Feature>> {
            addFeatures(it)
        })
    }

    private fun addFeatures(features: List<Feature>?) {
        features?.let {
            map.overlays.clear()
            Flowable.fromIterable(it)
                    .subscribeOn(Schedulers.io())
                    .map {
                        val marker = Marker(map)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.position = GeoPoint(it.geometry.coordinates[1], it.geometry.coordinates[0])
                        marker.snippet = it.property.comments.lastOrNull()?.html
                        marker
                    }
                    .delay(25, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        map.overlays.add(it)
                        map.invalidate()
                    }, {})
        }
    }

    private fun initMap() {
        map.setBuiltInZoomControls(false)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(12.0)
        mapController.setCenter(TAIPEI)
    }

    override fun onResume() {
        super.onResume()
        mapViewModel.fetchFeatures()
    }

}

class MapViewModel(private val repository: MapRepository = MapRepository()) : ViewModel() {
    val features: MutableLiveData<List<Feature>> = MutableLiveData()

    fun fetchFeatures() {
        repository.search()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ features.value = it }, {})
    }
}

class MapRepository : BaseRepository() {
    fun search(): Single<List<Feature>> = api().search().map { it.features }

}
