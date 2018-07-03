package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/29/2018.
 */

public class SendingSMS {
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface;
    private static String contactName = null;
    private static String messageBody = null;
    private static boolean welcomeBack = false;


    public SendingSMS(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerInterface=intentAnalyzerAndRecognizer;
        int i = isThereSmsShowSendAMessageByNameOrNumber(theWinningSentences);
        switch (i){
            case 0:analyzerInterface.onSmsShow("Tamam eshta il rsayl ahy");break;
            case 1:break;
            case 2:break;
        }
    }

    private void determineBestSentenceForSmsSend(ArrayList<ArrayList<Entity>> theWinningSentences) {

    }

    private int isThereSmsShowSendAMessageByNameOrNumber(ArrayList<ArrayList<Entity>> theWinningSentences) {
        for (int i = 0; i < theWinningSentences.size(); i++) {
            if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.SMS_SHOW_INTENT_TYPE_ENTITY
                            , theWinningSentences.get(i))) return 0;
            else if (IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY
                            , theWinningSentences.get(i))!=-1) return 1;
            else if (IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY
                            , theWinningSentences.get(i))!=-1) return 2;
        }
        return 0;
    }

    public static void showSms(Context context){
        clearData();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    public static void sendMessage(Context context, String contactNumber, String smsBody){
        clearData();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+contactNumber));
        context.startActivity(sendIntent);
    }

    private static void clearData() {
        contactName = null;messageBody = null; welcomeBack = false;
    }

}
