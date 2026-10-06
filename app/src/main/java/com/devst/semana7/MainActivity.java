package com.devst.semana7;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnLinterna;
    private TextView txtUbicacion;
    private Spinner spLugar;

    private CameraManager cameraManager;
    private String idCamara;
    private boolean linternaEncendida = false;

    private LocationManager locationManager;
    private LocationListener listenerUbicacion;
    private double latitud = 0;
    private double longitud = 0;
    private boolean ubicacionObtenida = false;

    private static final int PERMISO_UBICACION = 100;
    private static final int PERMISO_CAMARA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLinterna = findViewById(R.id.btnLinterna);
        txtUbicacion = findViewById(R.id.txtUbicacion);
        spLugar = findViewById(R.id.spLugar);

        spLugar.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Lugares.NOMBRES));

        prepararLinterna();

        btnLinterna.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISO_CAMARA);
                return;
            }
            cambiarLinterna();
        });

        findViewById(R.id.btnUbicacion).setOnClickListener(v -> obtenerUbicacion());
        findViewById(R.id.btnMapa).setOnClickListener(v -> abrirMapaExterno());
        findViewById(R.id.btnSegundaVista).setOnClickListener(v ->
                startActivity(new Intent(this, SegundaActivity.class)));
        findViewById(R.id.btnDetalle).setOnClickListener(v -> abrirDetalle());
        findViewById(R.id.btnFormulario).setOnClickListener(v ->
                startActivity(new Intent(this, FormActivity.class)));
        findViewById(R.id.btnMapaInterno).setOnClickListener(v -> abrirMapaInterno());
        findViewById(R.id.btnImplicitos).setOnClickListener(v ->
                startActivity(new Intent(this, ImplicitosActivity.class)));
    }

    private void abrirDetalle() {
        int pos = spLugar.getSelectedItemPosition();
        if (pos < 0) return;
        Intent intent = new Intent(this, DetalleActivity.class);
        intent.putExtra("nombre", Lugares.NOMBRES[pos]);
        intent.putExtra("descripcion", Lugares.DESCRIPCIONES[pos]);
        intent.putExtra("lat", Lugares.LATITUDES[pos]);
        intent.putExtra("lng", Lugares.LONGITUDES[pos]);
        startActivity(intent);
    }

    private void abrirMapaInterno() {
        if (!ubicacionObtenida) {
            Toast.makeText(this, "Obtén tu ubicación primero", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("lat", latitud);
        intent.putExtra("lng", longitud);
        startActivity(intent);
    }

    private void abrirMapaExterno() {
        if (!ubicacionObtenida) {
            Toast.makeText(this, "Obtén tu ubicación primero", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse("geo:" + latitud + "," + longitud + "?q=" + latitud + "," + longitud);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay app de mapas", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepararLinterna() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String id : cameraManager.getCameraIdList()) {
                Boolean flash = cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (Boolean.TRUE.equals(flash)) {
                    idCamara = id;
                    break;
                }
            }
        } catch (CameraAccessException ignored) {}
    }

    private void cambiarLinterna() {
        if (idCamara == null) return;
        try {
            linternaEncendida = !linternaEncendida;
            cameraManager.setTorchMode(idCamara, linternaEncendida);
            btnLinterna.setText(linternaEncendida ? "💡 APAGAR LINTERNA" : "💡 ENCENDER LINTERNA");
        } catch (CameraAccessException ignored) {}
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
            return;
        }

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps && !network) {
            Toast.makeText(this, "Activa el GPS en ajustes", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        txtUbicacion.setText("Buscando...");

        // 1. Intentar obtener ubicación rápida (Caché)
        Location last = null;
        if (network) last = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (last == null && gps) last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if (last != null) {
            actualizarUIConUbicacion(last, " (Caché)");
        }

        // 2. Escuchar la ubicación real
        detenerUbicacion(); // Limpiar si había uno previo
        listenerUbicacion = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location loc) {
                actualizarUIConUbicacion(loc, "");
                detenerUbicacion(); // Parar al recibir la primera
            }
            @Override public void onStatusChanged(String p, int s, Bundle e) {}
            @Override public void onProviderEnabled(@NonNull String p) {}
            @Override public void onProviderDisabled(@NonNull String p) {}
        };

        if (network) locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listenerUbicacion);
        if (gps) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listenerUbicacion);

        // Si estás en emulador, recuerda enviar las coordenadas desde los controles del emulador (...)
        if (last == null) {
            Toast.makeText(this, "Si usas emulador, envía coordenadas desde los controles laterales", Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarUIConUbicacion(Location loc, String sufijo) {
        latitud = loc.getLatitude();
        longitud = loc.getLongitude();
        ubicacionObtenida = true;
        txtUbicacion.setText(String.format(Locale.getDefault(), "Lat: %.5f\nLng: %.5f%s", latitud, longitud, sufijo));
    }

    private void detenerUbicacion() {
        if (locationManager != null && listenerUbicacion != null) {
            locationManager.removeUpdates(listenerUbicacion);
            listenerUbicacion = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        detenerUbicacion();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        }
    }
}
