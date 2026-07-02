package com.cynorra.kpssarena.presentation.common;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.cynorra.kpssarena.R;
public class CountdownView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private RectF oval;
    private float progress = 1f;
    private int backgroundColor;
    private int progressColor;
    private float strokeWidth;
    public CountdownView(Context context) {
        super(context);
        init(null);
    }
    public CountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(@Nullable AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CountdownVie
        
        backgroundColor = a.getColor(R.styleable.CountdownView_backgroundColor,
                ContextCompat.getColor(getContext(), R.color.outline_variant));
        progressColor = a.getColor(R.styleable.CountdownView_progressColor,
                ContextCompat.getColor(getContext(), R.color.primary));
        strokeWidth = a.getDimension(R.styleable.CountdownView_strokeWidth, 4f);
        
        a.recycle();
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        oval = new RectF();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = strokeWidth / 2f;
        oval.set(padding, padding, w - padding, h - padding);
    }
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        // Draw background circle
        canvas.drawArc(oval, 0, 360, false, backgroundPaint);
        // Draw progress arc (clockwise from top)
        float sweepAngle = 360f * progress;
        canvas.drawArc(oval, -90, sweepAngle, false, progressPaint);
        // Change color based on progress
        if (progress < 0.3f) {
            progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.error));
        } else {
            progressPaint.setColor(progressColor);
        }
    }
    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        invalidate();
    }
    public float getProgress() {
        return progress;
    }
}
