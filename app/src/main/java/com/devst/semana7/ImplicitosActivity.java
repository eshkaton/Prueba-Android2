package com.devst.semana7;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 5 INTENTS IMPLÍCITOS (abren otras apps del sistema):
 *  1. Mapas      -> geo:0,0?q=texto
 *  2. Web        -> ACTION_VIEW con https://
 *  3. Llamar     -> ACTION_DIAL con tel:
 *  4. Correo     -> ACTION_SENDTO con mailto:
 *  5. SMS        -> ACTION_SENDTO con smsto:
 *
 * No indicamos qué app abrir: Android busca una que pueda realizar la acción.
 * Se usa try/catch con ActivityNotFoundException porque, desde Android 11,
 * resolveActivity() puede devolver null por la visibilidad de paquetes.
 */
public class ImplicitosActivity extends AppCompatActivity {

    private EditText etDireccion, etUrl, etTelefono, etCorreo, etAsunto, etCuerpo, etSmsNumero, etSmsTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implicitos);

        etDireccion = findViewById(R.id.etDireccion);
        etUrl = findViewById(R.id.etUrl);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        etAsunto = findViewById(R.id.etAsunto);
        etCuerpo = findViewById(R.id.etCuerpo);
        etSmsNumero = findViewById(R.id.etSmsNumero);
        etSmsTexto = findViewById(R.id.etSmsTexto);

        ((Button) findViewById(R.id.btnMapaTexto)).setOnClickListener(v -> abrirMapa());
        ((Button) findViewById(R.id.btnWeb)).setOnClickListener(v -> abrirWeb());
        ((Button) findViewById(R.id.btnLlamar)).setOnClickListener(v -> abrirMarcador());
        ((Button) findViewById(R.id.btnCorreo)).setOnClickListener(v -> enviarCorreo());
        ((Button) findViewById(R.id.btnSms)).setOnClickListener(v -> enviarSms());
        ((Button) findViewById(R.id.btnVolverImpl)).setOnClickListener(v -> finish());
    }

    // 1. Google Maps con una dirección escrita
    private void abrirMapa() {
        if (!Validador.noVacio(etDireccion, "Escribe una dirección o lugar")) return;
        String q = etDireccion.getText().toString().trim();
        lanzar(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(q))),
                "No hay una app de mapas instalada");
    }

    // 2. Navegador con una URL
    private void abrirWeb() {
        String url = Validador.urlValida(etUrl);
        if (url == null) return;
        lanzar(new Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                "No hay un navegador instalado");
    }

    // 3. Marcador telefónico (no requiere permiso CALL_PHONE porque no llama solo)
    private void abrirMarcador() {
        if (!Validador.telefono(etTelefono)) return;
        String numero = Validador.limpiarTelefono(etTelefono.getText().toString());
        lanzar(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numero)),
                "No hay una app de teléfono");
    }

    // 4. Correo con destinatario, asunto y cuerpo prellenados
    private void enviarCorreo() {
        boolean ok = Validador.noVacio(etAsunto, "Escribe un asunto");
        ok &= Validador.email(etCorreo);
        if (!ok) return;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // solo apps de correo
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{etCorreo.getText().toString().trim()});
        intent.putExtra(Intent.EXTRA_SUBJECT, etAsunto.getText().toString().trim());
        intent.putExtra(Intent.EXTRA_TEXT, etCuerpo.getText().toString().trim());
        lanzar(intent, "No hay una app de correo instalada");
    }

    // 5. SMS prellenado (el usuario decide si lo envía)
    private void enviarSms() {
        boolean ok = Validador.noVacio(etSmsTexto, "Escribe el mensaje");
        ok &= Validador.telefono(etSmsNumero);
        if (!ok) return;

        String numero = Validador.limpiarTelefono(etSmsNumero.getText().toString());
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + numero));
        intent.putExtra("sms_body", etSmsTexto.getText().toString().trim());
        lanzar(intent, "No hay una app de mensajes instalada");
    }

    /** Lanza el intent y avisa si ninguna app puede atenderlo. */
    private void lanzar(Intent intent, String mensajeError) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
        }
    }
}
