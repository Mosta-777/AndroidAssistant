package com.example.mostafa.myapplication;

import android.util.Log;

import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.POJOS.Vote;

import java.util.ArrayList;
import java.util.HashMap;

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
    private static ArrayList<ArrayList<Entity>> sentences=new ArrayList<>();
    private static HashMap<String,Vote> votingMap=new HashMap<>();

    public static void analyzeAndRealize(ArrayList<String> whatWasHeardFromVoiceRecognizer){
        allPossibleStringsUserHasSaid=whatWasHeardFromVoiceRecognizer;
        sentences.clear();
        votingMap.clear();
        NetworkUtils.getResponse(whatWasHeardFromVoiceRecognizer.get(pointer++));
    }
    public static void handleFetchedEntities(ArrayList<Entity> receivedEntities) {
        sentences.add(receivedEntities);
        // We look for every major entity ( Intent ).
        // We check if the response contains an intent entity .
        int indexOfIntentEntity = containsType(JSONUtils.ENTITY_INTENT_KEY, receivedEntities);
        isAValidIntent(indexOfIntentEntity, receivedEntities);
        if (pointer <= allPossibleStringsUserHasSaid.size()-1) {
                NetworkUtils.getResponse(allPossibleStringsUserHasSaid.get(pointer++));
        }else {
            pointer=0;
            Log.e(TAG, "Voting ended");
            // Check the voting of each intent in each sentence , the one with the biggest votes , the
            // highest number of data entities shall be taken .
            // The getWinnerIntent function returns the sentence ( the ArrayList of entities )
            // with the intent got the highest votes and contains the highest number of data entities .
            ArrayList<ArrayList<Entity>> theWinnerSentence=getTheWinnerSentences();
        }
    }


    // For debugging purposes .
    private static int getWinnerIndex(ArrayList<Entity> theWinnerSentence) {
        for (int i=0;i<sentences.size();i++){
            if (sentences.get(i)==theWinnerSentence) return i;
        }
        return -1;
    }

    private static ArrayList<ArrayList<Entity>> getTheWinnerSentences() {
        HashMap.Entry<String, Vote> maxEntry = null;
        for (HashMap.Entry<String, Vote> entry : votingMap.entrySet()) {

            if (maxEntry == null
                    || entry.getValue().getNumberOfIntentOccurances()
                    >(maxEntry.getValue().getNumberOfIntentOccurances())) {
                maxEntry = entry;
            }
        }
        return maxEntry.getValue().getTheSentencesVoted();
    }

    private static boolean isAValidIntent(int indexOfIntentToCheck, ArrayList<Entity> currentSentence) {
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

    private static int containsType(String mainEntityToLookFor, ArrayList<Entity> receivedEntities) {
        for (int i=0;i<receivedEntities.size();i++){
            if (receivedEntities.get(i).getName().equals(mainEntityToLookFor)) return i;
        }
        return -1;
    }


}
