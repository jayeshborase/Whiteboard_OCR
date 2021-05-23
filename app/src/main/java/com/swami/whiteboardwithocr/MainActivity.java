package com.swami.whiteboardwithocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Canvas_View view;
    Bitmap bitmap;
    public static final String TESS_DATA = "/tessdata";
    private TessBaseAPI tessBaseAPI;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        view = new Canvas_View(this,null);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.init(metrics);
        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            tessBaseAPI = new TessBaseAPI();
            //tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.);
            //dataPath =getApplicationContext().getFilesDir().getPath();
            //tessBaseAPI.init(dataPath, "eng");
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024) {
            if (resultCode == Activity.RESULT_OK) {
                prepareTessData();
                //startOCR(outputFileDir);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
    }

    public void screeShort(){
        View root = getWindow().getDecorView();
        root.setDrawingCacheEnabled(true);
        root.setCameraDistance(6f);
        bitmap = Bitmap.createBitmap(root.getDrawingCache());
        root.setDrawingCacheEnabled(false);

        text = this.getText(bitmap);

        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    private void prepareTessData(){
//        try{
//            File dir = getExternalFilesDir(TESS_DATA);
//            if(!dir.exists()){
//                if (!dir.mkdir()) {
//                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
//                }
//            }
//            String[] fileList = getAssets().list("");
//            for(String fileName : fileList){
//                String pathToDataFile = dir + "/" + fileName;
//                if(!(new File(pathToDataFile)).exists()){
//                    InputStream in = getAssets().open(fileName);
//                    OutputStream out = new FileOutputStream(pathToDataFile);
//                    byte [] buff = new byte[1024];
//                    int len ;
//                    while(( len = in.read(buff)) > 0){
//                        out.write(buff,0,len);
//                    }
//                    in.close();
//                    out.close();
//                }
//            }
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                AssetManager manager = MainActivity.this.getAssets();
//                OutputStream out = null;
//                try {
//                    InputStream in = manager.open("eng.traineddata");
//                    //String fileName = getAssets().toString();
//                    String tessPath = getApplicationContext().getFilesDir() + "/tesseract/tessdata";
//                    File tessFolder = new File(tessPath);
//                    if(!tessFolder.exists()){
//                        out = new FileOutputStream(tessPath);
//                        byte [] buff = new byte[1024];
//                        int len ;
//                        while(( len = in.read(buff)) > 0){
//                            out.write(buff,0,len);
//                            len = in.read(buff);
//                        }
//                    }
//                    else{
//                        Toast.makeText(MainActivity.this, "File is exists", Toast.LENGTH_SHORT).show();
//                    }
//                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }finally {
//                    try {
//                        if (out != null){
//                            out.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                boolean fileExistFlag = false;
                AssetManager assetManager = getApplicationContext().getAssets();

                String dstPathDir = "/tesseract/tessdata/";

                String srcFile = "eng.traineddata";
                InputStream inFile = null;

                dstPathDir = getApplicationContext().getFilesDir() + dstPathDir;
                String dstInitPathDir = getApplicationContext().getFilesDir() + "/tesseract";
                String dstPathFile = dstPathDir + srcFile;
                FileOutputStream outFile = null;

                try {
                    inFile = assetManager.open(srcFile);

                    File f = new File(dstPathDir);

                    if (!f.exists()) {
                        if (!f.mkdirs()) {
                            Toast.makeText(getApplicationContext(), srcFile + " can't be created.", Toast.LENGTH_SHORT).show();
                        }
                        outFile = new FileOutputStream(new File(dstPathFile));
                    } else {
                        fileExistFlag = true;
                    }

                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();

                } finally {

                    if (fileExistFlag) {
                        try {
                            if (inFile != null) inFile.close();
                            tessBaseAPI.init(dstInitPathDir, "eng");
                            return;

                        } catch (Exception ex) {
                            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (inFile != null && outFile != null) {
                        try {
                            //copy file
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = inFile.read(buf)) != -1) {
                                outFile.write(buf, 0, len);
                            }
                            inFile.close();
                            outFile.close();
                            tessBaseAPI.init(dstInitPathDir, "eng");
                        } catch (Exception ex) {
                            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), srcFile + " can't be read.", Toast.LENGTH_SHORT).show();
                    }
                }
                System.out.println(dstInitPathDir);
            }
        };
        new Thread(runnable).start();
    }

    private String getText(Bitmap bitmap){
        String dataPath;

        try {
            tessBaseAPI.setImage(bitmap);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
            System.out.println(retStr);
            Toast.makeText(this, retStr, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        tessBaseAPI.end();
        return retStr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.pen:
                view.pen();
                Toast.makeText(this, "Draw", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.eraser:
                view.erase();
                Toast.makeText(this, "Erase", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.ocr:
                screeShort();
                //Toast.makeText(this, "Erase", Toast.LENGTH_SHORT).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}