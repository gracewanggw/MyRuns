package com.example.myruns_userprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE =  1;
    public static final String TEXT_VIEW_TEXT_KEY = "text key";
    private Uri tempImgUri;
    private ImageView imageView;
    private String tempImgFileName = "xd_temp_img.jpg";
    //***
    private static final String TEXTVIEW_KEY = "textview_key";
    private TextView textView;//
    String line = "...";//
    //***

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //#1
        Util.checkPermissions(this);
        imageView = (ImageView)findViewById(R.id.imageProfile);
        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);//XD: try Environment.DIRECTORY_PICTURES instead of "null"
        tempImgUri = FileProvider.getUriForFile(this, "com.xd.demotestcamera", tempImgFile);
        imageView.setImageURI(tempImgUri);

        //***
        //textView = (TextView)findViewById(R.id.text_view);
//        if(savedInstanceState != null)
//            line = savedInstanceState.getString(TEXTVIEW_KEY);
        //textView.setText(line);
        //***
    }

    //***
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(TEXTVIEW_KEY, line);
    }
    //***

    //#2
    public void onChangePhotoClicked(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    //#3
    @Override
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
            line = tempImgUri.getPath();
            textView.setText(line);
            //***
        }
    }


}