package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Contact;
import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.UIs.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mostafa on 6/27/2018.
 */

public class Calling {


    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    public static final int MODE_DEFAULT=100;
    public static final int MODE_REQUESTING_DATA=200;

    private static CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface;
    private int currentMode;



    public Calling(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                   ArrayList<ArrayList<Entity>> theWinningSentences,int currentMode) {
        analyzerInterface = intentAnalyzerAndRecognizer;
        this.currentMode=currentMode;
        int i = isThereShowContactsShowCallLogOrPhoneNumber(theWinningSentences);
        switch (i) {
            case 0:analyzerInterface.onShowCallLog("Tamam eshta il call log aho");break;
            case 1:analyzerInterface.onShowContacts("Tamam il contacts ahy");break;
            case 2:callUsingTheNumber(theWinningSentences);break;
            case 3:
                // lw inta wslt hina ffy e7tmal mn il itneen ya fe free text ya fe information na2sa
                determineTheBestSentenceForCallingByName(theWinningSentences);break;
        }
    }

    private void callUsingTheNumber(ArrayList<ArrayList<Entity>> theWinningSentences) {
        int indexOfSentenceContainingTheNumber = IntentAnalyzerAndRecognizer
                .containsEntitySentenceVersion(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theWinningSentences);
        ArrayList<Entity> theSentenceContainingTheNumber = theWinningSentences.get(indexOfSentenceContainingTheNumber);
        String theNumberToCall = theSentenceContainingTheNumber.get(IntentAnalyzerAndRecognizer
                .containsEntity(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY, theSentenceContainingTheNumber))
                .getValue().toString();
        analyzerInterface.onCallingNumberSucceeded(theNumberToCall);
    }


