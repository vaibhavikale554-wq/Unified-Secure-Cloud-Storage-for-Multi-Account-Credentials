package com.rao.multiaccountcredentials;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rao.multiaccountcredentials.comman.APIs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetFaceActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView cameraPreview;
    private TextView imageCountText;
    private ProgressBar progressBar;
    private Button captureButton;

    private Camera camera;
    private int frontCameraId;
    private String userId;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String UPLOAD_URL = APIs.upload_user_image; // Replace with your API endpoint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        setContentView(R.layout.activity_get_face);

        cameraPreview = findViewById(R.id.camera_preview);
        imageCountText = findViewById(R.id.image_count_text);
        progressBar = findViewById(R.id.progress_bar);
        captureButton = findViewById(R.id.capture_button);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");

        captureButton.setOnClickListener(v -> captureImage());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            initializeSurfaceView();
        }
    }

    private void initializeSurfaceView() {
        SurfaceHolder holder = cameraPreview.getHolder();
        holder.addCallback(this);
        frontCameraId = getFrontCameraId();
    }

    private int getFrontCameraId() {
        int numCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return -1;  // No front camera found
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (frontCameraId != -1) {
                camera = Camera.open(frontCameraId);
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90); // Set the orientation to vertical (portrait)
                camera.startPreview();
            } else {
                Toast.makeText(this, "No front camera found.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("Camera", "Error setting up camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                Log.e("Camera", "Error restarting preview: " + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void captureImage() {
        if (camera == null) {
            Log.e("Camera", "Camera is not initialized.");
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the capture button to prevent multiple clicks
        captureButton.setEnabled(false);

        camera.takePicture(null, null, (data, camera) -> {
            File path = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FaceScans");
            if (!path.exists()) path.mkdirs();
            File imageFile = new File(path, "face_scan_" + userId + ".jpg");

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                out.write(data);
                Log.d("Camera", "Saved image: " + imageFile.getAbsolutePath());

                // Upload the image after saving
                new Thread(() -> uploadImage(imageFile)).start();

            } catch (IOException e) {
                Log.e("Camera", "Error saving image: " + e.getMessage());
            }

            // Restart preview and re-enable the capture button
            camera.startPreview();
            captureButton.setEnabled(true);
        });
    }

    private void uploadImage(File imageFile) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream;
        String boundary = "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            URL url = new URL(UPLOAD_URL);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"user_id\"" + lineEnd + lineEnd + userId + lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imageFile.getName() + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes(lineEnd);

            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, 1024 * 1024);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bytesRead);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, 1024 * 1024);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("Upload", "Image uploaded successfully!");
                runOnUiThread(() -> Toast.makeText(GetFaceActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show());
            } else {
                Log.e("Upload", "Server returned non-OK code: " + responseCode);
                runOnUiThread(() -> Toast.makeText(GetFaceActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            Log.e("Upload", "Exception: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(GetFaceActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSurfaceView();
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if permission is not granted
            }
        }
    }
}
