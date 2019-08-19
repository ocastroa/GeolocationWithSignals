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
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.text.DecimalFormat;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private PubNub pubnub;
  private Marker marker;
  private Boolean isMarkerPlaced = false;

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

  // Initialize PubNub and set up a listener
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
        System.out.println("Message: " + signal.getMessage());

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            try {
              JsonArray payload = signal.getMessage().getAsJsonArray();
              placeMarker(payload);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
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
   * This is where we can add markers or lines, add listeners or move the camera.
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

    // Listener for when marker is dragged to another location
    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
      @Override
      public void onMarkerDrag(Marker arg0) {
        Log.d("Marker", "Dragging");
      }

      @Override
      public void onMarkerDragEnd(Marker arg0) {
        Log.d("Marker", "Finished");

        // Get coordinate values up to 6 decimal numbers
        DecimalFormat decimalFormat = new DecimalFormat("0.######");
        JsonArray payload = new JsonArray();
        payload.add(decimalFormat.format(marker.getPosition().latitude));
        payload.add(decimalFormat.format(marker.getPosition().longitude));

        // Message Payload size for Signals is limited to 30 bytes
        pubnub.signal()
              .channel("geolocation_channel")
              .message(payload)
              .async(new PNCallback<PNPublishResult>() {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {
                  // Error
                  if(status.isError()) {
                    System.out.println("Error: pub status code: " + status.getStatusCode());
                  }
                }
              });
      }

      @Override
      public void onMarkerDragStart(Marker arg0) {
        Log.d("Marker", "Started");
      }
    });
  }

  // Place marker on the map in the specified location
  public void placeMarker(JsonArray loc){
    //LatLng only accepts arguments of type Double
    Double lat = loc.get(0).getAsDouble();
    Double lng = loc.get(1).getAsDouble();
    LatLng newLoc = new LatLng(lat,lng);

    if(isMarkerPlaced){
      // Change position of the marker
      marker.setPosition(newLoc);
      marker.setTitle(newLoc.toString());
    }
    else{
      // Add a marker to the map in the specified location
      marker = mMap.addMarker(new MarkerOptions().position(newLoc).title(newLoc.toString()).draggable(true));
      isMarkerPlaced = true;
    }

    // Move the camera to the location of the marker
    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
  }
}
