package com.rao.multiaccountcredentials.comman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class CircularCutoutViewRegistration extends View {

    private Paint blackPaint; // Paint for the black overlay
    private Paint clearPaint; // Paint to clear the circle
    private Paint progressPaint; // Paint for the progress bar

    private int circleRadius = 200; // Radius of the circle
    private int circleX, circleY;  // Center of the circle

    private int progress = 10; // Current progress (0-100)
    private int progressStrokeWidth = 20; // Stroke width of the progress bar

    public CircularCutoutViewRegistration(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Black paint for the overlay
        blackPaint = new Paint();
        blackPaint.setColor(Color.argb(192, 0, 0, 0)); // Semi-transparent black

        // Clear paint for the cutout
        clearPaint = new Paint();
        clearPaint.setColor(Color.TRANSPARENT);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setAntiAlias(true);

        // Progress paint for the border
        progressPaint = new Paint();
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressStrokeWidth);
        progressPaint.setAntiAlias(true);

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

        // Draw circular progress bar
        float startAngle = -90; // Start at the top
        float sweepAngle = (360 * progress) / 100; // Convert progress to angle

        // Maintain consistent bounding box for the arc
        float strokeOffset = progressStrokeWidth / 2f;
        canvas.drawArc(
                circleX - circleRadius - strokeOffset,
                circleY - circleRadius - strokeOffset,
                circleX + circleRadius + strokeOffset,
                circleY + circleRadius + strokeOffset,
                startAngle,
                sweepAngle,
                false,
                progressPaint
        );
    }


    // Method to update the progress
    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, 100)); // Clamp between 0 and 100
        invalidate();
    }

    // Method to set the circle radius
    public void setCircleRadius(int radius) {
        this.circleRadius = radius;
        invalidate();
    }

    // Method to set the circle position
    public void setCirclePosition(int x, int y) {
        this.circleX = x;
        this.circleY = y;
        invalidate();
    }
}
