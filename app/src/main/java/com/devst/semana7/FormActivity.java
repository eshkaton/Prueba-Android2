package com.devst.semana7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * INTENT EXPLÍCITO #2: FormActivity → ConfirmActivity (con resultado).
 * Usa registerForActivityResult(), la API moderna que reemplaza a startActivityForResult().
 */
public class FormActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etTelefono, etEdad, etMensaje;
    private TextView txtRespuesta;

    // Se registra una sola vez; recibe la respuesta de ConfirmActivity
    private final ActivityResultLauncher<Intent> lanzadorConfirmacion =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    resultado -> {
                        if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                            String resp = resultado.getData().getStringExtra("respuesta");
                            txtRespuesta.setText("✅ " + resp);
                        } else {
                            txtRespuesta.setText("❌ El usuario canceló el envío");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmailForm);
        etTelefono = findViewById(R.id.etTelefonoForm);
        etEdad = findViewById(R.id.etEdad);
        etMensaje = findViewById(R.id.etMensaje);
        txtRespuesta = findViewById(R.id.txtRespuesta);

        Button btnEnviar = findViewById(R.id.btnEnviarForm);
        Button btnVolver = findViewById(R.id.btnVolverForm);

        btnEnviar.setOnClickListener(v -> enviar());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void enviar() {
        // Validaciones (se evalúan todas para marcar todos los errores a la vez)
        boolean ok = true;
        ok &= Validador.noVacio(etMensaje, "Escribe un mensaje");
        int edad = Validador.entero(etEdad, 1, 120);
        ok &= edad != -1;
        ok &= Validador.telefono(etTelefono);
        ok &= Validador.email(etEmail);
        ok &= Validador.noVacio(etNombre, "El nombre es obligatorio");
        if (!ok) return;

        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra("nombre", etNombre.getText().toString().trim());
        intent.putExtra("email", etEmail.getText().toString().trim());
        intent.putExtra("telefono", Validador.limpiarTelefono(etTelefono.getText().toString()));
        intent.putExtra("edad", edad);
        intent.putExtra("mensaje", etMensaje.getText().toString().trim());
        lanzadorConfirmacion.launch(intent);
    }
}
