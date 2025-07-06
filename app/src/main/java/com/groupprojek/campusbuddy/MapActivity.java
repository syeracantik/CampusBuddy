package com.groupprojek.campusbuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText etSearchLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_map;
    }

    @Override
    protected void initViews() {
        setToolbarTitle("Campus Map");

        etSearchLocation = findViewById(R.id.etSearchLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        etSearchLocation.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                            keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchLocation();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            showCurrentLocation();
        }

        LatLng uitmShahAlam = new LatLng(3.0738, 101.5039);
        mMap.addMarker(new MarkerOptions().position(uitmShahAlam).title("UiTM Shah Alam"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uitmShahAlam, 15));
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .title("You are here")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
                    }
                });
    }

    private void searchLocation() {
        String searchString = etSearchLocation.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list;

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (list != null && !list.isEmpty()) {
            Address address = list.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(searchString));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            Toast.makeText(this, "Location Found: " + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            }
        }
    }
}
