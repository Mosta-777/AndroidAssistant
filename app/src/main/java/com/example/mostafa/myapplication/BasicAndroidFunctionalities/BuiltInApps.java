package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.UIs.MainActivity;

import java.util.ArrayList;

/**
 * Created by Mahmoud Salah on 6/30/2018.
 */

public class BuiltInApps {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private ArrayList<ArrayList<Entity>> winningSentences;

    public BuiltInApps(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                       ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        analyzerinterface = intentAnalyzerAndRecognizer;
        winningSentences = theWinningSentences;
        int selectedIntent = determineIntent();
        switch (selectedIntent)
        {
            case 0:analyzerinterface.onCameraSucceeded();break;
            case 1:analyzerinterface.onMusicSucceeded();break;
            case 2:analyzerinterface.onGallerySucceeded();break;
        }

    }

    private int determineIntent()
    {
        for(int i=0;i<winningSentences.size();i++)
        {
            if(IntentAnalyzerAndRecognizer.containsIntentValue(IntentAnalyzerAndRecognizer.CAMERA_INTENT_TYPE_ENTITY,
                    winningSentences.get(i))) {
                analyzerinterface
                        .onChoosingTheWinningSentence
                                (IntentAnalyzerAndRecognizer.extractTextFromSentence(winningSentences.get(i)));
                return 0;
            }
            else if(IntentAnalyzerAndRecognizer.containsIntentValue(IntentAnalyzerAndRecognizer.MUSIC_INTENT_TYPE_ENTITY,
                    winningSentences.get(i))) {
                analyzerinterface
                        .onChoosingTheWinningSentence
                                (IntentAnalyzerAndRecognizer.extractTextFromSentence(winningSentences.get(i)));
                return 1;
            }
            else if(IntentAnalyzerAndRecognizer.containsIntentValue(IntentAnalyzerAndRecognizer.GALLERY_INTENT_TYPE_ENTITY,
                    winningSentences.get(i))) {
                analyzerinterface
                        .onChoosingTheWinningSentence
                                (IntentAnalyzerAndRecognizer.extractTextFromSentence(winningSentences.get(i)));
                return 2;
            }
        }
        return 3;
    }

    public static void openCamera(Context context)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA},
                    MainActivity.CAMERA_REQUEST);
        }

        Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        //takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(takePictureIntent);
        }
    }

    public static void openMusic(Context context)
    {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        context.startActivity(intent);
    }

    public static void openGallery(Context context){
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}


