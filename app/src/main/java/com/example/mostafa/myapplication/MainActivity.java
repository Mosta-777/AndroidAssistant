package com.example.mostafa.myapplication;

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
public class MainActivity extends AppCompatActivity {

    public static final String baseURL="https://api.wit.ai/";
    public static final String token="EDM7POFMZLZ6H2OB253HNBAVYPBKW2RC";
    private static final int RESULT_SPEECH = 1;

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
        lv = (ListView) findViewById(R.id.listview1);
        b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent voiceRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
                voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar");
                startActivityForResult(voiceRecognizer, RESULT_SPEECH);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_SPEECH && requestCode == RESULT_OK);
        {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confidence=data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
            ArrayList<String> endResults=new ArrayList<>();
            for (int i=0;i<results.size();i++){
                endResults.add(results.get(i)+"     "+confidence[i]);
            }
            lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , endResults));
            IntentAnalyzerAndRecognizer.analyzeAndRealize(results);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /*private void getSecret(String message){
        Call<ResponseBody> call=userClient.getSecret("https://api.wit.ai/message?v=28/11/2017&q="+message,
                " Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if (response.isSuccessful()){
               *//* try {
                    txtView.setText(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*
               //Toast.makeText(getApplicationContext(),"Response received , processing ....",Toast.LENGTH_LONG).show();
                try {
                    ArrayList<Entity> receivedEntities=JSONUtils.getEntitesFromJSONResponse(response.body().string());
                    Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(),"Response is not successful",Toast.LENGTH_LONG).show();
            }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),"Call is not successful",Toast.LENGTH_LONG).show();
            }
        });
    }*/
}
