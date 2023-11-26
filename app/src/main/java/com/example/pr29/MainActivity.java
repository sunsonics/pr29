package com.example.pr29;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    Button btnWeb;
    Button btnMap;
    Button btnCall;

    private File directory;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int REQUEST_CODE_PHOTO = 1;
    private static final int REQUEST_CODE_VIDEO = 2;
    private static final String TAG = "myLogs";
    private ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWeb = findViewById(R.id.btnWeb);
        btnMap = findViewById(R.id.btnMap);
        btnCall = findViewById(R.id.btnCall);

        btnWeb.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnCall.setOnClickListener(this);
    }

    public void onClickPhoto(View view) {
        if (checkPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
            startActivityForResult(intent, REQUEST_CODE_PHOTO);
        } else {
            requestPermission();
        }
    }

    public void onClickVideo(View view) {
        if (checkPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_VIDEO));
            startActivityForResult(intent, REQUEST_CODE_VIDEO);
        } else {
            requestPermission();
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent;

        if (v.getId() == R.id.btnWeb) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com"));
            startActivity(intent);
        } else if (v.getId() == R.id.btnMap) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:55.754283,37.62002"));
            startActivity(intent);
        } else if (v.getId() == R.id.btnCall) {
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:12345"));
            startActivity(intent);
         } else if (v.getId() == R.id.btnSecondWork) {
        // Переход на вторую работу
            //Intent secondWorkIntent = new Intent(this, pippp.class);
            // startActivity(secondWorkIntent);
    }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHOTO) {
                handlePhotoResult(intent);
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                handleVideoResult(intent);
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Canceled");
        }
    }

    private void handlePhotoResult(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object obj = bundle.get("data");
            if (obj instanceof Bitmap) {
                Bitmap bitmap = (Bitmap) obj;
                ivPhoto.setImageBitmap(bitmap);
            }
        }
    }

    private void handleVideoResult(Intent intent) {
        Uri videoUri = intent.getData();
        Log.d(TAG, "Video uri: " + videoUri);
    }

    private Uri generateFileUri(int type) {
        File file;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/photo_" + System.currentTimeMillis() + ".jpg");
                break;
            case TYPE_VIDEO:
                file = new File(directory.getPath() + "/video_" + System.currentTimeMillis() + ".mp4");
                break;
            default:
                return null;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyFolder");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with camera action
                Log.d(TAG, "Permission granted");
            } else {
                // Permission denied
                Log.d(TAG, "Permission denied");
            }
        }
    }
}