package com.rao.multiaccountcredentials.comman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class CircularCutoutView extends View {

    private Paint blackPaint; // Paint for the black overlay
    private Paint clearPaint; // Paint to clear the circle
    private int circleRadius = 200; // Radius of the circle
    private int circleX, circleY;  // Center of the circle

    public CircularCutoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Black paint for the overlay
        blackPaint = new Paint();
        blackPaint.setColor(Color.argb(192, 0, 0, 0));// Semi-transparent black


        // Clear paint for the cutout
        clearPaint = new Paint();
        clearPaint.setColor(Color.TRANSPARENT);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setAntiAlias(true);

        // Set default circle position (center)
        post(() -> {
            circleX = getWidth() / 2;
            circleY = getHeight() / 2;
        });

        // Enable software layer to support PorterDuff
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw black overlay
        canvas.drawRect(0, 0, getWidth(), getHeight(), blackPaint);

        // Clear circular cutout
        canvas.drawCircle(circleX, circleY, circleRadius, clearPaint);
    }

    public void setCircleRadius(int radius) {
        this.circleRadius = radius;
        invalidate();
    }

    public void setCirclePosition(int x, int y) {
        this.circleX = x;
        this.circleY = y;
        invalidate();
    }
}
