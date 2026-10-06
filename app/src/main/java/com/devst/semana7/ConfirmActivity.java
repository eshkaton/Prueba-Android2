package com.devst.semana7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/** Muestra los datos recibidos y devuelve una respuesta a FormActivity. */
public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        TextView txtResumen = findViewById(R.id.txtResumen);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        Intent datos = getIntent();
        String nombre = datos.getStringExtra("nombre");

        txtResumen.setText(
                "👤 Nombre: " + nombre
                + "\n📧 Correo: " + datos.getStringExtra("email")
                + "\n📞 Teléfono: " + datos.getStringExtra("telefono")
                + "\n🎂 Edad: " + datos.getIntExtra("edad", 0)
                + "\n💬 Mensaje: " + datos.getStringExtra("mensaje"));

        btnConfirmar.setOnClickListener(v -> {
            Intent respuesta = new Intent();
            respuesta.putExtra("respuesta", "Datos de " + nombre + " confirmados correctamente");
            setResult(RESULT_OK, respuesta);   // devolvemos el resultado
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
