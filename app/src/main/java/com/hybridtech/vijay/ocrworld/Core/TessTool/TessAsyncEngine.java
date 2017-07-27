package com.hybridtech.vijay.ocrworld.Core.TessTool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.hybridtech.vijay.ocrworld.Core.Dialogs.ImageDialog;
import com.hybridtech.vijay.ocrworld.Core.Imaging.Tools;

public class TessAsyncEngine extends AsyncTask<Object, Void, String> {

    static final String TAG = "DBG_" + TessAsyncEngine.class.getName();
    private Bitmap bmp, bitmap;
    private Activity context;

    @Override
    protected String doInBackground(Object... params) {

        try {
            if (params.length < 2) {
                Log.e(TAG, "Error passing parameter to execute - missing params");
                return null;
            }

            if (!(params[0] instanceof Activity) || !(params[1] instanceof Bitmap)) {
                Log.e(TAG, "Error passing parameter to execute(context, bitmap)");
                return null;
            }

            context = (Activity) params[0];
            bmp = (Bitmap) params[1];

            if (context == null || bmp == null) {
                Log.e(TAG, "Error passed null parameter to execute(context, bitmap)");
                return null;
            }

            int rotate = 0;

            if (params.length == 3 && params[2] != null && params[2] instanceof Integer) {
                rotate = (Integer) params[2];
            }

            if (rotate >= -180 && rotate <= 180 && rotate != 0) {
                bmp = Tools.preRotateBitmap(bmp, rotate);
                Log.d(TAG, "Rotated OCR bitmap " + rotate + " degrees");
            }

            /****************************Added**********************************
             File myDir=new File(Environment.getExternalStorageDirectory().getPath());
             myDir.mkdirs();
             String fname = "image-" + bmp + ".jpg";
             File file = new File(myDir, fname);
             ExifInterface exif = new ExifInterface(Environment.getExternalStorageDirectory().getPath()+file);
             int exifOrientation = exif.getAttributeInt(
             ExifInterface.TAG_ORIENTATION,
             ExifInterface.ORIENTATION_NORMAL);

             int rotateTest = 0;

             switch (exifOrientation) {
             case ExifInterface.ORIENTATION_ROTATE_90:
             rotateTest = 90;
             break;
             case ExifInterface.ORIENTATION_ROTATE_180:
             rotateTest = 180;
             break;
             case ExifInterface.ORIENTATION_ROTATE_270:
             rotateTest = 270;
             break;
             }

             if (rotateTest != 0) {
             int w = bitmap.getWidth();
             int h = bitmap.getHeight();

             // Setting pre rotate
             Matrix mtx = new Matrix();
             mtx.preRotate(rotateTest);

             // Rotating Bitmap & convert to ARGB_8888, required by tess
             bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
             }
             bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
             ****************************Added**********************************/

            TessEngine tessEngine = TessEngine.Generate(context);

            bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

            String result = tessEngine.detectText(bmp);

            Log.d(TAG, result);

            return result;

        } catch (Exception ex) {
            Log.d(TAG, "Error: " + ex + "\n" + ex.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        if (s == null || bmp == null || context == null)
            return;

        ImageDialog.New()
                .addBitmap(bmp)
                .addTitle(s)
                .show(context.getFragmentManager(), TAG);

        super.onPostExecute(s);
    }
}
