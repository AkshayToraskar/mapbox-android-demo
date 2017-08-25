package com.mapbox.mapboxandroiddemo.labs;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.style.sources.TileSet;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.commons.geojson.Feature;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapillaryActivity extends AppCompatActivity {

  private MapView mapView;
  private MapboxMap map;
  private Projection projection;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Mapbox access token is configured here. This needs to be called either in your application
    // object or in the same activity which contains the mapview.
    Mapbox.getInstance(this, getString(R.string.access_token));

    // This contains the MapView in XML and needs to be called after the access token is configured.
    setContentView(R.layout.activity_lab_mapillary);

//    final ImageView imageView = (ImageView) findViewById(R.id.imageView);

    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(final MapboxMap mapboxMap) {
        map = mapboxMap;
        projection = mapboxMap.getProjection();

        map.addSource(Mapillary.createSource());
        map.addLayerBelow(Mapillary.createLineLayer(), Mapillary.ID_ABOVE_LAYER);
        map.addLayerBelow(Mapillary.createCircleLayer(), Mapillary.ID_LINE_LAYER);
        map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
          @Override
          public void onMapClick(@NonNull LatLng point) {
            PointF screenPoint = projection.toScreenLocation(point);
            List<Feature> featureList = map.queryRenderedFeatures(screenPoint, Mapillary.ID_LINE_LAYER);
            if (!featureList.isEmpty()) {
              Feature feature = featureList.get(0);
              Log.e(Mapillary.ID_SOURCE, feature.toJson());
//              String key = feature.getStringProperty("key");
//              Picasso.with(MapillaryActivity.this)
//                .load(String.format(Mapillary.URL_IMAGE_PLACEHOLDER, key))
//                .into(imageView);
            } else {
              Log.e(Mapillary.ID_SOURCE, "NO FEATURES");
            }
          }
        });
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  private static class Mapillary {

    static final String ID_SOURCE = "mapillary";
    static final String ID_LINE_LAYER = ID_SOURCE + ".line";
    static final String ID_CIRCLE_LAYER = ID_SOURCE + ".circle";
    static final String ID_ABOVE_LAYER = "aerialway";
    static final String URL_TILESET = "https://d25uarhxywzl1j.cloudfront.net/v0.1/{z}/{x}/{y}.mvt";
//    static final String URL_IMAGE_PLACEHOLDER = "https://d1cuyjsrcm0gby.cloudfront.net/%s/thumb-320.jpg";

    static Source createSource() {
      TileSet mapillaryTileset = new TileSet("2.1.0", Mapillary.URL_TILESET);
      return new VectorSource(Mapillary.ID_SOURCE, mapillaryTileset);
    }

    static Layer createLineLayer() {
      LineLayer lineLayer = new LineLayer(Mapillary.ID_LINE_LAYER, Mapillary.ID_SOURCE);
      lineLayer.setMinZoom(0);
      lineLayer.setMaxZoom(14);
      lineLayer.setSourceLayer("mapillary-sequences");
      lineLayer.setProperties(
        lineCap(Property.LINE_CAP_ROUND),
        lineJoin(Property.LINE_JOIN_ROUND),
        lineOpacity(0.6f),
        lineWidth(2.0f),
        lineColor(Color.GREEN)
      );
      return lineLayer;
    }

    static Layer createCircleLayer() {
      CircleLayer circleLayer = new CircleLayer(Mapillary.ID_CIRCLE_LAYER, Mapillary.ID_SOURCE);
      circleLayer.setSourceLayer("mapillary-sequence-overview");
      circleLayer.setProperties(
        circleColor(Color.GREEN),
        circleRadius(4.0f),
        circleOpacity(0.6f)
      );
      return circleLayer;
    }
  }
}