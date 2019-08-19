# Build a Geolocation App with Android and PubNub Signals

Learn how to build a simple Geolocation app that sends a stream of GPS coordinates to other devices using PubNub Signals! In the app, a marker will initiially be placed in San Francisco. Drag and drop the marker anywhere you please, and the coordinates will be sent to a global channel. The marker will update for everyone connected to the channel! 

<p align="center">
  <img src="./media/geolocation_app_screenshot.png" alt="Screenshot of client-to-server demo" width="500" height="440" />
</p>

**Note: This app DOES NOT use your location!**

## PubNub Signals

PubNub Signals is a small message payload, no greater than **30 bytes**, that offers a **low-cost** data delivery rate while still leveraging PubNub’s secure and reliable [Data Stream Network](https://www.pubnub.com/products/global-data-stream-network/). Some use cases for Signals include:
  1) Typing indicators in a chat app
  2) GPS lat/long updates
  3) Sensor streaming updates for IoT devices
  4) Stats for gaming applications

## Setup

1) Sign up for a free PubNub account to get your Pub/Sub API keys.

 <a href="https://dashboard.pubnub.com/signup?devrel_gh=geolocation-signals-demo">
    <img alt="PubNub Signup" src="https://i.imgur.com/og5DDjf.png" width=260 height=97/>
</a>

2) Since we are using Google Maps in the app, you need to get a [Google Map API key](https://developers.google.com/maps/documentation/embed/get-api-key). Once you get the key, go to the **debug directory**, under **src**, and add it to the file [google_maps_api.xml](https://github.com/ocastroa/GeolocationWithSignals/blob/master/app/src/debug/res/values/google_maps_api.xml).

3) Clone the repo.
```bash
https://github.com/ocastroa/GeolocationWithSignals
```
4) Open the [MapsActivity.java](https://github.com/ocastroa/GeolocationWithSignals/blob/master/app/src/main/java/com/example/geolocationwithsignals/MapsActivity.java) file in Android Studio and replace 'Your_Pub_Key_Here' and 'Your_Sub_Key_Here' with your Pub/Sub API keys.

5) Run the app on two emulators, drag and drop the marker anywhere you please, and watch the location change on both emulators! 

## Build Your Own Geolocation App

To learn more about this project or if you want to build this project from scratch, check out the tutorial (coming soon).

  <a href="https://www.pubnub.com/blog/?devrel_gh=geolocation-signals-demo">
    <img alt="PubNub Blog" src="https://i.imgur.com/aJ927CO.png" width=260 height=98/>
  </a>
