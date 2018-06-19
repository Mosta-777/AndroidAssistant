package com.example.mostafa.myapplication.POJOS;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/18/2018.
 */

public class Vote {

    private int numberOfIntentOccurances;
    private ArrayList<ArrayList<Entity>> theSentencesVoted;

    public Vote(int n,ArrayList<ArrayList<Entity>> i){
        numberOfIntentOccurances=n;
        theSentencesVoted=i;
    }

    public int getNumberOfIntentOccurances() {
        return numberOfIntentOccurances;
    }

    public void setNumberOfIntentOccurances(int numberOfIntentOccurances) {
        this.numberOfIntentOccurances = numberOfIntentOccurances;
    }

    public ArrayList<ArrayList<Entity>> getTheSentencesVoted() {
        return theSentencesVoted;
    }

    public void setTheSentencesVoted(
            ArrayList<ArrayList<Entity>> theSentencesVoted) {
        this.theSentencesVoted = theSentencesVoted;
    }
}
