package com.example.mostafa.myapplication.UIs;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Alarm;
import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.R;
import com.example.mostafa.myapplication.service.UserClient;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity implements
        CommunicationInterfaces.MainActivityFunctionalityClassesInterface {

    public static final String baseURL="https://api.wit.ai/";
    public static final String token="EDM7POFMZLZ6H2OB253HNBAVYPBKW2RC";
    private final int REQUEST_DEFAULT = 1;
    private final int REQUEST_ALARM_DATA = 2;
    private Intent voiceRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

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
        if(requestCode == REQUEST_DEFAULT && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confidence=data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
            ArrayList<String> endResults=new ArrayList<>();
            for (int i=0;i<results.size();i++){
                endResults.add(results.get(i)+"     "+confidence[i]);
            }
            lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , endResults));
            new IntentAnalyzerAndRecognizer(this,results);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAlarmSetSucceeded() {

    }

    @Override
    public void onAlarmSetRequestingData() {
        // TODO for voice over : " Eshta 3ayz tzboto 3ala il sa3a kam "
        // TODO : open the simple activity or fragment or whatever of setting the alarm
        // TODO : open the voice recognition .
    }

    @Override
    public void onAlarmShowSucceeded() {
        // TODO for voice over : " Tammam eshta il mnbhat ahy "
        Alarm.showAlarm(this);
    }

    @Override
    public void onAlarmDeleteSucceeded() {

    }

    @Override
    public void onGettingWitResponseFailed(String failingMessage) {
        Toast.makeText(this,failingMessage,Toast.LENGTH_LONG).show();
    }

}
