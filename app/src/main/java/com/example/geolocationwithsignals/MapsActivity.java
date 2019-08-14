package com.example.geolocationwithsignals;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.JsonArray;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  PubNub pubnub;
  Marker marker;
  Boolean isMarkerPlaced = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    initPubNub();
  }

  public void initPubNub(){
    PNConfiguration pnConfiguration = new PNConfiguration();
    pnConfiguration.setPublishKey("Your_Pub_Key_Here");
    pnConfiguration.setSubscribeKey("Your_Sub_Key_Here");
    pnConfiguration.setSecure(true);
    pubnub = new PubNub(pnConfiguration);

    // Listen to messages that arrive in the channel
    pubnub.addListener(new SubscribeCallback() {
      @Override
      public void status(PubNub pub, PNStatus status) {

      }

      @Override
      public void message(PubNub pub, final PNMessageResult message) {

      }

      @Override
      public void presence(PubNub pub, PNPresenceEventResult presence) {

      }

      @Override
      public void signal(PubNub pubnub, final PNMessageResult signal) {

      }
    });

    // Subscribe to the global channel
    pubnub.subscribe()
          .channels(Arrays.asList("geolocation_channel"))
          .execute();
  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    // Init payload coordinates
    JsonArray payload = new JsonArray();
    payload.add(37.782486);
    payload.add(-122.395344);
    placeMarker(payload);

    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
      @Override
      public void onMarkerDrag(Marker arg0) {
        Log.d("Marker", "Dragging");
      }

      @Override
      public void onMarkerDragEnd(Marker arg0) {
        Log.d("Marker", "Finished");

        // Updated payload coordinates
        JsonArray payload = new JsonArray();
        payload.add(marker.getPosition().latitude);
        payload.add(marker.getPosition().longitude);
        placeMarker(payload);
      }

      @Override
      public void onMarkerDragStart(Marker arg0) {
        Log.d("Marker", "Started");
      }
    });
  }

  public void placeMarker(JsonArray loc){
    Log.d("Marker", "placeMarker function");

    Double lat = loc.get(0).getAsDouble();
    Double lng = loc.get(1).getAsDouble();

    LatLng newLoc = new LatLng(lat,lng);

    if(isMarkerPlaced){
      marker.setPosition(newLoc);
    }
    else{
      marker = mMap.addMarker(new MarkerOptions().position(newLoc).title("Marker").draggable(true));
      isMarkerPlaced = true;
      System.out.println(marker.getPosition());
    }

    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
  }
}
