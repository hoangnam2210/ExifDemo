package inno.exifdemo.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

import inno.exifdemo.Models.GPSTracker;
import inno.exifdemo.Models.MyExif;
import inno.exifdemo.R;
import inno.exifdemo.Util.Converter;

public class MainActivity extends AppCompatActivity {

    MyExif myExif;
    GPSTracker gps;

    TextView tvResult;
    Button btnSet;
    Button btnSelect;
    Button btnShowLocation;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    void initUI(){

        tvResult = (TextView) findViewById(R.id.tvResult);
        btnSet = (Button) findViewById(R.id.btnSet);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        ivImage = (ImageView) findViewById(R.id.ivImage);

        initPermission();
        int permissionCheckRead = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckRead == 0) {
            //Get exif from path string by URI
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/IMG_20170322_172253.jpg");
            if (file.exists()){
                Uri imageFilePath = new Converter().convertFileToUri(getBaseContext(), file);

                ivImage.setImageBitmap(new Converter().convertUriToBitmap(getBaseContext(), imageFilePath));

                myExif = new MyExif(imageFilePath, this);
                tvResult.setText(myExif.getSummary());
            }

            //Get exif info from path string by string
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/IMG_20170322_172253.jpg"; //OK
//            myExif = new MyExif(path, getParent());
//            tvResult.setText(myExif.getSummary());
        }

        gps = new GPSTracker(getBaseContext());

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gps.canGetLocation()) {

                    myExif.updateGeoTagFromLocation(gps.getLatitude(), gps.getLongitude());

                    // \n is for new line
                    gps.stopUsingGPS();
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                initPermission();
                int permissionCheckRead = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheckRead == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 0);
                }
            }
        });

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    gps.stopUsingGPS();
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();

            ivImage.setImageBitmap(new Converter().convertUriToBitmap(getBaseContext(), targetUri));

            myExif = new MyExif(targetUri, this);
            tvResult.setText(myExif.getSummary());
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {}
                else {}
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {}
                else {}
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


}
