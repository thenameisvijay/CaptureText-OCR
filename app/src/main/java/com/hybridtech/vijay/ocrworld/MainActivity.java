package com.hybridtech.vijay.ocrworld;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.hybridtech.vijay.ocrworld.Core.CameraEngine;
import com.hybridtech.vijay.ocrworld.Core.ExtraViews.FocusBoxView;
import com.hybridtech.vijay.ocrworld.Core.Imaging.Tools;
import com.hybridtech.vijay.ocrworld.Core.TessTool.TessAsyncEngine;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener,
        Camera.PictureCallback, Camera.ShutterCallback {

    static final String TAG = "DBG_" + MainActivity.class.getName();

    Button shutterButton;
    Button focusButton;
    FocusBoxView focusBox;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;

    private static final int REQUEST_CAMERA_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "Surface Created - starting camera");
        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }
        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera engine already on");
            return;
        }
        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();
        Log.d(TAG, "Camera engine started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
        shutterButton = (Button) findViewById(R.id.shutter_button);
        focusBox = (FocusBoxView) findViewById(R.id.focus_box);
        focusButton = (Button) findViewById(R.id.focus_button);

        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraFrame.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        if (v == shutterButton) {
            if (cameraEngine != null && cameraEngine.isOn()) {
                cameraEngine.takeShot(this, this, this);
            }
        }

        if (v == focusButton) {
            if (cameraEngine != null && cameraEngine.isOn()) {
                cameraEngine.requestFocus();
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        /*Log.d(TAG, "Picture taken");
        if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }*/
        Log.d(TAG, "Picture taken");

        File photo = new File(Environment.getExternalStorageDirectory(), String.format("/DCIM/%d.bmp", System.currentTimeMillis()));
        System.out.println("The name of the photo is " + photo.getName());
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(data);
            Log.d(TAG, "Inside Try");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }


        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getBox());
        int bc = bmp.getByteCount();
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        Log.d(TAG, "bite count: " + bc + " height: " + height + " width:" + width);

        Log.d(TAG, "Got bitmap");
        Log.d(TAG, "Initialization of TessBaseApi");

        /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);*/

        TessBaseAPI mTess = new TessBaseAPI();
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        String language = "eng";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists()) {
            dir.mkdirs();
            Log.d(TAG, "Inside if condition");
        }
        new TessAsyncEngine().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);
    }

    @Override
    public void onShutter() {

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        *//*Intent intent = new Intent(this, ClassUsingCamera);
                        startActivity(intent);*//*
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }*/
}