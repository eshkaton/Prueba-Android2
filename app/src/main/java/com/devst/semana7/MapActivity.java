package com.devst.semana7;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * INTENT EXPLÍCITO #3: MainActivity → MapActivity (envía coordenadas).
 * Muestra un marcador dibujado en un mapa propio a partir de lat/lng recibidos.
 */
public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapaView mapa = findViewById(R.id.mapaView);
        TextView txtCoords = findViewById(R.id.txtMapaCoords);
        Button btnVolver = findViewById(R.id.btnVolverMapa);

        double lat = getIntent().getDoubleExtra("lat", Double.NaN);
        double lng = getIntent().getDoubleExtra("lng", Double.NaN);

        // Validación de los extras recibidos
        if (Double.isNaN(lat) || Double.isNaN(lng) || !Validador.coordenadas(lat, lng)) {
            Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mapa.setCoordenadas(lat, lng);
        txtCoords.setText("📍 Latitud: " + lat + "\n📍 Longitud: " + lng);
        btnVolver.setOnClickListener(v -> finish());
    }
}
