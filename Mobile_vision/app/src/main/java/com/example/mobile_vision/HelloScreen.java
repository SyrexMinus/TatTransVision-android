package com.example.mobile_vision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.mobile_vision.TranslatedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelloScreen extends AppCompatActivity {

    // Объявляем локальные переменные
    // Кнопка открытия камеры
    private ImageButton mButtonOpenCamera = null;
    // кнопка открытия файла
    private ImageButton mButtonOpenFile = null;
    // Адрес выбранного изображения
    private final int Pick_image = 1;
    // Ссылка на сфотканную картинку
    private Uri outputFileUri;

    private Switch switchTatar;
    // by default
    boolean isTatar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || (ContextCompat.checkSelfPermission(HelloScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            || ContextCompat.checkSelfPermission(HelloScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET},1);
            }
        }

        // Привяжем переменные к объектам на экране
        // кнопка открытия камеры
        mButtonOpenCamera =  findViewById(R.id.btnOpenCamera);
        // кнопка открытия файла
        mButtonOpenFile =  findViewById(R.id.btnOpenFile);

        switchTatar = findViewById(R.id.switchTatar);

        switchTatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // действия, совершаемые после нажатия на свитч
                isTatar = !isTatar;
            }
        });

        // Что будет делать приложение после нажатия кнопки открытия камеры
        mButtonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // действия, совершаемые после нажатия на кнопку
                String _path = Environment.getExternalStorageDirectory() + "make_machine_example.jpg";
                File file = new File(_path);
                outputFileUri = FileProvider.getUriForFile(HelloScreen.this, "com.example.mobile_vision.provider", file );

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
                intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult( intent, 0 );
            }
        });
        // Что будет делать приложение после нажатия кнопки открытия файла
        mButtonOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // действия, совершаемые после нажатия на кнопку
                // Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                // Тип получаемых объектов - image:
                photoPickerIntent.setType("image/*");
                // Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Обрабатываем результат выбора в галерее:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode != RESULT_CANCELED){
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    //Получаем URI изображения, преобразуем его в Bitmap
                    // Создаем объект Intent для вызова новой Activity
                    Intent intent = new Intent(HelloScreen.this, TranslatedImage.class);
                    // Передадим картинку активити
                    intent.setData(outputFileUri);
                    if(isTatar)
                        intent.putExtra("language","tat");
                    else
                        intent.putExtra("language","rus");
                    // запуск activity
                    startActivity(intent);
                }
                break;
            case Pick_image:
                if(resultCode == RESULT_OK && imageReturnedIntent!=null){
                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
                        // Создаем объект Intent для вызова новой Activity
                        Intent intent = new Intent(HelloScreen.this, TranslatedImage.class);
                        // Передадим картинку активити
                        intent.setData(imageUri);
                        if(isTatar)
                            intent.putExtra("language","tat");
                        else
                            intent.putExtra("language","rus");
                        // запуск activity
                        startActivity(intent);
                }
                break;
        }}
}}