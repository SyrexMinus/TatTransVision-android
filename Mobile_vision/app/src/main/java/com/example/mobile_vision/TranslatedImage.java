package com.example.mobile_vision;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class TranslatedImage extends AppCompatActivity {

    // Объявляем локальные переменные
    // Поле для картинки открытой пользователем
    private ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translated_image);

        // Поле для картинки открытой пользователем
        imageView = (ImageView) findViewById(R.id.imageView);

        // Полученная картинка от другого активити
        Intent gotIntent = getIntent();
        Uri imageUri = gotIntent.getData();
        String lang = gotIntent.getStringExtra("language");
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            // конвертация картинки в jpg и её отправка на сервер
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            try {
                //convert array of bytes into file
                FileOutputStream fileOuputStream =
                        new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/myJpegImage.jpeg"));
                fileOuputStream.write(byteArray);
                fileOuputStream.close();
                imageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/myJpegImage.jpeg"));
                File jpegImage = new File(Environment.getExternalStorageDirectory() + "/myJpegImage.jpeg");
                // SendImage("http://192.168.50.79:8777", jpegImage, lang);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void SendImage(String urlToConnect, File fileToUpload, String language) throws IOException {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            // connecting
            Log.e("Server", "I am connecting to server...");
            URL url = new URL(urlToConnect);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            // post method
            Map<String, String> arguments = new HashMap<>();
            arguments.put("language", language);
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));

            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Log.e("Server", "http connect...");
            http.connect();
            Log.e("Server", "Write: "+sj.toString());
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
        }
    }
}