package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/27/2018.
 */

public class Reminder {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private ArrayList<ArrayList<Entity>> reminderSentences;


    public Reminder(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerinterface=intentAnalyzerAndRecognizer;
        reminderSentences=theWinningSentences;
        // TODO : Begin processing on sentences
    }



}
