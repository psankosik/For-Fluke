package th.co.scbprotect;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.SwipeListener;

public class ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_PICK_CODE = 1003;

    ImageView profile_imageview;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);

        TextView name = findViewById(R.id.profile_tv_name);
        name.setText(pref.getString(Configuration.Pref.NAME, "no name"));

        TextView email = findViewById(R.id.profile_tv_email);
        email.setText(pref.getString(Configuration.Pref.EMAIL, "no email"));


        SwipeListener listener = new SwipeListener(this) {
            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                //finishAffinity();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        };
        findViewById(R.id.profile_linear_layout).setOnTouchListener((View.OnTouchListener) (listener));


        profile_imageview = findViewById(R.id.profile_iv_profile_picture);

        findViewById(R.id.profile_bt_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePassActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.profile_bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Are you sure to logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove(Configuration.Pref.ACCESS_TOKEN).apply();

                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        profile_imageview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Select Image");

                String[] animals = {"Take Photo", "Choose from device"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            checkPermissionCamera();
                        } else if (which == 1){
                            checkPermissionPick();
                        }
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermissionCamera(){
        Log.e("PUN","checkPermissionCamera");
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_CODE);
        } else {
            openCamera();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermissionPick(){
        Log.e("PUN","checkPermissionPick");
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_CODE);
        } else {
            pickImageFromGallery();
        }
    }

    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //openCamera();
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            profile_imageview.setImageURI(image_uri);
        } else if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            profile_imageview.setImageURI(data.getData());
        }
    }
}
