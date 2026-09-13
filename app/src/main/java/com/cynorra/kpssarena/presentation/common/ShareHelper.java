package com.cynorra.kpssarena.presentation.common;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.cynorra.kpssarena.R;
import java.io.File;
import java.io.FileOutputStream;

/** Basari kartlarini Instagram/WhatsApp Hikayesi olarak veya genel bir gonderi olarak paylasir. */
public class ShareHelper {
    private static final String INSTAGRAM_PACKAGE = "com.instagram.android";

    public static void shareToStory(Fragment fragment, Bitmap bitmap) {
        Context context = fragment.requireContext();
        Uri imageUri = saveBitmapToCache(context, bitmap);
        if (imageUri == null) return;
        if (isPackageInstalled(context, INSTAGRAM_PACKAGE)) {
            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("interactive_asset_uri", imageUri);
            intent.setPackage(INSTAGRAM_PACKAGE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.grantUriPermission(INSTAGRAM_PACKAGE, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fragment.startActivity(intent);
        } else {
            shareImageGeneric(fragment, imageUri, null);
        }
    }
    public static void shareAsPost(Fragment fragment, String text, Bitmap bitmap) {
        if (bitmap != null) {
            Uri imageUri = saveBitmapToCache(fragment.requireContext(), bitmap);
            if (imageUri != null) {
                shareImageGeneric(fragment, imageUri, text);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        fragment.startActivity(Intent.createChooser(intent, fragment.getString(R.string.action_share_generic)));
    }
    private static void shareImageGeneric(Fragment fragment, Uri imageUri, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        if (text != null) intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fragment.startActivity(Intent.createChooser(intent, fragment.getString(R.string.action_share_generic)));
    }
    private static Uri saveBitmapToCache(Context context, Bitmap bitmap) {
        try {
            File dir = new File(context.getCacheDir(), "shared_images");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "share_card_" + System.currentTimeMillis() + ".png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } catch (Exception e) {
            return null;
        }
    }
    private static boolean isPackageInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
