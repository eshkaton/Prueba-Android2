package com.devst.semana7;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Mapa simple dibujado con Canvas (sin API key de Google).
 * Proyección equirectangular: dibuja una grilla de latitud/longitud y un marcador.
 */
public class MapaView extends View {

    private double lat = 0, lng = 0;
    private final Paint fondo = new Paint();
    private final Paint grilla = new Paint();
    private final Paint ejes = new Paint();
    private final Paint marcador = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint texto = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MapaView(Context c, AttributeSet a) {
        super(c, a);
        fondo.setColor(Color.parseColor("#CFE8F7"));
        grilla.setColor(Color.parseColor("#9CC5DD"));
        grilla.setStrokeWidth(1.5f);
        ejes.setColor(Color.parseColor("#4F8FB5"));
        ejes.setStrokeWidth(3f);
        marcador.setColor(Color.parseColor("#D32F2F"));
        texto.setColor(Color.parseColor("#1B3A4B"));
        texto.setTextSize(30f);
    }

    public void setCoordenadas(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        invalidate(); // vuelve a dibujar
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        canvas.drawRect(0, 0, w, h, fondo);

        // Grilla cada 30° y ejes (ecuador y meridiano 0)
        for (int g = -180; g <= 180; g += 30) {
            float x = (float) ((g + 180) / 360.0 * w);
            canvas.drawLine(x, 0, x, h, g == 0 ? ejes : grilla);
        }
        for (int g = -90; g <= 90; g += 30) {
            float y = (float) ((90 - g) / 180.0 * h);
            canvas.drawLine(0, y, w, y, g == 0 ? ejes : grilla);
        }

        // Marcador
        float px = (float) ((lng + 180) / 360.0 * w);
        float py = (float) ((90 - lat) / 180.0 * h);
        canvas.drawCircle(px, py, 18f, marcador);
        canvas.drawCircle(px, py, 6f, fondo);
        canvas.drawText(String.format(java.util.Locale.US, "%.4f, %.4f", lat, lng),
                Math.min(px + 26, w - 260), Math.max(py - 20, 36), texto);
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        int w = MeasureSpec.getSize(wSpec);
        setMeasuredDimension(w, w / 2); // relación 2:1 (360° x 180°)
    }
}
