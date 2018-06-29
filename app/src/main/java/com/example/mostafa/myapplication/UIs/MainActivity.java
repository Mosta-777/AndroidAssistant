package com.example.mostafa.myapplication.UIs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Alarm;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Calling;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Flashlight;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Reminder;
import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.R;
import com.example.mostafa.myapplication.service.UserClient;

import java.util.ArrayList;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity implements
        CommunicationInterfaces.MainActivityFunctionalityClassesInterface {

    public static final String baseURL="https://api.wit.ai/";
    public static final String token="EDM7POFMZLZ6H2OB253HNBAVYPBKW2RC";
    private final int REQUEST_DEFAULT = 1;
    private final int REQUEST_ALARM_DATA = 2;
    private final int REQUEST_REMINDER_DATA = 3;
    private static final int REQUEST_PHONE_NUMBER = 4;
    public static final int CALL_PHONE_REQUEST = 100;
    public static final int CAMERA_REQUEST=200;
    public static final int READ_CONTACTS_REQUEST = 300;
    private Intent voiceRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private IntentAnalyzerAndRecognizer intentAnalyzerAndRecognizer;

    Button b1 ;
    ListView lv;

    Retrofit.Builder builder=new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit=builder.build();
    UserClient userClient=retrofit.create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeTheVoiceRecognizer();
        lv = (ListView) findViewById(R.id.listview1);
        b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(voiceRecognizer, REQUEST_DEFAULT);
            }
        });
    }
    private void initializeTheVoiceRecognizer() {
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        float[] confidence=data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
        ArrayList<String> endResults=new ArrayList<>();
        for (int i=0;i<results.size();i++){
            endResults.add(results.get(i)+"     "+confidence[i]);
        }
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , endResults));
        if(requestCode == REQUEST_DEFAULT && resultCode == RESULT_OK) {
            intentAnalyzerAndRecognizer = new IntentAnalyzerAndRecognizer(this,results);
        }else if (requestCode==REQUEST_ALARM_DATA && resultCode == RESULT_OK){
            intentAnalyzerAndRecognizer.analyzeAndRealize(results,IntentAnalyzerAndRecognizer.ALARM_SET_INTENT_TYPE_ENTITY);
        }
        else if (requestCode==REQUEST_REMINDER_DATA && resultCode==RESULT_OK){
            intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.REMINDER_INTENT_TYPE_ENTITY);
        }else if (requestCode==REQUEST_PHONE_NUMBER && resultCode==RESULT_OK){
            intentAnalyzerAndRecognizer.analyzeAndRealize(results,IntentAnalyzerAndRecognizer.CONTACTS_CALL_INTENT_TYPE_INTENTY);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAlarmSetSucceeded(String dateTime) {
        if (!Alarm.setAlarm(this,dateTime))
            Toast.makeText(this,getResources().getString(R.string.set_alarm_failed),Toast.LENGTH_LONG).show();
        else ;// TODO for voice over : " Tamam eshta il mnbh itzabat "
    }

    @Override
    public void onAlarmSetRequestingData(String message) {
        // TODO for voice over : " Eshta 3ayz tzboto 3ala il sa3a kam "
        // TODO : open the simple activity or fragment or whatever of setting the alarm
        // TODO : open the voice recognition .
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        startActivityForResult(voiceRecognizer,REQUEST_ALARM_DATA);
    }

    @Override
    public void onAlarmShowSucceeded(String message) {
        // TODO for voice over : " Tammam eshta il mnbhat ahy "
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        Alarm.showAlarm(this);
    }

    @Override
    public void onAlarmDeleteSucceeded(String message) {
        // TODO for voice over : " T2dar tms7o mn il app "
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        Alarm.showAlarm(this);
    }

    @Override
    public void onGettingWitResponseFailed(String failingMessage) {
        Toast.makeText(this,failingMessage,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFlashLightOn(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        Flashlight flashlight = new Flashlight(this);
        if (flashlight.flashLightOn())
            // TODO voice over : " Tamam " or " Eshta "
            Toast.makeText(this,"Opened flashlight successfully .",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this,"Couldn't open the flashlight.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFlashLightOff(String message) {
        // TODO voice over : " Tamam " or " Eshta "
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        Flashlight flashlight = new Flashlight(this);
        if (flashlight.flashLightOff())
            Toast.makeText(this,"Closed flashlight successfully .",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this,"Couldn't close the flashlight.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelling(String intentToCancel) {
        // TODO voice over : " Tamam cancelt "
        Toast.makeText(this," Tamam cancelt "+intentToCancel,Toast.LENGTH_LONG).show();
        // if the intent to cancel stored data in the shared pref. , delete it
        // else do nothing
    }

    @Override
    public void onCancellingWhat(String message) {
        // TODO voice over : " a cancel eh mfeesh 7aga "
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailingToUnderstand(String message) {
        // TODO voice over : " msh fahm inta asdk eh m3lsh " or " deh results il web "
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowCallLog(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Calling.showCallLog(this);
    }

    @Override
    public void onShowContacts(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Calling.showContacts(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for the Camera", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
            case CALL_PHONE_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for the phone call", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for the phone call", Toast.LENGTH_SHORT).show();
                }
            case READ_CONTACTS_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for reading contacts", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for reading contacts", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onReminderSucceeded(String dateTime, String reminderFreeText) {
        if(Reminder.setReminder(MainActivity.this, dateTime, reminderFreeText))
            Toast.makeText(this,"Tamam reminder is set at  " + reminderFreeText+ "   at  " + dateTime,Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Fe moshkla",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReminderRequestingData(boolean dateTimeExists, boolean reminderFreeTextExists) {
        String missingData = null;
        if(dateTimeExists&&reminderFreeTextExists)
            missingData = "afakarak be eh wl sa3a kam ?";
        else if(dateTimeExists)
            missingData = "tmam, afakar beh emta ?";
        else if(reminderFreeTextExists)
            missingData = "tamam, afakrak b eh ?";
        Toast.makeText(this,missingData,Toast.LENGTH_LONG).show();
        startActivityForResult(voiceRecognizer,REQUEST_REMINDER_DATA);
    }

    @Override
    public void onCallingNumberSucceeded(String phoneNumber) {
        // TODO voice over : " tamam htsl dlw2ty "
        Calling.call(this,phoneNumber);
    }

    @Override
    public void onCallingNumberRequestingData(String message) {
        // TODO voice over : " tamam aklm meen "
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        startActivityForResult(voiceRecognizer,REQUEST_PHONE_NUMBER);
    }

    @Override
    public void onCallingByName(String name) {
        Calling.callByName(this,name);
    }

    @Override
    public void onCallingContactNotFound(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}
