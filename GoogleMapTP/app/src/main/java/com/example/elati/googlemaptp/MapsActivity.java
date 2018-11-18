package com.example.elati.googlemaptp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Code réponse pour la permission de localisation accordé
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

        // Add a marker in Paris and move the camera
//        LatLng paris = new LatLng(48.866667, 2.333333);
//        mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));

        // Active le button en cas de validation de permission
        enableMyLocation();
    }


    /**
     * Permet d'activer le button localisation en cas du permission de localisation accordé
     */
    private void enableMyLocation() {
        // Véfication si la permission de location est accordée
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Active l'affichage du button localisation
            mMap.setMyLocationEnabled(true);
        } else {
            // On redemande encore la permission au utilisateur lors du prochain ouverture de l'appli
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    // Activation de la location en fonction de la permission
                    enableMyLocation();
                    break;
                }
        }
    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonSearch:
                // On récupère le lieu (addresse)
                EditText addressField = (EditText) findViewById(R.id.textSearch);
                String address = addressField.getText().toString();

                List<Address> adressList;
                MarkerOptions userMarkerOptions = new MarkerOptions();
                // Vérifie si le champ de saisie n'est pas vide
                if(!TextUtils.isEmpty(address)){
                    // Moteur de recherche pour les adresses
                    Geocoder geocoder = new Geocoder(this);

                    try{
                        // On récupère seulement la 1ère adresse recherchée parmi les adresses recherchées
                        adressList = geocoder.getFromLocationName(address, 1);

                        // On vérifie si la liste des adresses ne sont pas vides et nulles.
                        if(adressList != null && adressList.size() != 0){

                            // On extrait le 1er adresse de la liste des adresses recherchées
                            Address userAddress = adressList.get(0);
                            // On définit l'attitude et la longitude de l'adresse
                            LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                            // On vide toutes les données de mMap (par exemple enlever tous les markers)
                            mMap.clear();
                            // On définit le marker avec l'altitude, la longitude de l'adresse, le titre, l'icône
                            userMarkerOptions.position(latLng);
                            userMarkerOptions.title(address);
                            userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                            // On ajoute le marker dans le map
                            mMap.addMarker(userMarkerOptions);
                            // On fixe le camera vers le marker créé avec le zoom niveau 3
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3));
                        }
                        else{
                            Toast.makeText(this, "Lieu non trouvé ...", Toast.LENGTH_LONG).show();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(this, "Entrez le lieu de votre choix, s'il vous plaît", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
