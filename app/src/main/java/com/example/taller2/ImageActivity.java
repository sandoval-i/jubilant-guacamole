package com.example.taller2;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {
    private final int PICK_CODE = 1;
    private final int CAMERA_CODE = 2;
    private final String CAMERA_RATIONALE = "Es necesario brindar permiso para acceder a la camara";
    private final String PICK_RATIONALE = "Es necesario brindar permiso para acceder al almacenamiento";

    private final int IMAGE_PICKER_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        findViewById(R.id.launchCameraButton).setOnClickListener(v -> {
            launchCamera();
        });
        findViewById(R.id.pickImageButton).setOnClickListener(v -> {
            pickImage();
        });
        imageView = findViewById(R.id.imageImageView);
    }

    private void pickImage() {
        if (!PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionHelper.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                    PICK_CODE, PICK_RATIONALE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    private void launchCamera() {
        if (!PermissionHelper.hasPermission(this, Manifest.permission.CAMERA)) {
            PermissionHelper.requestPermission(this, Manifest.permission.CAMERA,
                    CAMERA_CODE, CAMERA_RATIONALE);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == IMAGE_PICKER_REQUEST) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                imageStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
        }
    }
}