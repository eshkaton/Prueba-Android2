package com.devst.semana7;

import android.util.Patterns;
import android.widget.EditText;

/**
 * Funciones de validación reutilizables en todas las pantallas.
 * Cada método devuelve true si el dato es válido; si no, marca el error en el EditText.
 */
public class Validador {

    /** El campo no puede estar vacío. */
    public static boolean noVacio(EditText campo, String mensaje) {
        if (campo.getText().toString().trim().isEmpty()) {
            campo.setError(mensaje);
            campo.requestFocus();
            return false;
        }
        return true;
    }

    /** Email con formato válido (usa el patrón estándar de Android). */
    public static boolean email(EditText campo) {
        String texto = campo.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(texto).matches()) {
            campo.setError("Correo inválido (ej: nombre@dominio.cl)");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    /** Teléfono: opcional +, entre 8 y 15 dígitos (se ignoran espacios y guiones). */
    public static boolean telefono(EditText campo) {
        String texto = limpiarTelefono(campo.getText().toString());
        if (!texto.matches("^\\+?[0-9]{8,15}$")) {
            campo.setError("Teléfono inválido (8 a 15 dígitos, ej: +56912345678)");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static String limpiarTelefono(String texto) {
        return texto.replaceAll("[\\s\\-()]", "");
    }

    /** Si falta el esquema agrega https:// y valida que sea una URL real. */
    public static String urlValida(EditText campo) {
        String texto = campo.getText().toString().trim();
        if (!texto.startsWith("http://") && !texto.startsWith("https://")) {
            texto = "https://" + texto;
        }
        if (texto.length() < 10 || !Patterns.WEB_URL.matcher(texto).matches()) {
            campo.setError("URL inválida (ej: www.santotomas.cl)");
            campo.requestFocus();
            return null;
        }
        return texto;
    }

    /** Entero dentro de un rango. Devuelve -1 si es inválido. */
    public static int entero(EditText campo, int min, int max) {
        try {
            int valor = Integer.parseInt(campo.getText().toString().trim());
            if (valor >= min && valor <= max) return valor;
        } catch (NumberFormatException ignorada) { }
        campo.setError("Ingresa un número entre " + min + " y " + max);
        campo.requestFocus();
        return -1;
    }

    /** Coordenadas dentro del rango geográfico válido. */
    public static boolean coordenadas(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }
}
