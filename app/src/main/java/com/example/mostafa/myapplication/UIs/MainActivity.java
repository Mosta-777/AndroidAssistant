package com.example.mostafa.myapplication.UIs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Alarm;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.BuiltInApps;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Calling;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Flashlight;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.GoogleSearch;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.OpenNonNativeApps;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Profiles;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Reminder;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.SendingSMS;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Weather;
import com.example.mostafa.myapplication.BasicAndroidFunctionalities.WiFiAndBluetooth;
import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Forecast;
import com.example.mostafa.myapplication.R;
import com.example.mostafa.myapplication.service.UserClient;

import java.util.ArrayList;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity implements
        CommunicationInterfaces.MainActivityFunctionalityClassesInterface
        , RecognitionListener {

    public static final String baseURL="https://api.wit.ai/";
    public static final String token="EDM7POFMZLZ6H2OB253HNBAVYPBKW2RC";
    private final int REQUEST_DEFAULT = 1;
    private final int REQUEST_ALARM_DATA = 2;
    private final int REQUEST_REMINDER_DATA = 3;
    private static final int REQUEST_PHONE_NUMBER = 4;
    private static final int REQUEST_GOOGLE_SEARCH = 5;
    private static final int REQUEST_OPEN_APPS = 6;
    private static final int REQUEST_SMS_DATA = 7;
    public static final int CALL_PHONE_REQUEST = 100;
    public static final int CAMERA_REQUEST=200;
    public static final int READ_CONTACTS_REQUEST = 300;
    public static final int WRITE_CALENDAR_REQUEST = 400;
    public static final int WIFI_REQUEST = 500;
    public static final int BLUETOOTH_REQUEST = 600;
    public static final int BLUETOOTH_ADMIN_REQUEST = 700;
    private static final int RECORD_AUDIO_REQUEST = 800;
    private static int requestCode;
    private final Intent voiceRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private IntentAnalyzerAndRecognizer intentAnalyzerAndRecognizer;
    private SpeechRecognizer speech;
    private boolean isListening = false;
    Button b1 ;
    ListView lv;
    TextView tv;

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
        tv = findViewById(R.id.rms);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //startActivityForResult(voiceRecognizer, REQUEST_DEFAULT);
                if(!isListening) {
                    requestCode = REQUEST_DEFAULT ;
                    speech.startListening(voiceRecognizer);
                }
                else {
                    speech.stopListening();
                }
            }
        });
    }
    private void initializeTheVoiceRecognizer() {
        /*voiceRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar");*/
        speech = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speech.setRecognitionListener(this);
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MainActivity.RECORD_AUDIO_REQUEST);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== RESULT_OK && data!=null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confidence = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
            ArrayList<String> endResults = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                endResults.add(results.get(i) + "     " + confidence[i]);
            }
            lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, endResults));
            if (requestCode == REQUEST_DEFAULT) {
                intentAnalyzerAndRecognizer = new IntentAnalyzerAndRecognizer(this, results);
            } else if (requestCode == REQUEST_ALARM_DATA) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.ALARM_SET_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_REMINDER_DATA) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.REMINDER_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_PHONE_NUMBER) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.CONTACTS_CALL_INTENT_TYPE_ENTITY);
            } else if (requestCode==REQUEST_GOOGLE_SEARCH){
                intentAnalyzerAndRecognizer.analyzeAndRealize(results,IntentAnalyzerAndRecognizer.GOOGLE_SEARCH_INTENT_TYPE_ENTITY);
            } else if (requestCode==REQUEST_OPEN_APPS){
                intentAnalyzerAndRecognizer.analyzeAndRealize(results,IntentAnalyzerAndRecognizer.OPEN_APPS_INTENT_TYPE_ENTITY);
            } else if(requestCode==REQUEST_SMS_DATA){
                intentAnalyzerAndRecognizer.analyzeAndRealize(results,IntentAnalyzerAndRecognizer.SMS_SEND_INTENT_TYPE_ENTITY);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/

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
        //startActivityForResult(voiceRecognizer,REQUEST_ALARM_DATA);
        requestCode = REQUEST_ALARM_DATA;
        speech.startListening(voiceRecognizer);
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
        if(intentToCancel.equals(IntentAnalyzerAndRecognizer.REMINDER_INTENT_TYPE_ENTITY))
            Reminder.resetReminder();
        else if(intentToCancel.equals(IntentAnalyzerAndRecognizer.SMS_SEND_INTENT_TYPE_ENTITY))
            SendingSMS.clearData();
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
                }break;
            case READ_CONTACTS_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for reading contacts", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for reading contacts", Toast.LENGTH_SHORT).show();
                }break;
            case WRITE_CALENDAR_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for writing in calender", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for writing in calender", Toast.LENGTH_SHORT).show();
                }break;
            case WIFI_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for change wifi status", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for change wifi status", Toast.LENGTH_SHORT).show();
                }break;
            case BLUETOOTH_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for change Bluetooth status", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for change Bluetooth status", Toast.LENGTH_SHORT).show();
                }break;
            case BLUETOOTH_ADMIN_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for Bluetooth Amin", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for Bluetooth Amin", Toast.LENGTH_SHORT).show();
                }break;
            case RECORD_AUDIO_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for voice recognition", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for voice recognition", Toast.LENGTH_SHORT).show();
                }break;


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
        if(dateTimeExists)
            missingData = "tmam, afakar beh emta ?";
        else if(reminderFreeTextExists)
            missingData = "tamam, afakrak b eh ?";
        Toast.makeText(this,missingData,Toast.LENGTH_LONG).show();
        //startActivityForResult(voiceRecognizer,REQUEST_REMINDER_DATA);
        requestCode = REQUEST_REMINDER_DATA;
        speech.startListening(voiceRecognizer);
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
        //startActivityForResult(voiceRecognizer,REQUEST_PHONE_NUMBER);
        requestCode = REQUEST_PHONE_NUMBER;
        speech.startListening(voiceRecognizer);
    }

    @Override
    public void onCallingByName(String name) {
        Calling.callByName(this,name);
    }

    @Override
    public void onCallingContactNotFound(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNormalModeOn(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Profiles.putOnNormalMode(this);
    }

    @Override
    public void onSilentModeOn(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Profiles.putOnSilentMode(this);
    }

    @Override
    public void onVibrationModeOn(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Profiles.putOnVibrationMode(this);
    }

    @Override
    public void onSmsShow(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        SendingSMS.showSms(this);
    }

    @Override
    public void onSmsSendSucceeded(String contactName, String smsBody) {
        SendingSMS.sendMessage(this,contactName,smsBody);
        Toast.makeText(this, "Sending "+smsBody+" to "+contactName, Toast.LENGTH_SHORT).show();
        //TODO VO: "Lw 3ayz t confirm dos enter"

    }

    @Override
    public void onSmsSendRequestingData(boolean contactNameExists, boolean smsBodyExists) {
        if(!contactNameExists && !smsBodyExists){
            Toast.makeText(this,"Ab3at l sms l meen",Toast.LENGTH_LONG).show();
            //TODO VO: "Ab3at l sms l meen"
        }
        else if(!smsBodyExists) {
            Toast.makeText(this, "Ab3at a2olo eh ?", Toast.LENGTH_LONG).show();
            //TODO VO: "a2ool eh fl sms"
        }
        //startActivityForResult(voiceRecognizer,REQUEST_SMS_DATA);
        requestCode = REQUEST_SMS_DATA;
        speech.startListening(voiceRecognizer);
    }
    @Override
    public void onSmsSendFailed(String message){}

    @Override
    public void onSearchSuccess(String message) {
        // TODO voice over : "Tamam hya de ntayg il search"
        Toast.makeText(this, "Tamam hasearch", Toast.LENGTH_SHORT).show();
        GoogleSearch.googleSearch(this,message);
    }

    @Override
    public void onSearchRequestingData(String message) {
        // TODO voice over : "Eshta 3ayz tsearch 3ala eh "
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        //startActivityForResult(voiceRecognizer,REQUEST_GOOGLE_SEARCH);
        requestCode = REQUEST_GOOGLE_SEARCH;
        speech.startListening(voiceRecognizer);
    }

    @Override
    public void onOpeningNonNativeAppSuccess(String appPackageName) {
        // TODO voice over : approval
        Toast.makeText(this, "Opening "+appPackageName, Toast.LENGTH_SHORT).show();
        if (OpenNonNativeApps.isPackageInstalled(this,appPackageName))
            OpenNonNativeApps.openApp(this,appPackageName);
        else Toast.makeText(this, "Il app msh installed 3andk", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOpeningNonNativeAppRequestingData(String message) {
        // TODO voice over : "Eshta eh esm il app illi 3ayzo ytft7"
        //startActivityForResult(voiceRecognizer,REQUEST_OPEN_APPS);
        requestCode = REQUEST_OPEN_APPS;
        speech.startListening(voiceRecognizer);
    }

    @Override
    public void onWiFiOffSucceeded() {

        if(WiFiAndBluetooth.setWifi(this, false))
            // TODO voice over : approval
            Toast.makeText(this,"Tamam El Wifi et2afal",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Howa ma2fool aslan",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWiFiOnSucceeded() {
        if(WiFiAndBluetooth.setWifi(this, true))
            // TODO voice over : approval
            Toast.makeText(this,"Tamam El Wifi etfata7",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Howa mafto7 aslan",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBluetoothOnSucceeded() {
        if (WiFiAndBluetooth.setBluetooth(this, true))
            // TODO voice over : approval
            Toast.makeText(this,"Tamam fata7t l bluetooth", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"l bluetooth maftoo7 aslan", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBluetoothOffSucceeded() {

        if (WiFiAndBluetooth.setBluetooth(this, false))
            // TODO voice over : approval
            Toast.makeText(this,"Tamam 2afalt l bluetooth", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"l bluetooth ma2fool aslan", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMusicSucceeded() {
        // TODO voice over : approval
        BuiltInApps.openMusic(this);
    }

    @Override
    public void onGallerySucceeded() {
        // TODO voice over : approval
        BuiltInApps.openGallery(this);

    }

    @Override
    public void onCameraSucceeded() {
        // TODO voice over : approval
        BuiltInApps.openCamera(this);
    }

    @Override
    public void onWeatherSucceeded(String url) {
        ArrayList<Forecast> weather = null;
        try{
            weather = new Weather.fetchForecastData().execute(url).get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> weatherData = Weather.getWeather(weather);
        Toast.makeText(this,weatherData.get(0)+"\n"+weatherData.get(1) +"\n" +weatherData.get(2)+"\n" +weatherData.get(3),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {
        isListening = true;
    }

    @Override
    public void onRmsChanged(float v) {
        tv.setText(String.valueOf(v));
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        isListening = false;
        speech.stopListening();
    }

    @Override
    public void onError(int i) {
        isListening = false;
        Log.e("onError","Errooor");
        switch (i) {

            case SpeechRecognizer.ERROR_AUDIO:

                Log.e("ERROR_AUDIO","Errooor");

                break;

            case SpeechRecognizer.ERROR_CLIENT:

                Log.e("ERROR_CLIENT","Errooor");

                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:

                Log.e("ERROR_PERMISSIONS","Errooor");

                break;

            case SpeechRecognizer.ERROR_NETWORK:

                Log.e("ERROR_NETWORK","Errooor");

                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:

                Log.e("ERROR_NETWORK_TIMEOUT","Errooor");

                break;

            case SpeechRecognizer.ERROR_NO_MATCH:

                Log.e("ERROR_NO_MATCH","Errooor");

                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:

                Log.e("ERROR_RECOGNIZER_BUSY","Errooor");

                break;

            case SpeechRecognizer.ERROR_SERVER:

                Log.e("ERROR_SERVER","Errooor");

                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:

                Log.e("ERROR_SPEECH_TIMEOUT","Errooor");

                break;

            default:

                Log.e("Default","Errooor");

                break;
        }


    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (results != null) {
            lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));
            if (requestCode == REQUEST_DEFAULT) {
                intentAnalyzerAndRecognizer = new IntentAnalyzerAndRecognizer(this, results);
            } else if (requestCode == REQUEST_ALARM_DATA) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.ALARM_SET_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_REMINDER_DATA) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.REMINDER_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_PHONE_NUMBER) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.CONTACTS_CALL_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_GOOGLE_SEARCH) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.GOOGLE_SEARCH_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_OPEN_APPS) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.OPEN_APPS_INTENT_TYPE_ENTITY);
            } else if (requestCode == REQUEST_SMS_DATA) {
                intentAnalyzerAndRecognizer.analyzeAndRealize(results, IntentAnalyzerAndRecognizer.SMS_SEND_INTENT_TYPE_ENTITY);
            }
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
