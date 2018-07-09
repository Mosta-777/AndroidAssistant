package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Contact;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mostafa on 6/29/2018.
 */

public class SendingSMS {
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface;
    private static String contactName = null;
    private static String phoneNumber = null;
    private static String messageBody = null;
    private static boolean welcomeBack = false;
    private static String winningSentence = null;


    public SendingSMS(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerInterface=intentAnalyzerAndRecognizer;
        int i = isThereSmsShow(theWinningSentences);
        switch (i){
            case 0:analyzerInterface.onSmsShow("Tamam eshta il rsayl ahy");break;
            case 1:determineBestSentenceForSmsSend(theWinningSentences);break;
        }
    }

    private void determineBestSentenceForSmsSend(ArrayList<ArrayList<Entity>> theWinningSentences) {

        if(!welcomeBack)
        {
            if(findContactAndText(theWinningSentences))
            {
                analyzerInterface.onChoosingTheWinningSentence(winningSentence);
                if (contactName!=null && phoneNumber==null)
                    analyzerInterface.onSmsSendSucceeded(contactName,messageBody);
                else if(contactName==null && phoneNumber!=null)
                {
                    analyzerInterface.onSmsSendSucceeded(phoneNumber,messageBody);
                }
            }
            else if(findContact(theWinningSentences))
            {
                analyzerInterface.onChoosingTheWinningSentence(winningSentence);
                welcomeBack = true;
                analyzerInterface.onSmsSendRequestingData(true, false);
            }
            else {
                analyzerInterface.onChoosingTheWinningSentence(
                        IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(0)));
                welcomeBack = true;
                analyzerInterface.onSmsSendRequestingData(false,false);
            }
        }

        else
        {
            if(contactName == null && phoneNumber== null)
            {
                if(findPhoneNumber(theWinningSentences)) {
                    analyzerInterface.onChoosingTheWinningSentence(winningSentence);
                    analyzerInterface.onSmsSendRequestingData(true, false);
                }
                else {
                    analyzerInterface.onChoosingTheWinningSentence(
                            IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(0)));
                    contactName = (String) theWinningSentences.get(0).get(0).getValue();
                    analyzerInterface.onSmsSendRequestingData(true,false);
                }
            }
            else if((contactName==null || phoneNumber==null) && messageBody==null)
            {
                messageBody = (String) theWinningSentences.get(0).get(0).getValue();
                analyzerInterface.onChoosingTheWinningSentence(messageBody);
                if(contactName!=null && phoneNumber==null)
                    analyzerInterface.onSmsSendSucceeded(contactName, messageBody);
                else if(contactName==null && phoneNumber!=null)
                    analyzerInterface.onSmsSendSucceeded(phoneNumber,messageBody);
            }
        }
        winningSentence = null;
    }

    private int isThereSmsShow(ArrayList<ArrayList<Entity>> theWinningSentences) {
        for (int i = 0; i < theWinningSentences.size(); i++) {
            if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.SMS_SHOW_INTENT_TYPE_ENTITY
                            , theWinningSentences.get(i))) return 0;
        }
        return 1;
    }

    private boolean findContactAndText(ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        for(int i=0; i<theWinningSentences.size(); i++) {
            if (((Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY, theWinningSentences.get(i)))
                    ^ (Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences.get(i))))
                    && Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.SMS_FREE_TEXT_ENTITY, theWinningSentences.get(i))) {

                int contactNameEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY, theWinningSentences.get(i));
                int phoneNumberEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences.get(i));

                if(contactNameEntity != -1) {
                    contactName = (String) theWinningSentences.get(i).get(contactNameEntity).getValue();
                    if(contactName.equals("شخص") || contactName.equals("حد") || contactName.equals("رقم")
                            ||contactName.equals("لحد") || contactName.equals("لشخص") || contactName.equals("لرقم")){
                        contactName = null;
                        return false;
                    }
                }
                else if(phoneNumberEntity != -1)
                    phoneNumber = (String) theWinningSentences.get(i).get(phoneNumberEntity).getValue();
                int messageBodyEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.SMS_FREE_TEXT_ENTITY, theWinningSentences.get(i));
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i));
                messageBody = (String) theWinningSentences.get(i).get(messageBodyEntity).getValue();
                return true;
            }
        }
        return false;
    }

    private boolean findContact(ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        for(int i=0; i<theWinningSentences.size(); i++) {
            if ((Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY, theWinningSentences.get(i)))
                    ^ (Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences.get(i)))) {

                int contactNameEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY, theWinningSentences.get(i));
                int phoneNumberEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences.get(i));

                if(contactNameEntity != -1) {
                    contactName = (String) theWinningSentences.get(i).get(contactNameEntity).getValue();
                    if(contactName.equals("شخص") || contactName.equals("حد") || contactName.equals("رقم")
                            ||contactName.equals("لحد") || contactName.equals("لشخص") || contactName.equals("لرقم")) {
                        contactName=null;
                        return false;
                    }
                }
                else if(phoneNumberEntity != -1)
                    phoneNumber = (String) theWinningSentences.get(i).get(phoneNumberEntity).getValue();
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i));
                return true;
            }
        }
        return false;
    }

    private boolean findMessageBody(ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        for(int i=0; i<theWinningSentences.size(); i++) {
            if (Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.SMS_FREE_TEXT_ENTITY, theWinningSentences.get(i))) {
                int messageBodyEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.SMS_FREE_TEXT_ENTITY, theWinningSentences.get(i));
                messageBody = (String) theWinningSentences.get(i).get(messageBodyEntity).getValue();
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i));
                return true;
            }
        }
        return false;
    }
    private boolean findPhoneNumber(ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        for(int i=0; i<theWinningSentences.size(); i++) {
            if (Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences.get(i))) {
                int messageBodyEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences.get(i));
                messageBody = (String) theWinningSentences.get(i).get(messageBodyEntity).getValue();
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i));
                return true;
            }
        }
        return false;
    }
    public static void showSms(Context context){
        clearData();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    public static void sendMessage(Context context, String contactNumber, String smsBody){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+phoneNumber));
        sendIntent.putExtra("sms_body", smsBody);
        context.startActivity(sendIntent);
        clearData();
    }

    public static void clearData() {
        contactName = null;messageBody = null; welcomeBack = false;phoneNumber=null;
    }

    public static boolean findNumber(Context context)
    {
        if(contactName!=null && phoneNumber==null)
        {
            ArrayList<Contact> contacts = Calling.getTheContacts(context);
            phoneNumber = Calling.searchForContactFourMethods(contacts, contactName);
        }
        else
            return true;
        if (phoneNumber==null)
            return false;
        else
            return true;
    }
}
