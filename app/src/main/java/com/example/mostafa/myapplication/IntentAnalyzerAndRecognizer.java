package com.example.mostafa.myapplication;

import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Mostafa on 2/19/2018.
 */

public class IntentAnalyzerAndRecognizer {


    private static final String ALARM_SET_INTENT_TYPE_ENTITY="alarm_set";
    private static final String ALARM_SHOW_INTENT_TYPE_ENTITY="alarm_show";
    private static final String ALARM_DELETE_INTENT_TYPE_ENTITY="alarm_delete";
    private static final String DATETIME_ENTITY="datetime";
    private static final double CONFIDENCE_THRESHOLD = 0.65 ;
    private static int pointer=0;
    private static ArrayList<String> allPossibleStringsUserHasSaid=new ArrayList<>();

    public static void analyzeAndRealize(ArrayList<String> whatWasHeardFromVoiceRecognizer){
        allPossibleStringsUserHasSaid=whatWasHeardFromVoiceRecognizer;
        NetworkUtils.getResponse(whatWasHeardFromVoiceRecognizer.get(pointer++));
    }
    public static void handleFetchedEntities(ArrayList<Entity> receivedEntities) {
        // We look for every major entity ( Intent ).
        boolean sendAnotherRequest = true;
        int indexOfIntentEntity = containsType(JSONUtils.ENTITY_INTENT_KEY, receivedEntities);
        if (isAValidIntent(indexOfIntentEntity, receivedEntities)) sendAnotherRequest = false;
        if (pointer <= allPossibleStringsUserHasSaid.size()-1) {
            if (sendAnotherRequest) {
                Log.e(TAG, "handleFetchedEntities: this one failed , sending another response ..");
                NetworkUtils.getResponse(allPossibleStringsUserHasSaid.get(pointer++));
            }else {
                pointer=0;
                Log.e(TAG, "checkValidity: The right one is "+pointer);
            }
        }else {
            pointer=0;
            Log.e(TAG, "handleFetchedEntities: Can't understand a single statement .");
        }
    }
    private static boolean isAValidIntent(int indexOfIntentToCheck, ArrayList<Entity> receivedEntities) {
        // We look for entities that must present in each major entity so that
        // it can be valid .
        if (indexOfIntentToCheck==-1){
            Log.e(TAG,"isValid : No intent found in the statement");
            return false;
        }
        Entity entityToCheck=receivedEntities.get(indexOfIntentToCheck);
        if (entityToCheck.getConfidence()<CONFIDENCE_THRESHOLD) {
            Log.e(TAG, "isValid: Confidence is too low");
            return false;
        }
        String nameOfIntentToCheck=entityToCheck.getValue().toString();
        if (nameOfIntentToCheck.equals(ALARM_SET_INTENT_TYPE_ENTITY)) {
            //We check if there is a datetime Entity
            if (containsType(DATETIME_ENTITY,receivedEntities)!= -1)return true;
        }else if (nameOfIntentToCheck.equals(ALARM_DELETE_INTENT_TYPE_ENTITY)) return true;
        else if (nameOfIntentToCheck.equals(ALARM_SHOW_INTENT_TYPE_ENTITY)) return true;
        return false;
    }

    private static int containsType(String mainEntityToLookFor, ArrayList<Entity> receivedEntities) {
        for (int i=0;i<receivedEntities.size();i++){
            if (receivedEntities.get(i).getName().equals(mainEntityToLookFor)) return i;
        }
        return -1;
    }


}
