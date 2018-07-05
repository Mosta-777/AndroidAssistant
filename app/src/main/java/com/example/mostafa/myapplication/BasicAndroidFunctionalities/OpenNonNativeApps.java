package com.example.mostafa.myapplication.BasicAndroidFunctionalities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mostafa on 7/3/2018.
 */

public class OpenNonNativeApps {
    private HashMap<String, String> appsMap = new HashMap<String, String>() {{
        put("الفيسبوك","com.facebook.katana");
        put("الواتس اب","com.whatsapp");
        put("جوجل","com.google.android.googlequicksearchbox");
        put("جوجل درايف","com.google.android.apps.docs");
        put("الجي ميل","com.google.android.gm");
        put("جوجل كروم","com.android.chrome");
        put("فيسبوك لايت","com.facebook.lite");
        put("جوجل مابس","com.google.android.apps.maps");
        put("الماسنجر","com.facebook.orca");
        put("اوبر","com.ubercab");
        put("كريم","com.careem.acma");
        put("اليوتيوب","com.google.android.youtube");
        put("التروكولر","com.truecaller");
        put("ساوندكلاود","com.soundcloud.android");
        put("كورا","com.quora.android");
        put("شيرت","com.lenovo.anyshare.gps");
        put("شازام","com.shazam.android");
        put("انستجرام","com.instagram.android");
        put("انغامي","com.anghami");
        put("اوليكس","com.olxmena.horizontal");
        put("تويتر","com.twitter.android");
        put("سناب شات","com.snapchat.android");
        put("كاندي كراش","com.king.candycrushsaga");
        put("فايبر","com.viber.voip");
        put("صب واي سيرفرس","com.kiloo.subwaysurf");
        put("ماسنجر لايت","com.facebook.mlite");
        put("تليجرام","org.telegram.messenger");
        put("هانجوتس","com.google.android.talk");
        put("سكايب","com.skype.raider");
        put("رتريكا","com.venticake.retrica");
        put("جوميا","com.jumia.android");
        put("سوق","com.souq.app");
        put("خان اكاديمي","org.khanacademy.android");
        put("كورسيرا","org.coursera.android");
        put("وكيبيديا","org.wikipedia");
        put("دولينجو","com.duolingo");
        put("كام سكانر","com.intsig.camscanner");
        put("جوجل كالندر","com.google.android.calendar");
        put("فايرفوكس","org.mozilla.firefox");
        put("وان درايف","com.microsoft.skydrive");
        put("اوبرا","com.opera.browser");
    }};

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface;
    public OpenNonNativeApps(CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface,
                             ArrayList<ArrayList<Entity>> theWinningSentences){
        this.analyzerInterface=analyzerInterface;
        determineTheBestSentenceForOpeningApp(theWinningSentences);
    }

    private void determineTheBestSentenceForOpeningApp(ArrayList<ArrayList<Entity>> theWinningSentences) {
        int index= IntentAnalyzerAndRecognizer.containsEntitySentenceVersion(IntentAnalyzerAndRecognizer
                .APP_NAME_ENTITY,theWinningSentences);
        if (index==-1){
            analyzerInterface.onChoosingTheWinningSentence
                    (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(0)));
            analyzerInterface.onOpeningNonNativeAppRequestingData("Tamam aft7 anhy app");
        }
        else {
            ArrayList<Entity> sentenceContainingData=theWinningSentences.get(index);
            analyzerInterface.onChoosingTheWinningSentence
                    (IntentAnalyzerAndRecognizer.extractTextFromSentence(sentenceContainingData));
            String nameOfApp = sentenceContainingData.get(IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.APP_NAME_ENTITY,sentenceContainingData))
                    .getValue().toString();
            String packageNameOfTheApp = appsMap.get(nameOfApp);
            analyzerInterface.onOpeningNonNativeAppSuccess(packageNameOfTheApp);
        }
    }

    public static boolean isPackageInstalled(Context c, String targetPackage) {
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void openApp(Context context, String appPackageName){
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
        context.startActivity( LaunchIntent );
    }


}
