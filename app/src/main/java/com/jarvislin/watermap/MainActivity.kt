package com.jarvislin.watermap

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.jarvislin.watermap.data.models.Feature
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.compass.CompassOverlay


class MainActivity : AppCompatActivity() {

    lateinit var mapViewModel: MapViewModel

    companion object {
        private val TAIPEI = GeoPoint(25.0469077, 121.5363626)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        //setting this before the layout is inflated is a good idea

        setContentView(R.layout.activity_main)

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        initMap()

        mapViewModel.features.observe(this, Observer<List<Feature>> {
            addFeatures(it)
        })
    }

    private fun addFeatures(features: List<Feature>?) {
        features?.let {
            
        }
    }

    private fun initMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)

        map.setBuiltInZoomControls(false)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(12.0)
        mapController.setCenter(TAIPEI)

        val overlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        overlay.enableMyLocation()
        map.overlays.add(overlay)

        val compass = CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        compass.enableCompass()
        map.overlays.add(compass)
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

class MapRepository : BaseReposotory() {
    fun search(): Single<List<Feature>> = api().search().map { it.features }

}
