package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/27/2018.
 */

public class Calling {
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private ArrayList<ArrayList<Entity>> callingSentences;


    public Calling(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerinterface=intentAnalyzerAndRecognizer;
        callingSentences=theWinningSentences;
    }
}
