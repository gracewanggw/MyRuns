package com.example.myruns1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private boolean saved;
    private Uri tempImgUri;
    private Uri saveImgUri;
    private ImageView imageView;
    private String tempImgFileName = "profile.jpg";
    public static final String SAVED_KEY = "saved_key";
    //public static final boolean SAVED_BOOLEAN = true;
    public Uri location;

    public final static String SHARED_PREFS = "sharedPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkPermissions();

        loadData();
        Log.d("gwang","onCreate " + saved);
        if(saved){
            //if(savedInstanceState.getString(SAVED_KEY).equals("true")){
                imageView = (ImageView)findViewById(R.id.imageProfile);
                File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);//XD: try Environment.DIRECTORY_PICTURES instead of "null"
                tempImgUri = FileProvider.getUriForFile(this, "com.example.myruns1", tempImgFile);
                imageView.setImageURI(tempImgUri);
            //}
        }


    }
//    @Override
//    protected void onSaveInstanceState(Bundle outState){
//        super.onSaveInstanceState(outState);
//        String value;
//        if(saved==true){
//            value = "true";
//        }
//        else{
//            value = "false";
//        }
//        outState.putString(SAVED_KEY,value);
//        Log.d("gwang", "savedInstanceState "+ outState.getString(SAVED_KEY));
//    }
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
        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        tempImgUri = FileProvider.getUriForFile(this, "com.example.myruns1", tempImgFile);
        //imageView.setImageURI(tempImgUri);
        Log.d("gwang","here1");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        Log.d("gwang", "here2");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        imageView = (ImageView)findViewById(R.id.imageProfile);
        //#5
        Log.d("gwang", "here3");
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != Activity.RESULT_OK) return;

        //#4
        if(requestCode == CAMERA_REQUEST_CODE){
            //#6
            File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
            saveImgUri = FileProvider.getUriForFile(this,"com.example.myruns1", tempImgFile);
            Crop.of(tempImgUri, saveImgUri).asSquare().start(this);
            // for the actual assignment change the second tempImgUri so that if the person cancels
            // it doesn't override it
            //~~~~ run the code ~~~~
        }else if(requestCode == Crop.REQUEST_CROP){//#7
            Uri selectedImgUri = Crop.getOutput(data);
            imageView.setImageURI(null);
            imageView.setImageURI(selectedImgUri);
            //***
            //line = tempImgUri.getPath();
            //textView.setText(line);
            //***
            location = selectedImgUri;
        }
    }

    public void onSaveButton(View view){
        saveData(true);
        onPause();
        Log.d("gwang", "save ");
        finish();
        //save user data;
    }

    public void onCancelButton(View view){
        Log.d("gwang", "here2");
        saveData(false);
        finish();
    }

    public void saveData(Boolean save){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedPreferences.edit();
        editors.putBoolean(SAVED_KEY,save);
        saved = sharedPreferences.getBoolean(SAVED_KEY,false);
        editors.apply();
        Log.d("gwang","saved data " + saved);
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        saved = sharedPreferences.getBoolean(SAVED_KEY,false);
        Log.d("gwang", "loadData " + sharedPreferences.getBoolean(SAVED_KEY,false));
    }

}