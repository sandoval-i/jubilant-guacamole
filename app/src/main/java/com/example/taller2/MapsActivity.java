package com.example.taller2;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocation;
    private LatLng currLocation = null;
    private final int LOCATION_PERMISSION_CODE = 1;
    private EditText searchEditText;
    Geocoder geocoder;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    SensorEventListener lightSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        searchEditText = findViewById(R.id.searchEditText);
        geocoder = new Geocoder(this);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String searchAddress = searchEditText.getText().toString();
                if (!searchAddress.isEmpty()) {
                    LatLng newLocation = searchLocation(searchAddress);
                    if (newLocation != null) {
                        setLocation(newLocation);
                        printDistance(newLocation, currLocation);
                    }
                }
            }
            return true;
        });
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (map != null) {
                    if (event.values[0] < 5000) {
                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this,
                                R.raw.dark_style_json));
                    } else {
                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this,
                                R.raw.light_style_json));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }

    public double distance(LatLng l1, LatLng l2) {
        double lat1 = l1.latitude, lat2 = l2.latitude;
        double long1 = l1.longitude, long2 = l2.longitude;
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = 6371 * c;
        return Math.round(result * 100.0) / 100.0;
    }

    private LatLng searchLocation(String address) {
        if (address.isEmpty()) {
            Toast.makeText(this, "Direccion vacia", Toast.LENGTH_LONG).show();
            return null;
        }
        Log.i("LOL", "Busca la direccion " + address);
        try {
            List<Address> searchResult = geocoder.getFromLocationName(address, 2);
            if (searchResult != null && !searchResult.isEmpty()) {
                return new LatLng(searchResult.get(0).getLatitude(), searchResult.get(0).getLongitude());
            } else {
                Toast.makeText(this, "Direccion no encontrada", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String findTitle(LatLng location) {
        if (location == null) return "";
        try {
            List<Address> searchResult = geocoder.getFromLocation(location.latitude,
                    location.longitude, 2);
            StringBuilder result = new StringBuilder();
            if (searchResult != null && !searchResult.isEmpty()) {
                Address ad = searchResult.get(0);
                int len = ad.getMaxAddressLineIndex();
                for (int i = 0; i <= len; ++i) {
                    if (i > 0) result.append("/");
                    result.append(ad.getAddressLine(i));
                }
                Log.i("LOL", "La direccion es: " + result.toString());
                Log.i("LOL", "La direccion es: " + result.toString());
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setLocation(LatLng location) {
        if (location == null) return;
        map.clear();
        map.addMarker(new MarkerOptions().position(location).title(findTitle(location)));
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    @SuppressLint("MissingPermission")
    private void setCurrentLocation() {
        Log.i("LOL", "setCurrentLocation");
        if (!hasLocationPermission()) return;
        fusedLocation.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                null).addOnSuccessListener(this, location -> {
            if (location == null) return;
            currLocation = new LatLng(location.getLatitude(), location.getLongitude());
            setLocation(currLocation);
        });
    }

    private boolean hasLocationPermission() {
        return PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void printDistance(LatLng l1, LatLng l2) {
        Toast.makeText(this, "Distancia: " + distance(l1, l2) + "Km",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setCurrentLocation();
        }
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
        Log.i("LOL", "onMapReady");
        map = googleMap;
        if (!PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            String RATIONALE = "Es necesario brindar permiso para acceder a la localizacion";
            PermissionHelper.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_CODE, RATIONALE);
        }
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setOnMapLongClickListener(location -> {
            setLocation(location);
            printDistance(currLocation, location);
        });
        setCurrentLocation();
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        /*map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}