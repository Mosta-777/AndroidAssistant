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

    public GoogleSearch(CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface,
                        ArrayList<ArrayList<Entity>> theWinningSentences, boolean requestingDataMode){
        this.analyzerInterface=analyzerInterface;
        if (!requestingDataMode) determineTheBestSentenceForTheSearch(theWinningSentences);
        else {
            ArrayList<Entity> chosenSentence=theWinningSentences.get(0);
            analyzerInterface.onSearchSuccess(chosenSentence.get(IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.TEXT_ENTITY,chosenSentence))
                    .getValue().toString());
        }
    }

    private void determineTheBestSentenceForTheSearch(ArrayList<ArrayList<Entity>> theWinningSentences) {
        int index=IntentAnalyzerAndRecognizer.containsEntitySentenceVersion(IntentAnalyzerAndRecognizer
                        .SEARCH_FREE_TEXT_ENTITY,theWinningSentences);
        if (index==-1)analyzerInterface.onSearchRequestingData("Tamam asearch 3ala eh ?");
        else {
            ArrayList<Entity> sentenceContainingData=theWinningSentences.get(index);
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
}
