package com.example.loansharkapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageProcessor {
    //Convert bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    //Convert byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    //Please use thread to prevent UI lagging when implementing this function
    public static String saveToInternalStorage(Bitmap bitmap, Context context, String name) {
        int quality = 100; //0-100 higher value, higher quality
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        String fileName = name + ".png";
        File mypath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos); //Use JPEG format for compressed capability
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                }
            }
        return directory.getAbsolutePath() + "/" + fileName;
    }

    public static void deleteProfilePicFromInternalStorage(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String fileName = name + ".png";
        File file = new File(directory, fileName);
        boolean deleted = file.delete();
    }

    public static Bitmap convertUriToBitMap(ContentResolver cr, Uri selectedImageUri) {
        Bitmap selectedImageBitmap = null;
        try {
            selectedImageBitmap
                    = MediaStore.Images.Media.getBitmap(
                    cr,
                    selectedImageUri);
            return  getResizedBitmap(selectedImageBitmap, 800);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return selectedImageBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
