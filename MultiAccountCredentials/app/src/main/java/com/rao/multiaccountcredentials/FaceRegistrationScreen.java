package com.rao.multiaccountcredentials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.rao.multiaccountcredentials.Models.RegistrationResponse;
import com.rao.multiaccountcredentials.Models.ResetImagesResponse;
import com.rao.multiaccountcredentials.comman.ApiService;
import com.rao.multiaccountcredentials.comman.CircularCutoutViewRegistration;
import com.rao.multiaccountcredentials.comman.CustomSnackbar;
import com.rao.multiaccountcredentials.comman.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceRegistrationScreen extends AppCompatActivity {
    private static final int TOTAL_IMAGES = 10;
    Button Reset_button,confirm_button;
    TextView text_message;

    private int PROGRESS =0;
    private int imageCount = 0;
    Button clickButtonRegFace;
    private List<File> imageFiles = new ArrayList<>();
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    FrameLayout root_layout_registion;
    private String username = "rao12442111"; // Replace with actual username input
    private String fullName = "Om Wanve"; // Replace with actual full name input
    private String password = "rao124421"; // Replace with actual password input
    CircularCutoutViewRegistration cutoutView;
    String state="1";
    String username_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_registration_screen);
        Reset_button=findViewById(R.id.Reset_button);
        confirm_button=findViewById(R.id.confirm_button);
        text_message=findViewById(R.id.text_message);
        root_layout_registion=findViewById(R.id.root_layout_registion);
        state=getIntent().getStringExtra("state");

        if (state.equals("1")){
            username=getIntent().getStringExtra("username");
            fullName=getIntent().getStringExtra("full_name");
            password=getIntent().getStringExtra("password");
        }
        else if(state.equals("2")){
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            username_id=sharedPreferences.getString("username",null);

        }





        // Setup UI elements
   cutoutView = findViewById(R.id.cutout_view_registration);
         clickButtonRegFace = findViewById(R.id.click_button_regface);

        cutoutView.setCircleRadius(500);
        cutoutView.setCirclePosition(540, 700);
        cutoutView.setProgress(0);
        Reset_button.setOnClickListener(v->{
            imageFiles.clear();
            PROGRESS =0;
            imageCount = 0;
            cutoutView.setProgress(PROGRESS);
            cutoutView.setCirclePosition(540, 700);
            text_message.setText("Please click 10 frames to complete your face registration.");
            clickButtonRegFace.setVisibility(View.VISIBLE);
            Reset_button.setVisibility(View.GONE);
            confirm_button.setVisibility(View.GONE);

        });
        confirm_button.setOnClickListener(v->{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            if (isConnected) {
                if (state.equals("1")){
                    uploadDataToServer();

                }
                else if (state.equals("2")) {

                    resetImagesOnServer(username_id);
                }
            } else {
                CustomSnackbar.showSnackbar(FaceRegistrationScreen.this, root_layout_registion, "No Internet", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            }

        });

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();

        clickButtonRegFace.setOnClickListener(v -> {
            cutoutView.setCirclePosition(540, 700);
            vibratePhone();

            if (imageCount < TOTAL_IMAGES) {
                captureImage();
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(((PreviewView) findViewById(R.id.previewView_registion)).getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureImage() {
        File imageFile = new File(getCacheDir(), "image_" + (imageCount + 1) + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(imageFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                compressImage(imageFile);
                imageFiles.add(imageFile);
                imageCount++;
                if (imageCount <=TOTAL_IMAGES) {
                    PROGRESS=PROGRESS+10;
                    cutoutView.setProgress(PROGRESS);
                    if (PROGRESS==100){
                        text_message.setText("Are you sure you want to register this face? Once registered, it will be used for authentication.");
                        confirm_button.setVisibility(View.VISIBLE);
                        Reset_button.setVisibility(View.VISIBLE);
                        clickButtonRegFace.setVisibility(View.GONE);


                    }
//                    Toast.makeText(FaceRegistrationScreen.this, "Image " + imageCount + " captured", Toast.LENGTH_SHORT).show();
                }
//                else {
//                    Toast.makeText(FaceRegistrationScreen.this, "All images captured. Uploading...", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onError(ImageCaptureException exception) {
                Toast.makeText(FaceRegistrationScreen.this, "Error capturing image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void compressImage(File imageFile) {
        try {
            // Decode the image to a Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            // Resize the bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            // Overwrite the original file with the compressed image
            FileOutputStream fos = new FileOutputStream(imageFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos); // 50% quality
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error compressing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDataToServer() {
        for (File file : imageFiles) {
            Log.d("ImageFile", "File: " + file.getAbsolutePath());
        }
        MultipartBody.Part[] imageParts = new MultipartBody.Part[imageFiles.size()];
        for (int i = 0; i < imageFiles.size(); i++) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFiles.get(i));
            imageParts[i] = MultipartBody.Part.createFormData("images", imageFiles.get(i).getName(), imageBody);
        }

        RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody fullNamePart = RequestBody.create(MediaType.parse("text/plain"), fullName);
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<RegistrationResponse> call = apiService.registerUser(usernamePart, fullNamePart, passwordPart, imageParts);
        Toast.makeText(this, "Sending data", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    saveToSharedPreferences(username,fullName);
                    Toast.makeText(FaceRegistrationScreen.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FaceRegistrationScreen.this,HomeActivity.class));
                } else {
                    Toast.makeText(FaceRegistrationScreen.this, "Something went wrong !Please try again" + response.message(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Toast.makeText(FaceRegistrationScreen.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(200);
            }
        }
    }
    private void saveToSharedPreferences(String username,String fullName) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putBoolean("islogin",true);
        editor.putString("full_name", fullName);
        editor.apply();
    }
    private void resetImagesOnServer(String userId) {
        for (File file : imageFiles) {
            Log.d("ImageFile", "File: " + file.getAbsolutePath());
        }

        // Prepare image parts
        MultipartBody.Part[] imageParts = new MultipartBody.Part[imageFiles.size()];
        for (int i = 0; i < imageFiles.size(); i++) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFiles.get(i));
            imageParts[i] = MultipartBody.Part.createFormData("images", imageFiles.get(i).getName(), imageBody);
        }

        // Prepare user ID part
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId);

        // Create API service
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResetImagesResponse> call = apiService.resetImages(userIdPart, imageParts);

        Toast.makeText(this, "Resetting images for user", Toast.LENGTH_SHORT).show();

        // Execute API call
        call.enqueue(new Callback<ResetImagesResponse>() {
            @Override
            public void onResponse(Call<ResetImagesResponse> call, Response<ResetImagesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    Toast.makeText(FaceRegistrationScreen.this, "Face data reset successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FaceRegistrationScreen.this,HomeActivity.class));
                    finish();
//                    Toast.makeText(FaceRegistrationScreen.this, "Images reset successfully: " + message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FaceRegistrationScreen.this, "Error resetting images: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetImagesResponse> call, Throwable t) {
                Toast.makeText(FaceRegistrationScreen.this, "Failed to reset images: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
