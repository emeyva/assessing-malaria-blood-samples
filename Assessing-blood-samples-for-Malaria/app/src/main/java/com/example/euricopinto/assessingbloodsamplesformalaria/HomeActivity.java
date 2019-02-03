package com.example.euricopinto.assessingbloodsamplesformalaria;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ImageView result;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static int PICK_PHOTO_CODE = 1046;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    static {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initDebug();
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);
        Button camera = (Button)findViewById(R.id.camera_btn);
        Button gallery = (Button)findViewById(R.id.galleryBtn);

        result=(ImageView)findViewById(R.id.malaria_blood_sample);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchGalleryPicture();
            }
        });
    }
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void dispatchTakePictureIntent() {
        verifyStoragePermissions(this);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
    public void dispatchGalleryPicture() {
        verifyStoragePermissions(this);
        Intent GalleryPicture = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (GalleryPicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(GalleryPicture, PICK_PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Mat mat = new Mat();
            Bitmap bmp32 = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32, mat);
            Mat finalresult=grayScale(mat);
            finalresult=cellContour(finalresult, mat);
            Utils.matToBitmap(finalresult, bmp32);
            result.setImageBitmap(bmp32);
        }
        if (requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap d = BitmapFactory.decodeFile(picturePath);
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
            Mat mat = new Mat();
            Utils.bitmapToMat(scaled, mat);
            Mat finalresult=grayScale(mat);
            finalresult=cellContour(finalresult, mat);
            Utils.matToBitmap(finalresult, scaled);
            result.setImageBitmap(scaled);

        }
    }

    public Mat grayScale(Mat img){
        Mat gray = new Mat();
        Mat thresh = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(gray, thresh , 161, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 11, 12);
        return thresh;
    }

    public Mat cellContour(Mat thresh, Mat img){
        ArrayList cellArray = new ArrayList();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i=0; i< contours.size();i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            Mat image_roi = new Mat(img,rect);
            if (rect.area()>500) {
                img = cropContour(image_roi, img, rect.x, rect.y, rect.width, rect.height, cellArray);
                Imgproc.rectangle(img, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(255, 255, 255));
            }
        }
        /*drawActualInfected(img); ------------ Scale Incorrect ------------------*/
        return img;
    }

    public Mat cropContour(Mat cell, Mat img, Integer x, Integer y, Integer w, Integer h, ArrayList cellArray){
        /*System.out.println("Coordinates: X: "+ x + " Y:" + y);
        System.out.println(cellArray.size());*/
        try {
            cell=cellRecognition(cell, img, x, y, w, h, cellArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cell;
    }

    public Mat cellRecognition(Mat crop, Mat img, Integer x, Integer y, Integer w, Integer h, ArrayList cellArray) throws IOException {
        // Copy the resource into a temp file so OpenCV can load it
        InputStream is = getResources().openRawResource(R.raw.cell_cascade);
        File cascadeDir = getDir("cell_cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, "cell_cascade.xml");
        FileOutputStream os = new FileOutputStream(mCascadeFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        is.close();
        os.close();

        CascadeClassifier cellCascade = new
                CascadeClassifier(mCascadeFile.getAbsolutePath());
                cellCascade.load(mCascadeFile.getAbsolutePath());
        MatOfRect Cell = new MatOfRect();
        cellCascade.detectMultiScale(crop, Cell, 1.3, 5);
        String cellDetected = Cell.toString();
        Log.i("TAG",cellDetected);
        for (Rect rect : Cell.toArray()) {
            Log.i("TAG","---Infected-Cell-Detected---");
            Log.i("TAG",rect.toString());
            Imgproc.rectangle(img, new Point(rect.x+x, rect.y+y),
                    new Point(x+rect.x + 30, y+rect.y + 30),
                    new Scalar(0, 255, 0),2);
        }
        return img;
    }
    /* Scale incorrect
    public Mat drawActualInfected(Mat img){
        drawRect(img, 393, 140);
        drawRect(img, 1005, 796);
        drawRect(img, 580, 679);
        drawRect(img, 1085, 600);
        drawRect(img, 1027, 357);
        drawRect(img, 361, 309);
        drawRect(img, 769, 301);
        drawRect(img, 1103, 225);
        drawRect(img, 640, 225);
        return img;
    }
    public Mat drawRect(Mat img, Integer x, Integer y){

        Imgproc.rectangle(img, new Point(x,y),
                new Point(x + 30, y + 30),
                new Scalar(0, 0, 0),2);

        return img;
    }
    */
}
