package com.example.myruns1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE =  1;
    private Uri tempImgUri;
    private ImageView imageView;
    private String tempImgFileName = "profile.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }
    private void checkPermissions()
    {
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    public void onChangeButton(View view){
        imageView = (ImageView)findViewById(R.id.imageProfile);
        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        tempImgUri = FileProvider.getUriForFile(this, "com.example.myruns1", tempImgFile);
        imageView.setImageURI(tempImgUri);
        Log.d("gwang","here1");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        Log.d("gwang", "here2");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //#5
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != Activity.RESULT_OK) return;

        //#4
        if(requestCode == CAMERA_REQUEST_CODE){
            //#6
            Crop.of(tempImgUri, tempImgUri).asSquare().start(this);
            //~~~~ run the code ~~~~
        }else if(requestCode == Crop.REQUEST_CROP){//#7
            Uri selectedImgUri = Crop.getOutput(data);
            imageView.setImageURI(null);
            imageView.setImageURI(selectedImgUri);
            //***
            //line = tempImgUri.getPath();
            //textView.setText(line);
            //***
        }
    }

}