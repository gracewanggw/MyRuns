package com.example.myruns1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private EditText nameText;
    private EditText  emailText;
    private EditText  phoneText;
    private RadioButton genderFemale;
    private RadioButton genderMale;
    private EditText classNum;
    private EditText  majorText;

    private String name;
    private String email;
    private String phone;
    private int gender;
    private int classYear;
    private String major;

    public final static String SHARED_PREFS = "sharedPrefs";
    public static final String SAVED_KEY = "saved_key";
    public static final String NAME_KEY = "name_key";
    public static final String EMAIL_KEY = "email_key";
    public static final String PHONE_KEY = "phone_key";
    public static final String CLASS_KEY = "class_key";
    public static final String MAJOR_KEY = "major_key";
    public static final String GENDER_KEY = "gender_key";

    public static final int CAMERA_REQUEST_CODE =  1;
    private boolean saved;
    private Uri tempImgUri;
    private Uri saveImgUri;
    private ImageView imageView;
    private String tempImgFileName = "profile.jpg";
    private boolean took_picture = false;
    private Uri imgUri;


    public Uri location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkPermissions();

        nameText = (EditText)findViewById(R.id.nameEdit);
        emailText = (EditText)findViewById(R.id.emailEdit);
        phoneText = (EditText)findViewById(R.id.phoneEdit);
        classNum = (EditText)findViewById(R.id.classEdit);
        majorText = (EditText)findViewById(R.id.majorEdit);
        genderFemale = (RadioButton)findViewById(R.id.femaleButton);
        genderMale = (RadioButton)findViewById(R.id.maleButton);
        imageView = (ImageView)findViewById(R.id.imageProfile);
        loadData();

        Log.d("gwang","onCreate " + saved);

        if(saved){
            //File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);//XD: try Environment.DIRECTORY_PICTURES instead of "null"
            //tempImgUri = FileProvider.getUriForFile(this, "com.example.myruns1", tempImgFile);
            //imageView.setImageURI(saveImgUri);
            //imgUri=tempImgUri;

            updateViews();

        }


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
        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        tempImgUri = FileProvider.getUriForFile(this, "com.example.myruns1", tempImgFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);

        Log.d("gwang", "changing photo");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        imageView = (ImageView)findViewById(R.id.imageProfile);

        Log.d("gwang", "cropping photo");
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != Activity.RESULT_OK) return;


        if(requestCode == CAMERA_REQUEST_CODE){

            File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
            //saveImgUri = FileProvider.getUriForFile(this,"com.example.myruns1", tempImgFile);
            Crop.of(tempImgUri, tempImgUri).asSquare().start(this);

        }else if(requestCode == Crop.REQUEST_CROP){
            Uri selectedImgUri = Crop.getOutput(data);
            imageView.setImageURI(null);
            imageView.setImageURI(selectedImgUri);
            //location = selectedImgUri;
            //tempImgUri = selectedImgUri;
        }
        took_picture = true;
    }

    public void onFemaleButton(View view){
        gender = 1;
    }

    public void onMaleButton(View view){
        gender = 2;
    }

    public void onSaveButton(View view){
        //saveImgUri = tempImgUri;
        imageView.buildDrawingCache();
        Bitmap map = imageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(tempImgFileName,MODE_PRIVATE);
            map.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        saveData(true);
        onPause();
        Log.d("gwang", "save ");
        finish();
    }

    public void onCancelButton(View view){
        Log.d("gwang", "cancel");
//        if(took_picture){
//            File fdelete = new File(tempImgUri.getPath());
//            fdelete.delete();
//            tempImgUri = saveImgUri;
//        }
        finish();
    }



    public void saveData(Boolean save){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedPreferences.edit();
        editors.putBoolean(SAVED_KEY,save);
        editors.putString(NAME_KEY,nameText.getText().toString());
        editors.putString(EMAIL_KEY,emailText.getText().toString());
        editors.putString(PHONE_KEY,phoneText.getText().toString());
        //Log.d("gwang",saveImgUri.toString());
        //editors.putString("image_key",saveImgUri.toString());
        if(!classNum.getText().toString().equals("")){
            editors.putInt(CLASS_KEY, Integer.parseInt(classNum.getText().toString()));
        }
        else if(classNum.getText().toString().equals("")){
            editors.putInt(CLASS_KEY, 0);
        }

        editors.putString(MAJOR_KEY,majorText.getText().toString());
        editors.putInt(GENDER_KEY,gender);

        editors.apply();

        Toast.makeText(this,"Data saved", Toast.LENGTH_SHORT).show();
        Log.d("gwang","saved data " + saved);
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        saved = sharedPreferences.getBoolean(SAVED_KEY,false);
        name = sharedPreferences.getString(NAME_KEY,"");
        email = sharedPreferences.getString(EMAIL_KEY,"");
        phone = sharedPreferences.getString(PHONE_KEY,"");
        classYear = sharedPreferences.getInt(CLASS_KEY,0);
        major = sharedPreferences.getString(MAJOR_KEY,"");
        gender = sharedPreferences.getInt(GENDER_KEY,0);
        //saveImgUri = Uri.parse(sharedPreferences.getString("image_key",""));
        Log.d("gwang", "loadData " + sharedPreferences.getBoolean(SAVED_KEY,false));
    }

    public void updateViews(){

        try {
            FileInputStream fis = openFileInput(tempImgFileName);
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            imageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            imageView.setImageResource(R.drawable._200px_dartmouth_college_shield_svg);
        }

        nameText.setText(name);
        emailText.setText(email);
        phoneText.setText(phone);
        if(classYear>0){
            classNum.setText(classYear+"");
        }
        else{
            classNum.setText("");
        }

        majorText.setText(major);
        if(gender ==1){
            genderFemale.setChecked(true);
        }
        else if(gender == 2 ){
            genderMale.setChecked(true);
        }
        Log.d("gwang", "updatedView");
    }

}