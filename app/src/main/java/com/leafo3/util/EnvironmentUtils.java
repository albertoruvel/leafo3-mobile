package com.leafo3.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alberto Rubalcaba on 4/11/2015.
 */
public class EnvironmentUtils {

    private static final String PREFS_NAME = "com.spaceapps.main.shared.name";
    private static final String ID_NAME = "com.spaceapps.main.shared.userid";
    public static final boolean TEST = false;

    private static final String AMAZON_HOST = "http://dev.dareu.me/leafo3";
    public static final String HOST = "http://albertoruvel.com/leafo3";
    private static final String IMAGE_PATH = "/rest/leafs/leafImage?imageType=%d&leafId=%s";

    public static String getImageUrl(ImageType imageType, String id){
        switch(imageType){
            case ORIGINAL:
                return String.format(HOST + IMAGE_PATH, 1, id);
            case PROCESSED:
                return String.format(HOST + IMAGE_PATH, 0, id);
            default:
                return "";
        }
    }



    public static String getCurrentHost(){
        if(TEST){
            return HOST;
        }else{
            return HOST;
        }
    }

    public static String getUserCountry(Context context){
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimCountryIso();
    }

    public static int DEFAULT_PAGE_SIZE = 10;

    /**
     * Change this URL to point to any production server,
     * this is only for testing, you can create your own static variable with
     * your server url and set it at doInBackground function of any BaseAsyncTask you create
     *
     */
    private static final String TEST_HOST = "http://192.168.0.10";
    private static final int TEST_PORT = 8080;

    private static final String EMAIL_PREFS = "com.spaceapps.main.shared.email";
    public static final String TEST_URL = TEST_HOST + ":" + TEST_PORT;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean validateEmail(String email){
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public static void saveUserEmail(Context context, String email){
        //create a new UUID
        UUID id = UUID.randomUUID();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(EMAIL_PREFS, email).commit();
    }

    public static String getUserEmail(Context context){
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(EMAIL_PREFS, "");
    }

    public static void saveUserId(Context context, String id){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(ID_NAME, id).commit();
    }

    public static String getUserId(Context context){
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(ID_NAME, "");
    }

    public static String getNewPicturePath(){
        String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/leafo3/";
        return directory += "leaf_" + new SimpleDateFormat("MM-dd-yyyy HH-mm").format(new Date()) + ".jpg";
    }

    public static enum ImageType{
        ORIGINAL, PROCESSED;
    }
}
