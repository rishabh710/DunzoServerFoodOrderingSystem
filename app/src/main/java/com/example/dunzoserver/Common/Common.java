package com.example.dunzoserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.example.dunzoserver.Model.Request;
import com.example.dunzoserver.Model.User;
import com.example.dunzoserver.Remote.APIService;
import com.example.dunzoserver.Remote.FCMRetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {

    public static User currentUser;
    public static Request currentRequest;

    public static String topicName="News";

    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public static final String UPDATE="Update";
    public static final String DELETE="Delete";

    public static String PHONE_TEXT="userPhone";

    public static final int PICK_IMAGE_REQUEST=71;


    public static final String fcmUrl="https://fcm.googleapis.com/";

    public static String convertCodetoStatus(String code){
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On the way";
        else if (code.equals("2"))
            return "Shipped";
        else
            return "Delivered";

    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo[] info= connectivityManager.getAllNetworkInfo();
            if (info!=null){
                for (int i=0; i<info.length;i++){
                    if (info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String getDate(long time)
    {
        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date= new StringBuilder(
                DateFormat.format("dd-MM-yyyy HH:MM",calendar).toString());
        return date.toString();
    }

}
