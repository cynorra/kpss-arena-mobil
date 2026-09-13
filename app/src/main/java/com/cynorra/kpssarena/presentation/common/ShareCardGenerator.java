package com.cynorra.kpssarena.presentation.common;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import com.cynorra.kpssarena.R;

/** Instagram/WhatsApp Hikaye (Story) formatinda (9:16) paylasilabilir bir basari karti uretir. */
public class ShareCardGenerator {
    private static final int WIDTH = 1080;
    private static final int HEIGHT = 1920;

    public static Bitmap generateStoryCard(Context context, String title, String scoreValue,
                                            String scoreLabel, String ctaText) {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int secondary = ContextCompat.getColor(context, R.color.secondary);
        int secondaryContainer = ContextCompat.getColor(context, R.color.secondary_container);
        int primary = ContextCompat.getColor(context, R.color.primary);
        int onSecondary = ContextCompat.getColor(context, R.color.on_secondary);

        Paint bgPaint = new Paint();
        bgPaint.setShader(new LinearGradient(0, 0, 0, HEIGHT, secondary, secondaryContainer, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, WIDTH, HEIGHT, bgPaint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(onSecondary);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        textPaint.setTextSize(58f);
        canvas.drawText(context.getString(R.string.share_story_app_name), WIDTH / 2f, 220f, textPaint);

        textPaint.setTextSize(46f);
        canvas.drawText(title, WIDTH / 2f, 760f, textPaint);

        Paint scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scorePaint.setColor(primary);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setTextSize(240f);
        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText(scoreValue, WIDTH / 2f, 1020f, scorePaint);

        textPaint.setTextSize(42f);
        textPaint.setTypeface(Typeface.DEFAULT);
        canvas.drawText(scoreLabel, WIDTH / 2f, 1110f, textPaint);

        Paint ctaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ctaPaint.setColor(primary);
        ctaPaint.setTextAlign(Paint.Align.CENTER);
        ctaPaint.setTextSize(40f);
        ctaPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText(ctaText, WIDTH / 2f, HEIGHT - 160f, ctaPaint);

        return bitmap;
    }
}
