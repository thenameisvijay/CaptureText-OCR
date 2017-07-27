package com.hybridtech.vijay.ocrworld.Core.TessTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessEngine {

    static final String TAG = "DBG_" + TessEngine.class.getName();

    private Context context;

    private TessEngine(Context context) {
        this.context = context;
    }

    public static TessEngine Generate(Context context) {
        return new TessEngine(context);
    }

    public String detectText(Bitmap bitmap) {
        Log.d(TAG, "Initialization of TessBaseApi");

        TessDataManager.initTessTrainedData(context);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();

        String path = TessDataManager.getTesseractFolder();

        Log.d(TAG, "Tess folder: " + path);
        //System.out.println("directoryPath: " + directoryPath);
        tessBaseAPI.setDebug(true);

        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz");

        /*tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
                "YTREWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");*/

        tessBaseAPI.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        //tessBaseAPI.setPageSegMode(TessBaseAPI.OEM_TESSERACT_ONLY);

        boolean tessSuccess = tessBaseAPI.init(path, "eng");
        System.out.println("check path: " + tessSuccess);

        Log.d(TAG, "Ended initialization of TessEngine");
        Log.d(TAG, "Running inspection on bitmap");
        tessBaseAPI.setImage(bitmap);

        int bitmapHeight = bitmap.getHeight();
        Log.i(TAG, "Height: " + bitmapHeight);

        int bitmapWidth = bitmap.getWidth();
        Log.i(TAG, "Width: " + bitmapWidth);

        String inspection = tessBaseAPI.getUTF8Text();
        Log.d(TAG, "Captured data: " + inspection);
        tessBaseAPI.end();
        System.gc();
        return inspection;
    }

    /*public boolean init(String datapath, String language) {
        if (datapath == null)
            throw new IllegalArgumentException("Data path must not be null!");
        if (!datapath.endsWith(File.separator))
            datapath += File.separator;

        File tessdata = new File(datapath + "tessdata");
        if (!tessdata.exists() || !tessdata.isDirectory())
            throw new IllegalArgumentException("Data path must contain subfolder tessdata!");

        return nativeInit(datapath, language);
    }*/
}
