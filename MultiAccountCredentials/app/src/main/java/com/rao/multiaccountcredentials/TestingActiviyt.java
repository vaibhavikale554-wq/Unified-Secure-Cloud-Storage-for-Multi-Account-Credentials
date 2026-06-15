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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.rao.multiaccountcredentials.comman.ApiService;
import com.rao.multiaccountcredentials.comman.CircularCutoutView;
import com.rao.multiaccountcredentials.comman.CustomSnackbar;
import com.rao.multiaccountcredentials.comman.RetrofitClient;
import com.rao.multiaccountcredentials.comman.VerificationResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestingActiviyt extends AppCompatActivity {

    private PreviewView previewView;
    private ProgressBar progressBar;
    private Button verify_button;
    private ImageCapture imageCapture;
    private FrameLayout root_layout_testing;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_testing_activiyt);
        CircularCutoutView cutoutView = findViewById(R.id.cutout_view);
        root_layout_testing = findViewById(R.id.root_layout_testing);
        previewView = findViewById(R.id.previewView);
        progressBar = findViewById(R.id.progressBar);
        verify_button = findViewById(R.id.verify_button);

        cutoutView.setCircleRadius(500);
        cutoutView.setCirclePosition(540, 700);

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();

        verify_button.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            if (isConnected) {
                progressBar.setVisibility(View.VISIBLE);
                vibratePhone();
                captureAndVerify();
            } else {
                CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "No Internet", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
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
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
                CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "Something went wrong", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureAndVerify() {
        if (imageCapture == null) {
            CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "Something went wrong", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            return;
        }

        // Prepare file for image capture
        File photoFile = new File(getCacheDir(), "captured_image.jpg");

        // Capture image
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                // Compress the image to reduce size
                compressAndSendImage(photoFile);
            }

            @Override
            public void onError(ImageCaptureException exception) {
                CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "Something went wrong", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            }
        });
    }

    private void compressAndSendImage(File originalFile) {
        try {
            // Load original bitmap
            Bitmap originalBitmap = BitmapFactory.decodeFile(originalFile.getAbsolutePath());

            // Resize the bitmap to reduce dimensions
            int targetWidth = 300; // Adjust width
            int targetHeight = 300; // Adjust height
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

            // Compress the bitmap to JPEG format and save to a temporary file
            File compressedFile = new File(getCacheDir(), "compressed_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(compressedFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream); // Adjust quality (70–85 works well)
            outputStream.flush();
            outputStream.close();

            // Check file size and log it
            long fileSizeInKB = compressedFile.length() / 1024;
//            if (fileSizeInKB < 9 || fileSizeInKB > 12) {
//                Toast.makeText(this, "Final image size: " + fileSizeInKB + "KB", Toast.LENGTH_SHORT).show();
//            }

            // Send compressed image to the server
            sendImageToServer(compressedFile);
        } catch (Exception e) {
            e.printStackTrace();
            CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "Image compression failed", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
        }
    }

    private void sendImageToServer(File imageFile) {
        verify_button.setEnabled(false);
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), ""+sharedPreferences.getString("username",null));

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<VerificationResult> call = apiService.verifyFaces(imagePart, usernamePart);

        call.enqueue(new Callback<VerificationResult>() {
            @Override
            public void onResponse(Call<VerificationResult> call, Response<VerificationResult> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
//                    Toast.makeText(TestingActiviyt.this, "Face verified successfully!", Toast.LENGTH_SHORT).show();
                    if (getIntent().getStringExtra("state").equals("view")){
                        Intent intent = new Intent(TestingActiviyt.this, ViewCredentialsActivity.class);
                        intent.putExtra("data", getIntent().getStringExtra("data"));
                        intent.putExtra("id", getIntent().getStringExtra("id"));
                        intent.putExtra("name", getIntent().getStringExtra("name"));
                        intent.putExtra("url", getIntent().getStringExtra("url"));

                        startActivity(intent);
                        finish();
                    } else if (getIntent().getStringExtra("state").equals("reset")) {
                                    Intent intent=new Intent(TestingActiviyt.this,FaceRegistrationScreen.class);
                                    intent.putExtra("state","2");
                                startActivity(intent);
                                finish();
                    }
                } else {
                    onBackPressed();
                    verify_button.setEnabled(true);
                    finish();
                    Toast.makeText(TestingActiviyt.this, "Face not recognized", Toast.LENGTH_SHORT).show();
//                    CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "Face not recognized", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<VerificationResult> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TestingActiviyt.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                onBackPressed();
                verify_button.setEnabled(true);
                finish();
//                CustomSnackbar.showSnackbar(TestingActiviyt.this, root_layout_testing, "Cannot reach server", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
