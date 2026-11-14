package com.cynorra.kpssarena.presentation.common;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.core.content.ContextCompat;
import com.cynorra.kpssarena.R;

/** Instagram/WhatsApp Hikaye (Story) formatinda (9:16) paylasilabilir bir basari karti uretir. */
public class ShareCardGenerator {
    private static final int WIDTH = 1080;
    private static final int HEIGHT = 1920;

    public static Bitmap generateStoryCard(Context context, String title, String scoreValue,
                                            String scoreLabel, String ctaText) {
        return generateStoryCard(context, title, scoreValue, scoreLabel, ctaText, null);
    }

    /** @param inviteLine optional line such as "Davet kodum: AB12CD34" drawn under the CTA */
    public static Bitmap generateStoryCard(Context context, String title, String scoreValue,
                                            String scoreLabel, String ctaText, String inviteLine) {
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
        if (inviteLine != null && !inviteLine.isEmpty()) {
            textPaint.setTextSize(36f);
            canvas.drawText(inviteLine, WIDTH / 2f, HEIGHT - 100f, textPaint);
        }

        return bitmap;
    }

    /**
     * 1080x1350 (4:5) question card for feeds/stories: category chip, question, A-D options,
     * a "can you solve it?" hook and the invite line. The answer is NOT shown so friends have to play.
     */
    public static Bitmap generateQuestionCard(Context context, String category, String question,
                                               String[] options, String hook, String ctaText, String inviteLine) {
        final int w = 1080;
        final int h = 1350;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int secondary = ContextCompat.getColor(context, R.color.secondary);
        int secondaryContainer = ContextCompat.getColor(context, R.color.secondary_container);
        int primary = ContextCompat.getColor(context, R.color.primary);
        int onSecondary = ContextCompat.getColor(context, R.color.on_secondary);

        Paint bg = new Paint();
        bg.setShader(new LinearGradient(0, 0, 0, h, secondary, secondaryContainer, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, bg);

        TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        tp.setColor(primary);
        tp.setTextSize(48f);
        canvas.drawText(context.getString(R.string.share_story_app_name), 70f, 110f, tp);

        tp.setTextSize(38f);
        tp.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(category == null ? "" : category, w - 70f, 110f, tp);
        tp.setTextAlign(Paint.Align.LEFT);

        float y = 190f;
        tp.setColor(onSecondary);
        tp.setTypeface(Typeface.DEFAULT_BOLD);
        tp.setTextSize(50f);
        y = drawWrapped(canvas, question, tp, 70, y, w - 140, 9);
        y += 40f;

        tp.setTypeface(Typeface.DEFAULT);
        tp.setTextSize(42f);
        String[] letters = {"A) ", "B) ", "C) ", "D) "};
        Paint box = new Paint(Paint.ANTI_ALIAS_FLAG);
        box.setColor(0x33FFFFFF);
        for (int i = 0; i < 4 && options != null && i < options.length; i++) {
            if (y > h - 330f) break;
            StaticLayout layout = layout(letters[i] + options[i], tp, w - 180, 2);
            float boxHeight = layout.getHeight() + 40f;
            canvas.drawRoundRect(70f, y, w - 70f, y + boxHeight, 28f, 28f, box);
            canvas.save();
            canvas.translate(100f, y + 20f);
            layout.draw(canvas);
            canvas.restore();
            y += boxHeight + 22f;
        }

        tp.setColor(primary);
        tp.setTypeface(Typeface.DEFAULT_BOLD);
        tp.setTextSize(44f);
        tp.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(hook, w / 2f, h - 200f, tp);
        tp.setColor(onSecondary);
        tp.setTextSize(36f);
        canvas.drawText(ctaText, w / 2f, h - 140f, tp);
        if (inviteLine != null && !inviteLine.isEmpty()) {
            tp.setTypeface(Typeface.DEFAULT);
            tp.setTextSize(32f);
            canvas.drawText(inviteLine, w / 2f, h - 85f, tp);
        }
        return bitmap;
    }

    private static StaticLayout layout(String text, TextPaint paint, int width, int maxLines) {
        return StaticLayout.Builder.obtain(text, 0, text.length(), paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(8f, 1f)
                .setMaxLines(maxLines)
                .setEllipsize(android.text.TextUtils.TruncateAt.END)
                .build();
    }

    private static float drawWrapped(Canvas canvas, String text, TextPaint paint, int x, float y, int width, int maxLines) {
        StaticLayout layout = layout(text == null ? "" : text, paint, width, maxLines);
        canvas.save();
        canvas.translate(x, y);
        layout.draw(canvas);
        canvas.restore();
        return y + layout.getHeight();
    }
}
