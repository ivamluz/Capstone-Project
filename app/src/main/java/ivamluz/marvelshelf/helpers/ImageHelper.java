package ivamluz.marvelshelf.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by iluz on 5/15/16.
 */
public class ImageHelper {

    public static File saveBitmapToPNG(Bitmap bitmap, File directory, String filename) throws Exception {

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String formattedFilename = filename + "_" + System.currentTimeMillis() + ".png";

        File file = new File(directory, formattedFilename);
        if (file.exists()) {
            return file;
        } else {
            file.createNewFile();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90 /*ignored for PNG*/, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bitmapData);
        fileOutputStream.flush();
        fileOutputStream.close();

        return file;
    }

    public static void addImageToGallery(Context context, String filePath, String title) {

        ContentValues values = new ContentValues();

        String name = title + "_" + System.currentTimeMillis() + ".png";

        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}

