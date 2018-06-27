package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.util.Log;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/27/2018.
 */

public class Reminder {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private ArrayList<ArrayList<Entity>> reminderSentences;
    private static String dateTime = null;
    private static String whatToBeReminded = null;


    public Reminder(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerinterface=intentAnalyzerAndRecognizer;
        reminderSentences=theWinningSentences;
        determineTheBestSentenceForReminderSet(theWinningSentences);
    }

    private void determineTheBestSentenceForReminderSet(ArrayList<ArrayList<Entity>> reminderSentences)
    {
        ArrayList<Entity> selectedSentence = null;
        //If we have all the data to set a reminder
        for(int i=0; i<reminderSentences.size(); i++)
        {
            if((isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,reminderSentences.get(i)))
                    & isThereOnlyOne(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i)))
            {
                selectedSentence = reminderSentences.get(i);
                break;
            }
        }

        if(selectedSentence!=null)
        {
            Log.e("FULL REMINDER","FULL REMINDER REACHED");
        }
        else{

        }
    }

    private boolean isThereOnlyOne(String entityName, ArrayList<Entity> sentence)
    {
        int counter=0;
        for (int i=0;i<sentence.size();i++){
            if (sentence.get(i).getName().equals(entityName)){
                counter++;
                if (counter>=2) return false;
            }
        }
        return counter == 1;
    }
}
