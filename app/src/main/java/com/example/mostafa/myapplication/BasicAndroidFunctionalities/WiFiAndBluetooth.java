package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.UIs.MainActivity;

import java.util.ArrayList;

/**
 * Created by Mahmoud Salah on 6/29/2018.
 */

public class WiFiAndBluetooth {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private ArrayList<ArrayList<Entity>> selectedSentences;


    public WiFiAndBluetooth(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                            ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        analyzerinterface = intentAnalyzerAndRecognizer;
        selectedSentences = theWinningSentences;
        int intentSelected = determineIntent();
        switch (intentSelected)
        {
            case 0: analyzerinterface.onWiFiOnSucceeded();break;
            case 1:analyzerinterface.onWiFiOffSucceeded();break;
            case 2:analyzerinterface.onBluetoothOnSucceeded();break;
            case 3:analyzerinterface.onBluetoothOffSucceeded();break;
        }

    }

    private int determineIntent()
    {
        for(int i=0;i<selectedSentences.size();i++)
        {
            if(IntentAnalyzerAndRecognizer.containsIntentValue(
                    IntentAnalyzerAndRecognizer.WIFI_ON_INTENT_TYPE_ENTITY,selectedSentences.get(0))) {
                analyzerinterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(selectedSentences.get(0)));
                return 0;
            }
            else if(IntentAnalyzerAndRecognizer.containsIntentValue(
                    IntentAnalyzerAndRecognizer.WIFI_OFF_INTENT_TYPE_ENTITY,selectedSentences.get(0))) {
                analyzerinterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(selectedSentences.get(0)));
                return 1;
            }

            else if(IntentAnalyzerAndRecognizer.containsIntentValue(
                    IntentAnalyzerAndRecognizer.BLUETOOTH_ON_INTENT_TYPE_ENTITY,selectedSentences.get(0))) {
                analyzerinterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(selectedSentences.get(0)));
                return 2;
            }
            else if(IntentAnalyzerAndRecognizer.containsIntentValue(
                    IntentAnalyzerAndRecognizer.BLUETOOTH_OFF_INTENT_TYPE_ENTITY,selectedSentences.get(0))) {
                analyzerinterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(selectedSentences.get(0)));
                return 3;
            }
        }
        return 3;
    }
    public static boolean setWifi(Context context, boolean newWifiStatus)
    {
        WifiManager mainWifiObj;
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions( (Activity)context, new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    MainActivity.WIFI_REQUEST);
        }
        mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        int oldWifiState = mainWifiObj.getWifiState();
        if(((oldWifiState == WifiManager.WIFI_STATE_ENABLED)&&!newWifiStatus)
                ||((oldWifiState == WifiManager.WIFI_STATE_DISABLED)&& newWifiStatus)) {
            mainWifiObj.setWifiEnabled(newWifiStatus);
            return true;
        }
        return false;
    }

    public static boolean setBluetooth(Context context, boolean newBluetoothStatus)
    {
        boolean hasPermissionBluetooth = (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionBluetooth) {
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.BLUETOOTH},
                    MainActivity.BLUETOOTH_REQUEST);
        }

        boolean hasPermissionBluetoothAdmin = (ContextCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionBluetoothAdmin) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                    MainActivity.BLUETOOTH_ADMIN_REQUEST);
        }
        BluetoothAdapter mainBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( mainBluetoothAdapter.isEnabled() && !newBluetoothStatus )
        {
            mainBluetoothAdapter.disable();
            return true;
        }
        else if( !mainBluetoothAdapter.isEnabled() && newBluetoothStatus )
        {
            mainBluetoothAdapter.enable();
            return true;
        }
        else
            return false;
    }
}
