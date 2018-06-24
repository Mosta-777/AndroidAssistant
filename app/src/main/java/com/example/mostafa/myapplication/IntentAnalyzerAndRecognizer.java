package com.example.mostafa.myapplication;

import android.util.Log;

import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Alarm;
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
    public  static final String DATETIME_ENTITY="datetime";
    public  static final String DURATION_ENTITY="duration";
    private static final double CONFIDENCE_THRESHOLD = 0.8 ;
    private static final String FLASH_ON_INTENT_TYPE_ENTITY = "flash_on";
    private static final String FLASH_OFF_INTENT_TYPE_ENTITY = "flash_off";
    private int pointer=0;
    private ArrayList<String> allPossibleStringsUserHasSaid=new ArrayList<>();
    private ArrayList<ArrayList<Entity>> sentences=new ArrayList<>();
    private HashMap<String,Vote> votingMap=new HashMap<>();
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface
            mainActivityAndAnalyzerInterface;
    private CommunicationInterfaces.AnalyzerNetworkUtilsInterface
            analyzerNetworkUtilsInterface;
    private Alarm alarm;
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
        //NetworkUtils.getResponse(whatWasHeardFromVoiceRecognizer.get(pointer++));
    }
    @Override
    public void toAnalyzer(ArrayList<Entity> receivedEntities) {
        sentences.add(receivedEntities);
        // We look for every major entity ( Intent ).
        // We check if the response contains an intent entity .
            int indexOfIntentEntity = containsEntity(JSONUtils.ENTITY_INTENT_KEY, receivedEntities);
            if (theIntentRequestingData==null) isAValidIntent(indexOfIntentEntity, receivedEntities);
            if (pointer <= allPossibleStringsUserHasSaid.size() - 1) {
                analyzerNetworkUtilsInterface.toNetworkUtils(allPossibleStringsUserHasSaid.get(pointer++));
            } else {
                pointer = 0;
                Log.e(TAG, "Voting ended");
                // Check the voting of each intent in each sentence , the one with the biggest votes , the
                // highest number of data entities shall be taken .
                // The getWinnerIntent function returns the sentence ( the ArrayList of entities )
                // with the intent got the highest votes and contains the highest number of data entities .
                if (theIntentRequestingData==null) {
                    HashMap.Entry<String, Vote> winningEntry = getTheWinnerEntry();
                    Vote theWinnerVote = winningEntry.getValue();
                    String winningIntent = winningEntry.getKey();
                    ArrayList<ArrayList<Entity>> theWinningSentences = theWinnerVote.getTheSentencesVoted();
                    goToTheAppropriateFunctionalityClass(winningIntent, theWinningSentences);
                }else if (theIntentRequestingData.equals(IntentAnalyzerAndRecognizer.ALARM_SET_INTENT_TYPE_ENTITY))
                    new Alarm(this,sentences);

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
                alarm = new Alarm(this, theWinningSentences);
                break;
            case FLASH_ON_INTENT_TYPE_ENTITY:
                mainActivityAndAnalyzerInterface.onFlashLightOn("Opening the flashlight ...");
                break;
            case FLASH_OFF_INTENT_TYPE_ENTITY:
                mainActivityAndAnalyzerInterface.onFlashLightOff("Closing the flashlight ...");
                break;
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
        return nameOfIntentToCheck.equals(ALARM_SET_INTENT_TYPE_ENTITY) ||
                (nameOfIntentToCheck.equals(ALARM_DELETE_INTENT_TYPE_ENTITY) ||
                        (nameOfIntentToCheck.equals(ALARM_SHOW_INTENT_TYPE_ENTITY)));
    }

    public static int containsEntity(String mainEntityToLookFor, ArrayList<Entity> receivedEntities) {
        for (int i=0;i<receivedEntities.size();i++){
            if (receivedEntities.get(i).getName().equals(mainEntityToLookFor)) return i;
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
    @Override
    public void onAlarmSetSucceeded(String dateTime) {mainActivityAndAnalyzerInterface.onAlarmSetSucceeded(dateTime);}
    @Override public void onAlarmSetRequestingData(String message) {
        mainActivityAndAnalyzerInterface.onAlarmSetRequestingData(message);}
    @Override public void onAlarmShowSucceeded(String message) {mainActivityAndAnalyzerInterface.onAlarmShowSucceeded(message);}
    @Override public void onAlarmDeleteSucceeded(String message) {mainActivityAndAnalyzerInterface.onAlarmDeleteSucceeded(message);}
    @Override public void onGettingWitResponseFailed(String failingMessage) {
        mainActivityAndAnalyzerInterface.onGettingWitResponseFailed(failingMessage);
    }

    @Override
    public void onFlashLightOn(String message) {

    }

    @Override
    public void onFlashLightOff(String message) {

    }

    /*static void handleFetchedEntities(ArrayList<Entity> receivedEntities) {
        sentences.add(receivedEntities);
        // We look for every major entity ( Intent ).
        // We check if the response contains an intent entity .
        int indexOfIntentEntity = containsEntity(JSONUtils.ENTITY_INTENT_KEY, receivedEntities);
        isAValidIntent(indexOfIntentEntity, receivedEntities);
        if (pointer <= allPossibleStringsUserHasSaid.size()-1) {
            analyzerNetworkUtilsInterface.toNetworkUtils(whatWasHeardFromVoiceRecognizer.get(pointer++));
            //NetworkUtils.getResponse(allPossibleStringsUserHasSaid.get(pointer++));
        }else {
            pointer=0;
            Log.e(TAG, "Voting ended");
            // Check the voting of each intent in each sentence , the one with the biggest votes , the
            // highest number of data entities shall be taken .
            // The getWinnerIntent function returns the sentence ( the ArrayList of entities )
            // with the intent got the highest votes and contains the highest number of data entities .
            HashMap.Entry<String,Vote> winningEntry=getTheWinnerEntry();
            Vote theWinnerVote=winningEntry.getValue();String winningIntent=winningEntry.getKey();
            ArrayList<ArrayList<Entity>> theWinningSentences=theWinnerVote.getTheSentencesVoted();
            goToTheAppropriateFunctionalityClass(winningIntent,theWinningSentences);
        }
    }*/
    @Override public void toNetworkUtils(String message) {}
}