    private void determineTheBestSentenceForCallingByName(ArrayList<ArrayList<Entity>> theWinningSentences) {
        if (currentMode == MODE_DEFAULT) {
            int indexOfTheSentenceContainingTheContactName = IntentAnalyzerAndRecognizer
                    .containsEntitySentenceVersion(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY, theWinningSentences);
            if (indexOfTheSentenceContainingTheContactName != -1) {
                ArrayList<Entity> theSentenceContainingTheContactName = theWinningSentences
                        .get(indexOfTheSentenceContainingTheContactName);
                String theContactName = theSentenceContainingTheContactName.get(IntentAnalyzerAndRecognizer
                        .containsEntity(IntentAnalyzerAndRecognizer.CONTACT_NAME_ENTITY, theSentenceContainingTheContactName))
                        .getValue().toString();
                if(theContactName.equals("شخص") || theContactName.equals("حد") || theContactName.equals("رقم")
                        ||theContactName.equals("بحد") || theContactName.equals("بشخص") || theContactName.equals("برقم")){
                    analyzerInterface.onCallingNumberRequestingData("Tamam aklm meen");
                    return;
                }
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theSentenceContainingTheContactName));
                analyzerInterface.onCallingByName(theContactName);
            } else analyzerInterface.onCallingNumberRequestingData("Tamam aklm meen");
        }else if (currentMode == MODE_REQUESTING_DATA){
            ArrayList<Entity> theSentenceContainingTheContactName=theWinningSentences.get(0);
            String theContactName = theSentenceContainingTheContactName.get(IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.TEXT_ENTITY,theSentenceContainingTheContactName))
                    .getValue().toString();
            analyzerInterface.onChoosingTheWinningSentence(theContactName);
            analyzerInterface.onCallingByName(theContactName);
        }

    }

    private int isThereShowContactsShowCallLogOrPhoneNumber(ArrayList<ArrayList<Entity>> theWinningSentences) {
        for (int i = 0; i < theWinningSentences.size(); i++) {
            if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.CALL_LOG_SHOW_INTENT_TYPE_ENTITY
                            , theWinningSentences.get(i))) {
                analyzerInterface.onChoosingTheWinningSentence
                                (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i)));
                return 0;
            }
            else if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.CONTACTS_SHOW_INTENT_TYPE_ENTITY
                            , theWinningSentences.get(i))){
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i)));
                return 1;
            }
            else if (IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.PHONE_NUMBER_ENTITY
                            , theWinningSentences.get(i))!=-1) {
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i)));
                return 2;
            }
        }
        return 3;
    }

    public static void showContacts(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        context.startActivity(intent);
    }

    public static void showCallLog(Context context) {
        Intent showCallLog = new Intent();
        showCallLog.setAction(Intent.ACTION_VIEW);
        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
        context.startActivity(showCallLog);
    }

    public static void call(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" +phoneNumber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},
                    MainActivity.CALL_PHONE_REQUEST);
            return;
        }
        context.startActivity(intent);
    }

    public static void callByName(Context context, String contactName) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS},
                    MainActivity.READ_CONTACTS_REQUEST);
            return;
        }
        ArrayList<Contact> contacts = getTheContacts(context);
        String contactNumber = searchForContactFourMethods(contacts,contactName);
        //String contactNumber = searchForContactFourMethods(getTheContacts(context),contactName);
        if (contactNumber!=null) analyzerInterface.onCallingNumberSucceeded(contactNumber);
        else analyzerInterface.onCallingContactNotFound("Msh la2y il contact dah 3ndk");

    }

    public static String searchForContactFourMethods(ArrayList<Contact> contacts, String contactName) {
        String contactNumber;
        for (int i = 0; i < 4; i++) {
            contactNumber = searchForContact(contacts, contactName, i);
            if (contactNumber != null) return contactNumber;
        }
        return null;
    }

    private static String searchForContact(ArrayList<Contact> contacts, String contactName,int searchMode) {
        String contactName1=contactName+" ";
        String contactName2=contactName.substring(1)+" ";
        for (int i=0;i<contacts.size();i++){
            if (searchMode==0) {
                if (contactName.equals(contacts.get(i).getContactName())) {
                    return contacts.get(i).getContactNumber();
                }
            }else if (searchMode==1){
                if (contactName.substring(1).equals(contacts.get(i).getContactName())) {
                    return contacts.get(i).getContactNumber();
                }
            }else if (searchMode==2){
                String co=contacts.get(i).getContactName();
                if (contacts.get(i).getContactName().contains(contactName1)) {
                    return contacts.get(i).getContactNumber();
                }
            }else if (searchMode==3){
                if (contacts.get(i).getContactName().contains(contactName2)) {
                    return contacts.get(i).getContactNumber();
                }
            }
        }
        return null;
    }
    public static ArrayList<Contact> getTheContacts(Context context) {
        ContentResolver cr = context.getContentResolver();

        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{PHONE_NUMBER, PHONE_CONTACT_ID},
                null,
                null,
                null
        );
        if(pCur != null){
            if(pCur.getCount() > 0) {
                HashMap<Integer, ArrayList<String>> phones = new HashMap<>();
                while (pCur.moveToNext()) {
                    Integer contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID));
                    ArrayList<String> curPhones = new ArrayList<>();
                    if (phones.containsKey(contactId)) {
                        curPhones = phones.get(contactId);
                    }
                    curPhones.add(pCur.getString(pCur.getColumnIndex(PHONE_NUMBER)));
                    phones.put(contactId, curPhones);
                }
                Cursor cur = cr.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        new String[]{CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER},
                        HAS_PHONE_NUMBER + " > 0",
                        null,
                        DISPLAY_NAME + " ASC");
                if (cur != null) {
                    if (cur.getCount() > 0) {
                        ArrayList<Contact> contacts = new ArrayList<>();
                        while (cur.moveToNext()) {
                            int id = cur.getInt(cur.getColumnIndex(CONTACT_ID));
                            if(phones.containsKey(id)) {
                                Contact con = new Contact(cur
                                        .getString(cur.getColumnIndex(DISPLAY_NAME)),phones.get(id).get(0));
                                contacts.add(con);
                            }
                        }
                        return contacts;
                    }
                    cur.close();
                }
            }
            pCur.close();
        }
        return null;
    }

}