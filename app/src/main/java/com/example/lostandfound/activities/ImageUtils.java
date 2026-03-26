package com.example.lostandfound.activities;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ImageUtils {

    public static String copyToInternalStorage(Context ctx, Uri sourceUri) {
        try {
            File dir = new File(ctx.getFilesDir(), "item_images");
            dir.mkdirs();

            String filename = "img_" + UUID.randomUUID() + ".jpg";
            File dest = new File(dir, filename);

            try (InputStream in  = ctx.getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(dest)) {
                if (in == null) return null;
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}