package com.cynorra.kpssarena.presentation.home;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.cynorra.kpssarena.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * Interactive KPSS Net & Score Calculator (P3 Lisans, P93 Önlisans, P94 Ortaöğretim).
 * Uses ÖSYM's official 4-wrong-cancels-1-right net rule (Net = D - Y/4).
 */
public final class KpssCalculatorDialog {

    private KpssCalculatorDialog() {}

    public static void show(Context ctx) {
        if (ctx == null) return;
        float dp = ctx.getResources().getDisplayMetrics().density;
        int pad = (int) (18 * dp);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, (int) (10 * dp), pad, 0);

        RadioGroup rg = new RadioGroup(ctx);
        rg.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton rbP3 = new RadioButton(ctx);
        rbP3.setText(R.string.kpss_calc_p3);
        rbP3.setChecked(true);
        RadioButton rbP93 = new RadioButton(ctx);
        rbP93.setText(R.string.kpss_calc_p93);
        RadioButton rbP94 = new RadioButton(ctx);
        rbP94.setText(R.string.kpss_calc_p94);
        rg.addView(rbP3);
        rg.addView(rbP93);
        rg.addView(rbP94);
        root.addView(rg);

        EditText etGyD = makeNumInput(ctx, ctx.getString(R.string.kpss_calc_gy_d));
        EditText etGyY = makeNumInput(ctx, ctx.getString(R.string.kpss_calc_gy_y));
        EditText etGkD = makeNumInput(ctx, ctx.getString(R.string.kpss_calc_gk_d));
        EditText etGkY = makeNumInput(ctx, ctx.getString(R.string.kpss_calc_gk_y));

        root.addView(makeRow(ctx, etGyD, etGyY, dp));
        root.addView(makeRow(ctx, etGkD, etGkY, dp));

        TextView tvResult = new TextView(ctx);
        tvResult.setPadding(0, (int) (14 * dp), 0, (int) (6 * dp));
        tvResult.setTextSize(14f);
        tvResult.setGravity(Gravity.CENTER_HORIZONTAL);
        tvResult.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
        tvResult.setVisibility(View.GONE);
        root.addView(tvResult);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.kpss_calc_title)
                .setView(root)
                .setPositiveButton(R.string.kpss_calc_btn, null)
                .setNegativeButton(R.string.action_close, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    int gyD = parse(etGyD);
                    int gyY = parse(etGyY);
                    int gkD = parse(etGkD);
                    int gkY = parse(etGkY);
                    if (gyD + gyY > 60 || gkD + gkY > 60) {
                        Toast.makeText(ctx, R.string.kpss_calc_max_err, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double gyNet = Math.max(0, gyD - (gyY / 4.0));
                    double gkNet = Math.max(0, gkD - (gkY / 4.0));
                    double totalNet = gyNet + gkNet;
                    // Calibrated ÖSYM approximation for P3 / P93 / P94
                    double base = rbP3.isChecked() ? 51.5 : (rbP93.isChecked() ? 50.8 : 50.2);
                    double coeffGy = rbP3.isChecked() ? 0.415 : 0.405;
                    double coeffGk = rbP3.isChecked() ? 0.395 : 0.405;
                    double score = Math.min(100.0, Math.max(0.0, base + (gyNet * coeffGy) + (gkNet * coeffGk)));
                    if (totalNet <= 0.01) score = 0.0;

                    String typeLabel = rbP3.isChecked() ? "P3" : (rbP93.isChecked() ? "P93" : "P94");
                    tvResult.setText(ctx.getString(R.string.kpss_calc_result_fmt,
                            String.format(Locale.getDefault(), "%.2f", gyNet),
                            String.format(Locale.getDefault(), "%.2f", gkNet),
                            String.format(Locale.getDefault(), "%.2f", totalNet),
                            typeLabel,
                            String.format(Locale.getDefault(), "%.2f", score)));
                    tvResult.setVisibility(View.VISIBLE);
                }));
        dialog.show();
    }

    private static LinearLayout makeRow(Context ctx, EditText left, EditText right, float dp) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, (int) (8 * dp), 0, 0);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp1.setMarginEnd((int) (8 * dp));
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        row.addView(left, lp1);
        row.addView(right, lp2);
        return row;
    }

    private static EditText makeNumInput(Context ctx, String hint) {
        EditText et = new EditText(ctx);
        et.setHint(hint);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setMaxLines(1);
        return et;
    }

    private static int parse(EditText et) {
        String s = et.getText() != null ? et.getText().toString().trim() : "";
        if (s.isEmpty()) return 0;
        try {
            return Math.max(0, Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
