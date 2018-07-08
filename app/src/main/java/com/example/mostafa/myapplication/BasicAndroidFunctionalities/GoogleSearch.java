package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 7/2/2018.
 */

public class GoogleSearch {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface;
    //Edited
    private static String querySearch = null;
    private static boolean waitingConfirmation = false;

    public GoogleSearch(CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface,
                        ArrayList<ArrayList<Entity>> theWinningSentences, boolean requestingDataMode){
        this.analyzerInterface=analyzerInterface;
        if (!requestingDataMode) determineTheBestSentenceForTheSearch(theWinningSentences);
        else {
            ArrayList<Entity> chosenSentence = theWinningSentences.get(0);
            analyzerInterface.onChoosingTheWinningSentence
                    (IntentAnalyzerAndRecognizer.extractTextFromSentence(chosenSentence));
            if(waitingConfirmation)
            {
                boolean confirm= false;
                for(int i=0;i<theWinningSentences.size();i++) {
                confirm = IntentAnalyzerAndRecognizer.containsIntentValue(IntentAnalyzerAndRecognizer
                        .CONFIRM_INTENT_TYPE_ENTITY,theWinningSentences.get(0));
                if(confirm)break;
                }
                if(confirm) {
                    analyzerInterface.onSearchSuccess(querySearch);
                    clearData();
                }

                else
                    analyzerInterface.onFailingToUnderstand();
            }
            else {
                analyzerInterface.onSearchSuccess(chosenSentence.get(IntentAnalyzerAndRecognizer
                        .containsEntity(IntentAnalyzerAndRecognizer.TEXT_ENTITY, chosenSentence))
                        .getValue().toString());
            }
        }
    }

    private void determineTheBestSentenceForTheSearch(ArrayList<ArrayList<Entity>> theWinningSentences) {
        int index=IntentAnalyzerAndRecognizer.containsEntitySentenceVersion(IntentAnalyzerAndRecognizer
                        .SEARCH_FREE_TEXT_ENTITY,theWinningSentences);
        if (index==-1){
            boolean intent = false;
            for(int i=0;i<theWinningSentences.size();i++) {

                intent = IntentAnalyzerAndRecognizer.containsIntentValue(IntentAnalyzerAndRecognizer
                        .GOOGLE_SEARCH_INTENT_TYPE_ENTITY, theWinningSentences.get(i));
                if(intent)
                    break;
            }
            if(!intent)
            {
                querySearch = (String) theWinningSentences.get(0).get(0).getValue();
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(0)));
                waitingConfirmation = true;
                analyzerInterface.onFailingToUnderstand();
            }
            else {
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(0)));
                analyzerInterface.onSearchRequestingData("Tamam asearch 3ala eh ?");
            }
        }
        else {
            ArrayList<Entity> sentenceContainingData=theWinningSentences.get(index);
            analyzerInterface.onChoosingTheWinningSentence
                    (IntentAnalyzerAndRecognizer.extractTextFromSentence(sentenceContainingData));
            String searchQuery = sentenceContainingData
                    .get(IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.SEARCH_FREE_TEXT_ENTITY,sentenceContainingData))
                    .getValue().toString();
            analyzerInterface.onSearchSuccess(searchQuery);
        }
    }

    public static void googleSearch(Context context, String whatToBeSearched){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q="+whatToBeSearched));
        context.startActivity(browserIntent);
    }
    public static void clearData()
    {
        querySearch = null;
        waitingConfirmation = false;
    }
}
