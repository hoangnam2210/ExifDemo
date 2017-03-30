package inno.exifdemo.Models;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by nam on 23/03/2017.
 */

public class MyExif {

    private File exifFile;    //It's the file passed from constructor
    private String exifFilePath;  //file in Real Path format
    private Activity parentActivity; //Parent Activity

    private String exifFilePath_withoutext;
    private String ext;

    private ExifInterface exifInterface;
    private Boolean exifValid = false;;

    //Exif TAG
//for API Level 8, Android 2.2
    private String exif_DATETIME = "";
    private String exif_FLASH = "";
    private String exif_FOCAL_LENGTH = "";
    private String exif_GPS_DATESTAMP = "";
    private String exif_GPS_LATITUDE = "";
    private String exif_GPS_LATITUDE_REF = "";
    private String exif_GPS_LONGITUDE = "";
    private String exif_GPS_LONGITUDE_REF = "";
    private String exif_GPS_PROCESSING_METHOD = "";
    private String exif_GPS_TIMESTAMP = "";
    private String exif_IMAGE_LENGTH = "";
    private String exif_IMAGE_WIDTH = "";
    private String exif_MAKE = "";
    private String exif_MODEL = "";
    private String exif_ORIENTATION = "";
    private String exif_WHITE_BALANCE = "";

    //Constructor from path
    public MyExif(String fileString, Activity parent){
        exifFile = new File(fileString);
        parentActivity = parent;
        exifFilePath = fileString;
        prepareExif();
    }

    //Constructor from URI
    public MyExif(Uri fileUri, Activity parent){
        exifFile = new File(fileUri.toString());
        parentActivity = parent;
        exifFilePath = getRealPathFromURI(fileUri);
        prepareExif();
    }

    private void prepareExif(){

        int dotposition= exifFilePath.lastIndexOf(".");
        exifFilePath_withoutext = exifFilePath.substring(0,dotposition);
        ext = exifFilePath.substring(dotposition + 1, exifFilePath.length());

        if (ext.equalsIgnoreCase("jpg")){
            try {
                exifInterface = new ExifInterface(exifFilePath);
                readExifTag();
                exifValid = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void readExifTag(){

        exif_DATETIME = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        exif_FLASH = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        exif_FOCAL_LENGTH = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        exif_GPS_DATESTAMP = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
        exif_GPS_LATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        exif_GPS_LATITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        exif_GPS_LONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        exif_GPS_LONGITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        exif_GPS_PROCESSING_METHOD = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
        exif_GPS_TIMESTAMP = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
        exif_IMAGE_LENGTH = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        exif_IMAGE_WIDTH = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        exif_MAKE = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        exif_MODEL = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        exif_ORIENTATION = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        exif_WHITE_BALANCE = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);

    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = parentActivity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getSummary(){
        if(!exifValid){
            return ("Invalide EXIF!");
        }else{
            return( exifFilePath + " : \n" +

                    "Name without extension: " + exifFilePath_withoutext + "\n" +
                    "with extension: " + ext + "\n" +

                    //"Date Time: " + exif_DATETIME + "\n" +
                    //"Flash: " + exif_FLASH + "\n" +
                    //"Focal Length: " + exif_FOCAL_LENGTH + "\n" +
                    //"GPS Date Stamp: " + exif_GPS_DATESTAMP + "\n" +
                    "GPS Latitude: " + exif_GPS_LATITUDE + "\n" +
                    "GPS Latitute Ref: " + exif_GPS_LATITUDE_REF + "\n" +
                    "GPS Longitude: " + exif_GPS_LONGITUDE + "\n" +
                    "GPS Longitude Ref: " + exif_GPS_LONGITUDE_REF);
            //"Processing Method: " + exif_GPS_PROCESSING_METHOD + "\n" +
            //"GPS Time Stamp: " + exif_GPS_TIMESTAMP + "\n" +
            //"Image Length: " + exif_IMAGE_LENGTH + "\n" +
            //"Image Width: " + exif_IMAGE_WIDTH + "\n" +
            //"Make: " + exif_MAKE + "\n" +
            //"Model: " + exif_MODEL + "\n" +
            //"Orientation: " + exif_ORIENTATION + "\n" +
            //"White Balance: " + exif_WHITE_BALANCE + "\n");
        }
    }

    public void updateGeoTag(){
        //with dummy data
        final String DUMMY_GPS_LATITUDE = "22/1,21/1,299295/32768";
        final String DUMMY_GPS_LATITUDE_REF = "N";
        final String DUMMY_GPS_LONGITUDE = "114/1,3/1,207045/4096";
        final String DUMMY_GPS_LONGITUDE_REF = "E";

        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, DUMMY_GPS_LATITUDE);
        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, DUMMY_GPS_LATITUDE_REF);
        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, DUMMY_GPS_LONGITUDE);
        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, DUMMY_GPS_LONGITUDE_REF);
        try {
            exifInterface.saveAttributes();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateGeoTagFromLocation(double lat, double lon){

        //lat
        double alat = Math.abs(lat);
        String dms = Location.convert(alat, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        if(secnds.length==0)
        {
            seconds = splits[2];
        }
        else
        {
            seconds = secnds[0];
        }

        String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat>0?"N":"S");

        //lon
        double alon = Math.abs(lon);
        dms = Location.convert(alon, Location.FORMAT_SECONDS);
        splits = dms.split(":");
        secnds = (splits[2]).split("\\.");

        if(secnds.length==0)
        {
            seconds = splits[2];
        }
        else
        {
            seconds = secnds[0];
        }
        String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";


        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon>0?"E":"W");

        try {
            exifInterface.saveAttributes();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
