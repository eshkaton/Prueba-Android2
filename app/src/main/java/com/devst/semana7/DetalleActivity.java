package com.devst.semana7;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * INTENT EXPLÍCITO #1: MainActivity → DetalleActivity (con datos extra).
 * Además usa un Thread secundario para simular la carga del detalle sin
 * bloquear el hilo principal (UI).
 */
public class DetalleActivity extends AppCompatActivity {

    private Thread hiloCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        TextView txtNombre = findViewById(R.id.txtDetalleNombre);
        TextView txtDescripcion = findViewById(R.id.txtDetalleDescripcion);
        TextView txtCoords = findViewById(R.id.txtDetalleCoords);
        ProgressBar progreso = findViewById(R.id.progresoDetalle);
        View contenido = findViewById(R.id.contenidoDetalle);
        Button btnVolver = findViewById(R.id.btnVolverDetalle);

        // Recibimos los extras enviados con putExtra()
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        // Validación: si no llegaron datos, no mostramos nada inválido
        if (nombre == null || descripcion == null) {
            txtNombre.setText("Sin datos para mostrar");
            progreso.setVisibility(View.GONE);
            contenido.setVisibility(View.VISIBLE);
        } else {
            contenido.setVisibility(View.INVISIBLE);

            // THREAD: simula una consulta lenta (red/base de datos) en segundo plano
            hiloCarga = new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    return; // la Activity se cerró, terminamos el hilo
                }
                // Solo el hilo principal puede tocar la interfaz
                runOnUiThread(() -> {
                    txtNombre.setText("📍 " + nombre);
                    txtDescripcion.setText(descripcion);
                    txtCoords.setText("Latitud: " + lat + "\nLongitud: " + lng);
                    progreso.setVisibility(View.GONE);
                    contenido.setVisibility(View.VISIBLE);
                });
            });
            hiloCarga.start();
        }

        btnVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hiloCarga != null) hiloCarga.interrupt(); // evita actualizar una pantalla destruida
    }
}
