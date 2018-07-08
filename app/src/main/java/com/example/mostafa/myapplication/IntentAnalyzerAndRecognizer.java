package com.example.mostafa.myapplication;

import android.util.Log;

import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Alarm;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.BuiltInApps;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Calling;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.GoogleSearch;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.OpenNonNativeApps;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Profiles;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Reminder;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.SendingSMS;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Weather;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.WiFiAndBluetooth;
import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.POJOS.Vote;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Mostafa on 2/19/2018.
 */

public class IntentAnalyzerAndRecognizer implements
        CommunicationInterfaces.MainActivityFunctionalityClassesInterface,
        CommunicationInterfaces.AnalyzerNetworkUtilsInterface {

    public static final String ALARM_SET_INTENT_TYPE_ENTITY="alarm_set";
    public static final String ALARM_SHOW_INTENT_TYPE_ENTITY="alarm_show";
    public static final String ALARM_DELETE_INTENT_TYPE_ENTITY="alarm_delete";
    private static final String FLASH_ON_INTENT_TYPE_ENTITY = "flash_on";
    private static final String FLASH_OFF_INTENT_TYPE_ENTITY = "flash_off";
    private static final String CANCEL_INTENT_TYPE_ENTITY="cancel";
    public static final String REMINDER_INTENT_TYPE_ENTITY="reminder";
    public static final String REMINDER_FREE_TEXT = "reminder_free_text";
    public static final String CALL_LOG_SHOW_INTENT_TYPE_ENTITY="call_log_show";
    public static final String CONTACTS_SHOW_INTENT_TYPE_ENTITY="contacts_show";
    public static final String CONTACTS_CALL_INTENT_TYPE_ENTITY ="contacts_call";
    public static final String NORMAL_MODE_INTENT_TYPE_ENTITY="mode_normal";
    public static final String SILENT_MODE_INTENT_TYPE_ENTITY="mode_silent";
    public static final String VIBRATION_MODE_INTENT_TYPE_ENTITY ="mode_vibration";
    public static final String PHONE_NUMBER_ENTITY = "phone_number";
    public static final String CONTACT_NAME_ENTITY = "contact_name";
    public static final String WIFI_ON_INTENT_TYPE_ENTITY = "wifi_on";
    public static final String WIFI_OFF_INTENT_TYPE_ENTITY = "wifi_off";
    public static final String BLUETOOTH_ON_INTENT_TYPE_ENTITY = "bluetooth_on";
    public static final String BLUETOOTH_OFF_INTENT_TYPE_ENTITY = "bluetooth_off";
    public static final String CAMERA_INTENT_TYPE_ENTITY = "camera";
    public static final String MUSIC_INTENT_TYPE_ENTITY = "music";
    public static final String GALLERY_INTENT_TYPE_ENTITY="gallery";
    public static final String SMS_SHOW_INTENT_TYPE_ENTITY = "sms_show";
    public static final String SMS_SEND_INTENT_TYPE_ENTITY = "sms_send";
    public static final String GOOGLE_SEARCH_INTENT_TYPE_ENTITY = "google_search";
    public static final String SMS_FREE_TEXT_ENTITY = "sms_free_text";
    public  static final String DATETIME_ENTITY="datetime";
    public  static final String DURATION_ENTITY="duration";
    public static final String TEXT_ENTITY="text";
    public static final String SEARCH_FREE_TEXT_ENTITY = "search_free_text";
    public static final String OPEN_APPS_INTENT_TYPE_ENTITY="open_apps";
    public static final String APP_NAME_ENTITY = "app_name";
    public static final String WEATHER_INTENT_TYPE_ENTITY = "weather";
    public static final String FUNCTIONS_LIST_INTENT_TYPE_ENTITY = "functions_list";
    public static final String GREETING_INTENT_TYPE_ENTITY = "greeting";
    public static final String CONFIRM_INTENT_TYPE_ENTITY = "confirm";
    public static final double CONFIDENCE_THRESHOLD = 0.7 ;
    private int pointer=0;
    private ArrayList<String> allPossibleStringsUserHasSaid=new ArrayList<>();
    private ArrayList<ArrayList<Entity>> sentences=new ArrayList<>();
    private HashMap<String,Vote> votingMap=new HashMap<>();
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface
            mainActivityAndAnalyzerInterface;
    private CommunicationInterfaces.AnalyzerNetworkUtilsInterface
            analyzerNetworkUtilsInterface;
    private String theIntentRequestingData;

    public IntentAnalyzerAndRecognizer(CommunicationInterfaces.MainActivityFunctionalityClassesInterface communicationInterface,
                                       ArrayList<String> whatWasHeardFromVoiceRecognizer){
        mainActivityAndAnalyzerInterface=communicationInterface;
        analyzeAndRealize(whatWasHeardFromVoiceRecognizer,null);
    }
    IntentAnalyzerAndRecognizer(CommunicationInterfaces.AnalyzerNetworkUtilsInterface analyzerNetworkUtilsInterface){
        this.analyzerNetworkUtilsInterface=analyzerNetworkUtilsInterface;
    }
    public void analyzeAndRealize(ArrayList<String> whatWasHeardFromVoiceRecognizer,String knownIntent){
        theIntentRequestingData=knownIntent;
        allPossibleStringsUserHasSaid=whatWasHeardFromVoiceRecognizer;
        sentences.clear();
        votingMap.clear();
        analyzerNetworkUtilsInterface = new NetworkUtils(this);
        analyzerNetworkUtilsInterface.toNetworkUtils(whatWasHeardFromVoiceRecognizer.get(pointer++));
    }
    @Override
    public void toAnalyzer(ArrayList<Entity> receivedEntities) {
        sentences.add(receivedEntities);
        // We look for every major entity ( Intent ).
        // We check if the response contains an intent entity .
            int indexOfIntentEntity = containsEntity(JSONUtils.ENTITY_INTENT_KEY, receivedEntities);
            isAValidIntent(indexOfIntentEntity, receivedEntities);
            if (pointer <= allPossibleStringsUserHasSaid.size() - 1) {
                analyzerNetworkUtilsInterface.toNetworkUtils(allPossibleStringsUserHasSaid.get(pointer++));
            } else {
                pointer = 0;
                Log.e(TAG, "Voting ended");
                // Check the voting of each intent in each sentence , the one with the biggest votes , the
                // highest number of data entities shall be taken .
                // The getWinnerIntent function returns the sentence ( the ArrayList of entities )
                // with the intent got the highest votes and contains the highest number of data entities .
                HashMap.Entry<String, Vote> winningEntry = getTheWinnerEntry();

                if (winningEntry != null) { // The sentences were meaningful ( one of them at least )
                    Vote theWinnerVote = winningEntry.getValue();
                    String winningIntent = winningEntry.getKey();
                    ArrayList<ArrayList<Entity>> theWinningSentences = theWinnerVote.getTheSentencesVoted();
                    if (theIntentRequestingData == null) {
                        if (winningIntent.equals(CANCEL_INTENT_TYPE_ENTITY)) {
                            mainActivityAndAnalyzerInterface
                                    .onChoosingTheWinningSentence(extractTextFromSentence(theWinningSentences.get(0)));
                            mainActivityAndAnalyzerInterface.onCancellingWhat("a cancel eh ? , mfeesh 7aga");
                        }
                        goToTheAppropriateFunctionalityClass(winningIntent, theWinningSentences);
                    } else {
                        if (winningIntent.equals(CANCEL_INTENT_TYPE_ENTITY)) {
                            mainActivityAndAnalyzerInterface
                                    .onChoosingTheWinningSentence(extractTextFromSentence(theWinningSentences.get(0)));
                            mainActivityAndAnalyzerInterface.onCancelling(theIntentRequestingData);
                        }
                        else giveDataToIntentRequestingData();
                    }
                }else { // The sentences had no meaning ( not a single one had an intent or crossed the confidence threshold )
                    if (theIntentRequestingData == null) {
                        mainActivityAndAnalyzerInterface
                                .onChoosingTheWinningSentence(allPossibleStringsUserHasSaid.get(0));
                        //mainActivityAndAnalyzerInterface.onFailingToUnderstand(allPossibleStringsUserHasSaid.get(0));
                        ArrayList<Entity> temp = new ArrayList<>();
                        temp.add(new Entity(TEXT_ENTITY, 1.0, allPossibleStringsUserHasSaid.get(0)));
                        ArrayList<ArrayList<Entity>> temp2 = new ArrayList<>();
                        temp2.add(temp);
                        new GoogleSearch(this, temp2, false);
                    }
                    else giveDataToIntentRequestingData();
                }
            }
    }

    private void giveDataToIntentRequestingData() {
        switch (theIntentRequestingData) {
            case IntentAnalyzerAndRecognizer.ALARM_SET_INTENT_TYPE_ENTITY:
                new Alarm(this, sentences);
                break;
            case IntentAnalyzerAndRecognizer.REMINDER_INTENT_TYPE_ENTITY:
                new Reminder(this, sentences);
                break;
            case IntentAnalyzerAndRecognizer.CONTACTS_CALL_INTENT_TYPE_ENTITY:
                new Calling(this, sentences, Calling.MODE_REQUESTING_DATA);
                break;
            case IntentAnalyzerAndRecognizer.GOOGLE_SEARCH_INTENT_TYPE_ENTITY:
                new GoogleSearch(this,sentences,true);break;
            case IntentAnalyzerAndRecognizer.OPEN_APPS_INTENT_TYPE_ENTITY:
                new OpenNonNativeApps(this,sentences);break;
            case IntentAnalyzerAndRecognizer.SMS_SEND_INTENT_TYPE_ENTITY:
                new SendingSMS(this,sentences);break;
        }
    }

    @Override
    public void toAnalyzerFailedResponse(String failingMessage) {
        mainActivityAndAnalyzerInterface.onGettingWitResponseFailed(failingMessage);
    }
    private void goToTheAppropriateFunctionalityClass(String winningIntent,
                                                             ArrayList<ArrayList<Entity>> theWinningSentences) {
        // Go to the alarm class with the winning sentences
        switch (winningIntent) {
            case ALARM_SET_INTENT_TYPE_ENTITY:
            case ALARM_SHOW_INTENT_TYPE_ENTITY:
            case ALARM_DELETE_INTENT_TYPE_ENTITY:
                new Alarm(this, theWinningSentences);break;
            case FLASH_ON_INTENT_TYPE_ENTITY:
                ArrayList<Entity> theWinningSentence = theWinningSentences.get(0);
                mainActivityAndAnalyzerInterface
                        .onChoosingTheWinningSentence(extractTextFromSentence(theWinningSentence));
                mainActivityAndAnalyzerInterface.onFlashLightOn("Opening the flashlight ...");
                break;
            case FLASH_OFF_INTENT_TYPE_ENTITY:
                ArrayList<Entity> theWinningSentence1= theWinningSentences.get(0);
                mainActivityAndAnalyzerInterface
                        .onChoosingTheWinningSentence(extractTextFromSentence(theWinningSentence1));
                mainActivityAndAnalyzerInterface.onFlashLightOff("Closing the flashlight ...");break;
            case REMINDER_INTENT_TYPE_ENTITY:
                new Reminder(this,theWinningSentences);break;
            case CALL_LOG_SHOW_INTENT_TYPE_ENTITY:
            case CONTACTS_CALL_INTENT_TYPE_ENTITY:
            case CONTACTS_SHOW_INTENT_TYPE_ENTITY :
                new Calling(this,theWinningSentences,Calling.MODE_DEFAULT);break;
            case NORMAL_MODE_INTENT_TYPE_ENTITY:
            case SILENT_MODE_INTENT_TYPE_ENTITY:
            case VIBRATION_MODE_INTENT_TYPE_ENTITY:
                new Profiles(this,theWinningSentences);break;
            case WIFI_ON_INTENT_TYPE_ENTITY:
            case WIFI_OFF_INTENT_TYPE_ENTITY:
            case BLUETOOTH_ON_INTENT_TYPE_ENTITY:
            case BLUETOOTH_OFF_INTENT_TYPE_ENTITY:
                new WiFiAndBluetooth(this, theWinningSentences);break;
            case SMS_SEND_INTENT_TYPE_ENTITY:
            case SMS_SHOW_INTENT_TYPE_ENTITY:
                new SendingSMS(this,theWinningSentences);break;
            case GOOGLE_SEARCH_INTENT_TYPE_ENTITY:
                new GoogleSearch(this,theWinningSentences,false);break;
            case OPEN_APPS_INTENT_TYPE_ENTITY:
                new OpenNonNativeApps(this,theWinningSentences);break;
            case CAMERA_INTENT_TYPE_ENTITY:
            case MUSIC_INTENT_TYPE_ENTITY:
            case GALLERY_INTENT_TYPE_ENTITY:
                new BuiltInApps(this, theWinningSentences);break;
            case WEATHER_INTENT_TYPE_ENTITY:
                new Weather(this, theWinningSentences);break;
            case FUNCTIONS_LIST_INTENT_TYPE_ENTITY:
                ArrayList<Entity> theWinningSentence2 = theWinningSentences.get(0);
                mainActivityAndAnalyzerInterface
                        .onChoosingTheWinningSentence(extractTextFromSentence(theWinningSentence2));
                mainActivityAndAnalyzerInterface.onFunctionListSucceeded();break;
            case GREETING_INTENT_TYPE_ENTITY:
                ArrayList<Entity> theWinningSentence3 = theWinningSentences.get(0);
                mainActivityAndAnalyzerInterface
                        .onChoosingTheWinningSentence(extractTextFromSentence(theWinningSentence3));
                mainActivityAndAnalyzerInterface.onGreetingSucceeded();break;
        }

    }


    // For debugging purposes .
    private int getWinnerIndex(ArrayList<Entity> theWinnerSentence) {
        for (int i=0;i<sentences.size();i++){
            if (sentences.get(i)==theWinnerSentence) return i;
        }
        return -1;
    }

    private HashMap.Entry<String,Vote> getTheWinnerEntry() {
        HashMap.Entry<String, Vote> maxEntry = null;
        for (HashMap.Entry<String, Vote> entry : votingMap.entrySet()) {

            if (maxEntry == null
                    || entry.getValue().getNumberOfIntentOccurances()
                    >(maxEntry.getValue().getNumberOfIntentOccurances())) {
                maxEntry = entry;
            }
        }
        return maxEntry;
    }

    private boolean isAValidIntent(int indexOfIntentToCheck, ArrayList<Entity> currentSentence) {
        // We look for entities that must present in each major entity so that
        // it can be valid .
        // But first we check if there is an intent entity in the response before
        // anything .
        if (indexOfIntentToCheck==-1){
            Log.e(TAG,"isAValidIntent : No intent found in the statement");
            return false;
        }
        Entity entityToCheck=currentSentence.get(indexOfIntentToCheck);
        if (entityToCheck.getConfidence()<CONFIDENCE_THRESHOLD) {
            Log.e(TAG, "isAValidIntent: Confidence is too low");
            return false;
        }
        String nameOfIntentToCheck=entityToCheck.getValue().toString();
        if (votingMap.containsKey(nameOfIntentToCheck)){
            // We increase the current number of occurrences by one
            int newNumberOfOccurrances=votingMap.get(nameOfIntentToCheck).getNumberOfIntentOccurances()+1;
            // We then compare between the length-1 of the ArrayList of the index found in the
            // Vote object and the current one , we replace it with the current one if it is
            // simply bigger ( implying that the current sentence has more data )
            // We initialize the new sentence with the existing sentence in the vote .
            ArrayList<ArrayList<Entity>> existingSentences=votingMap.get(nameOfIntentToCheck)
                    .getTheSentencesVoted();
            existingSentences.add(currentSentence);
            votingMap.put(nameOfIntentToCheck,new Vote(newNumberOfOccurrances,existingSentences));
        }else {
            ArrayList<ArrayList<Entity>> existingSentences= new ArrayList<>();
            existingSentences.add(currentSentence);
            votingMap.put(nameOfIntentToCheck,new Vote(1,existingSentences));
        }
        /*return nameOfIntentToCheck.equals(ALARM_SET_INTENT_TYPE_ENTITY) ||
                nameOfIntentToCheck.equals(ALARM_DELETE_INTENT_TYPE_ENTITY) ||
                        nameOfIntentToCheck.equals(ALARM_SHOW_INTENT_TYPE_ENTITY);*/
        return true;
    }

    public static int containsEntity(String mainEntityToLookFor, ArrayList<Entity> receivedEntities) {
        for (int i=0;i<receivedEntities.size();i++){
            if (receivedEntities.get(i).getName().equals(mainEntityToLookFor)) return i;
        }
        return -1;
    }
    public static int containsEntitySentenceVersion(String entityToLookFor,ArrayList<ArrayList<Entity>> sentences){
        for (int i = 0; i < sentences.size(); i++) {
            if (IntentAnalyzerAndRecognizer
                    .containsEntity(entityToLookFor, sentences.get(i))!=-1) return i;
        }
        return -1;
    }
    public static boolean containsIntentValue(String intentValue,ArrayList<Entity> receivedEntities){
        for (int i=0;i<receivedEntities.size();i++){
            if (receivedEntities.get(i).getName().equals(JSONUtils.ENTITY_INTENT_KEY) &&
                    receivedEntities.get(i).getValue().equals(intentValue)) return true;
        }
        return false;
    }

    public static String extractTextFromSentence(ArrayList<Entity> sentence){
        return sentence.get(IntentAnalyzerAndRecognizer
                        .containsEntity(IntentAnalyzerAndRecognizer.TEXT_ENTITY,sentence))
                        .getValue().toString();
    }


    // Tube functions
    @Override public void onAlarmSetSucceeded(String dateTime) {mainActivityAndAnalyzerInterface.onAlarmSetSucceeded(dateTime);}
    @Override public void onAlarmSetRequestingData(String message){mainActivityAndAnalyzerInterface.onAlarmSetRequestingData(message);}
    @Override public void onAlarmShowSucceeded(String message) {mainActivityAndAnalyzerInterface.onAlarmShowSucceeded(message);}
    @Override public void onAlarmDeleteSucceeded(String message) {mainActivityAndAnalyzerInterface.onAlarmDeleteSucceeded(message);}
    @Override public void onGettingWitResponseFailed(String failingMessage){mainActivityAndAnalyzerInterface.onGettingWitResponseFailed(failingMessage);}
    @Override public void onShowCallLog(String message) {mainActivityAndAnalyzerInterface.onShowCallLog(message);}
    @Override public void onShowContacts(String message) {mainActivityAndAnalyzerInterface.onShowContacts(message);}
    @Override public void onReminderSucceeded(String dateTime, String reminderFreeText) {mainActivityAndAnalyzerInterface.onReminderSucceeded(dateTime, reminderFreeText);}
    @Override public void onReminderRequestingData(boolean dateTime, boolean reminderFreeTextExists) {mainActivityAndAnalyzerInterface.onReminderRequestingData(dateTime, reminderFreeTextExists);}
    @Override public void onCallingNumberSucceeded(String phoneNumber) {mainActivityAndAnalyzerInterface.onCallingNumberSucceeded(phoneNumber);}
    @Override public void onCallingByName(String name) {mainActivityAndAnalyzerInterface.onCallingByName(name);}
    @Override public void onCallingContactNotFound(String message) {mainActivityAndAnalyzerInterface.onCallingContactNotFound(message);}
    @Override public void onNormalModeOn(String message) {mainActivityAndAnalyzerInterface.onNormalModeOn(message);}
    @Override public void onSilentModeOn(String message) {mainActivityAndAnalyzerInterface.onSilentModeOn(message);}
    @Override public void onVibrationModeOn(String message) {mainActivityAndAnalyzerInterface.onVibrationModeOn(message);}
    @Override public void onSmsShow(String message) {mainActivityAndAnalyzerInterface.onSmsShow(message);}


    @Override
    public void onWeatherSucceeded(String url) {
        mainActivityAndAnalyzerInterface.onWeatherSucceeded(url);
    }

    @Override public void onSmsSendSucceeded(String contactName, String smsBody) {mainActivityAndAnalyzerInterface.onSmsSendSucceeded(contactName,smsBody);}
    @Override public void onSmsSendRequestingData(boolean contactNameExists, boolean smsBodyExists) {mainActivityAndAnalyzerInterface.onSmsSendRequestingData(contactNameExists,smsBodyExists);}
    @Override public void onSmsSendFailed(String message) {mainActivityAndAnalyzerInterface.onSmsSendFailed(message);}
    @Override public void onSearchSuccess(String message) {mainActivityAndAnalyzerInterface.onSearchSuccess(message);}
    @Override public void onSearchRequestingData(String message) {mainActivityAndAnalyzerInterface.onSearchRequestingData(message);}
    @Override public void onOpeningNonNativeAppSuccess(String appName) {mainActivityAndAnalyzerInterface.onOpeningNonNativeAppSuccess(appName);}
    @Override public void onOpeningNonNativeAppRequestingData(String message) {mainActivityAndAnalyzerInterface.onOpeningNonNativeAppRequestingData(message);}
    @Override public void onChoosingTheWinningSentence(String winningSentence) {mainActivityAndAnalyzerInterface.onChoosingTheWinningSentence(winningSentence);}
    @Override public void onCallingNumberRequestingData(String message) {mainActivityAndAnalyzerInterface.onCallingNumberRequestingData(message);}
    @Override public void onWiFiOnSucceeded() {mainActivityAndAnalyzerInterface.onWiFiOnSucceeded();}
    @Override public void onWiFiOffSucceeded() {mainActivityAndAnalyzerInterface.onWiFiOffSucceeded();}
    @Override public void onBluetoothOnSucceeded() {mainActivityAndAnalyzerInterface.onBluetoothOnSucceeded();}
    @Override public void onBluetoothOffSucceeded() {mainActivityAndAnalyzerInterface.onBluetoothOffSucceeded();}
    @Override public void onCameraSucceeded(){mainActivityAndAnalyzerInterface.onCameraSucceeded();}
    @Override public void onMusicSucceeded(){mainActivityAndAnalyzerInterface.onMusicSucceeded();}
    @Override public void onGallerySucceeded() {mainActivityAndAnalyzerInterface.onGallerySucceeded();}
    @Override public void onFailingToUnderstand() {mainActivityAndAnalyzerInterface.onFailingToUnderstand();}

    // Rubbish functions , bnnadehom 3ala tool mn hna msh bn7tag nroo7 class
    @Override public void onFlashLightOn(String message) {}
    @Override public void onFlashLightOff(String message) {}
    @Override public void toNetworkUtils(String message) {}
    @Override public void onCancelling(String intentToCancel) {}
    @Override public void onCancellingWhat(String message) {}
    @Override public void onFunctionListSucceeded() {}
    @Override public void onGreetingSucceeded() {}
}
