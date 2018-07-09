package com.example.mostafa.myapplication.UIs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.mostafa.myapplication.POJOS.Message;
import com.example.mostafa.myapplication.POJOS.Forecast;
import com.example.mostafa.myapplication.R;
import com.example.mostafa.myapplication.service.UserClient;

import java.util.ArrayList;
import java.util.Random;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity implements
        CommunicationInterfaces.MainActivityFunctionalityClassesInterface
        , RecognitionListener {

    public static final String baseURL="https://api.wit.ai/";
    public static final String token="EDM7POFMZLZ6H2OB253HNBAVYPBKW2RC";
    private static final int REQUEST_DEFAULT = 1;
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
    private static int requestCode = REQUEST_DEFAULT;
    private final Intent voiceRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private IntentAnalyzerAndRecognizer intentAnalyzerAndRecognizer;
    private SpeechRecognizer speech;
    private boolean isListening = false;
    private Button startOrStopListening,openKeyboard,closeKeyboard,sendCommand;
    private RecyclerView recyclerView;
    private EditText editText;
    private ArrayList<Message> messages = new ArrayList<>() ;
    private MessageListAdapter mMessageAdapter;
    private LinearLayout layoutkeyboard, layoutspeech;
    private Random randomNumber= new Random();


    Retrofit.Builder builder=new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit=builder.build();
    UserClient userClient=retrofit.create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        getSupportActionBar().hide();
        bindTheViews();
        initializeTheVoiceRecognizer();
        startOrStopListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isListening) {
                    speech.startListening(voiceRecognizer);
                } else {
                    speech.stopListening();
                }
            }
        });
        openKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutspeech.setVisibility(View.GONE);
                layoutkeyboard.setVisibility(View.VISIBLE);
            }
        });
        closeKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutkeyboard.setVisibility(View.GONE);
                layoutspeech.setVisibility(View.VISIBLE);
            }
        });
        sendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command = editText.getText().toString();
                if (!command.equals("")){
                    editText.setText("");
                    ArrayList<String> theCommand= new ArrayList<>();
                    theCommand.add(command);
                    goToTheAnalyzer(theCommand);
                }
            }
        });
    }
    private void bindTheViews() {
        messages.add(new Message("ازيك ,اقدر اساعدك ازاي؟",false));
        layoutkeyboard = (LinearLayout) findViewById(R.id.layoutkeyboard);
        layoutspeech = (LinearLayout) findViewById(R.id.layoutspeech);
        layoutkeyboard.setVisibility(View.GONE);
        layoutspeech.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        startOrStopListening = (Button) findViewById(R.id.btnSpeech);
        openKeyboard = (Button) findViewById(R.id.btnkeyboard);
        closeKeyboard = (Button) findViewById(R.id.btnnokeyboard);
        sendCommand = (Button) findViewById(R.id.btnsend);
        editText = (EditText) findViewById(R.id.editText);
        mMessageAdapter = new MessageListAdapter(this, messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        //layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mMessageAdapter);
    }
    private void initializeTheVoiceRecognizer() {
        speech = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speech.setRecognitionListener(this);
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        voiceRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MainActivity.RECORD_AUDIO_REQUEST);
        }
    }
    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        goToTheAnalyzer(results);
    }
    public void goToTheAnalyzer(ArrayList<String> results){
        if (results != null) {
            messages.add(new Message(results.get(0),true));
            mMessageAdapter.notifyDataSetChanged();
            scrollToBottom();
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
    public void onChoosingTheWinningSentence(String winningSentence) {
        messages.remove(messages.size()-1);
        mMessageAdapter.notifyDataSetChanged();
        messages.add(new Message(winningSentence,true));
        mMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private String writeAnApprovalOnChat() {
        int randomNo = randomNumber.nextInt(5) + 1;
        String stringID ="approval_"+randomNo;
        String appMessage = getResources().getString(
                getResources().getIdentifier(stringID
                        , "string", getPackageName())) ;
        messages.add(new Message(appMessage,false));
        mMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
        return stringID;
    }

    private String writeToTheMessagesRV(String code, int max){
        int randomNo = randomNumber.nextInt(max) + 1;
        String stringAndAudioName=code+"_"+randomNo;
        String appMessage = getResources().getString(
                getResources().getIdentifier(stringAndAudioName
                        , "string", getPackageName())) ;
        messages.add(new Message(appMessage,false));
        mMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
        return stringAndAudioName;
    }

    private int getAudioID(String stringAndAudioName){
        return getResources()
                .getIdentifier(stringAndAudioName,"raw",getPackageName());
    }

    private void playNormalAudio(int id){
        if ( id != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, id);
            mediaPlayer.start();
        }
    }


    @Override
    public void onAlarmSetSucceeded(String dateTime) {
        if (!Alarm.setAlarm(this,dateTime)) {
            playNormalAudio(getAudioID(writeToTheMessagesRV("alarm_set_failed",2)));
        }
        else  playNormalAudio(getAudioID(writeToTheMessagesRV("alarm_set_appr",2)));
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onAlarmSetRequestingData(String message) {
        int audioID=getAudioID(writeToTheMessagesRV("alarm_set_when",2));
        if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_ALARM_DATA;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }
    @Override
    public void onAlarmShowSucceeded(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        Alarm.showAlarm(this);
    }
    @Override
    public void onAlarmDeleteSucceeded(String message) {
        writeToTheMessagesRV("alarm_delete_appr",1);
        Alarm.showAlarm(this);
    }

    @Override
    public void onGettingWitResponseFailed(String failingMessage) {
        Toast.makeText(this,failingMessage,Toast.LENGTH_LONG).show();
        writeToTheMessagesRV("problem_internet",1);
    }

    @Override
    public void onFlashLightOn(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        Flashlight flashlight = new Flashlight(this);
        if (flashlight.flashLightOn()) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        }
    }

    @Override
    public void onFlashLightOff(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        Flashlight flashlight = new Flashlight(this);
        if (flashlight.flashLightOff()) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        }
        else Toast.makeText(this,"Couldn't close the flashlight.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelling(String intentToCancel) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        switch (intentToCancel) {
            case IntentAnalyzerAndRecognizer.REMINDER_INTENT_TYPE_ENTITY:
                Reminder.resetReminder();
                break;
            case IntentAnalyzerAndRecognizer.SMS_SEND_INTENT_TYPE_ENTITY:
                SendingSMS.clearData();
                break;
            case IntentAnalyzerAndRecognizer.GOOGLE_SEARCH_INTENT_TYPE_ENTITY:
                GoogleSearch.clearData();
                break;
        }
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onCancellingWhat(String message) {
        playNormalAudio(getAudioID(writeToTheMessagesRV("cancel_what",1)));
    }

    @Override
    public void onFailingToUnderstand() {
        int audioID = getAudioID(writeToTheMessagesRV("no_intent",2));
        if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_GOOGLE_SEARCH;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }

    @Override
    public void onShowCallLog(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        Calling.showCallLog(this);
    }

    @Override
    public void onShowContacts(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        Calling.showContacts(this);
    }

    @Override
    public void onReminderSucceeded(String dateTime, String reminderFreeText) {
        if(Reminder.setReminder(MainActivity.this, dateTime, reminderFreeText)) {
            playNormalAudio(getAudioID(writeToTheMessagesRV("reminder_appr",3)));
        }else {
            Toast.makeText(this, "Fe moshkla", Toast.LENGTH_LONG).show();
        }
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onReminderRequestingData(boolean dateTimeExists, boolean reminderFreeTextExists) {
        int audioID = 0;
        if(dateTimeExists) {
            audioID = getAudioID(writeToTheMessagesRV("reminder_time",2));
        }
        else if(reminderFreeTextExists) {
            audioID = getAudioID(writeToTheMessagesRV("reminder_free_text",2));
        }if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_REMINDER_DATA;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }

    @Override
    public void onCallingNumberSucceeded(final String phoneNumber) {
        writeToTheMessagesRV("calling_appr",2);
        requestCode = REQUEST_DEFAULT ;
        Calling.call(this,phoneNumber);
    }

    @Override
    public void onCallingNumberRequestingData(String message) {
        int audioID = getAudioID(writeToTheMessagesRV("calling_who",2));
        if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_PHONE_NUMBER;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }

    @Override
    public void onCallingByName(String name) {
        Calling.callByName(this,name);
    }

    @Override
    public void onCallingContactNotFound(String message) {
        playNormalAudio(getAudioID(writeToTheMessagesRV("calling_no",2)));
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onNormalModeOn(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        Profiles.putOnNormalMode(this);
    }

    @Override
    public void onSilentModeOn(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        Profiles.putOnSilentMode(this);
    }

    @Override
    public void onVibrationModeOn(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        Profiles.putOnVibrationMode(this);
    }

    @Override
    public void onSmsShow(String message) {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        SendingSMS.showSms(this);
    }

    @Override
    public void onSmsSendSucceeded(String contactName, String smsBody) {
        if(SendingSMS.findNumber(this)){
            playNormalAudio(getAudioID(writeToTheMessagesRV("sms_send_appr",2)));
            SendingSMS.sendMessage(this,contactName,smsBody);
        }
        else{
            SendingSMS.clearData();
            playNormalAudio(getAudioID(writeToTheMessagesRV("calling_no",2)));
        }
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onSmsSendRequestingData(boolean contactNameExists, boolean smsBodyExists) {
        int audioID = 0 ;
        if(!contactNameExists && !smsBodyExists){
            audioID = getAudioID(writeToTheMessagesRV("sms_send_who",2));
        }
        else if(!smsBodyExists) {
            audioID = getAudioID(writeToTheMessagesRV("sms_send_what",2));
        }
        if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_SMS_DATA;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }
    @Override
    public void onSmsSendFailed(String message){}

    @Override
    public void onSearchSuccess(String message) {
        playNormalAudio(getAudioID(writeToTheMessagesRV("google_search_approval",2)));
        GoogleSearch.googleSearch(this,message);
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onSearchRequestingData(String message) {
        int audioID = getAudioID(writeToTheMessagesRV("google_search_what",2));
        if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_GOOGLE_SEARCH;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }


    @Override
    public void onOpeningNonNativeAppSuccess(String appPackageName) {
        if (OpenNonNativeApps.isPackageInstalled(this,appPackageName)) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
            OpenNonNativeApps.openApp(this, appPackageName);
        }
        else {
            playNormalAudio(getAudioID(writeToTheMessagesRV("app_open_no",2)));
        }
        requestCode = REQUEST_DEFAULT ;
    }

    @Override
    public void onOpeningNonNativeAppRequestingData(String message) {
        int audioID = getAudioID(writeToTheMessagesRV("app_open_what",1));
        if ( audioID != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    requestCode = REQUEST_OPEN_APPS;
                    speech.startListening(voiceRecognizer);
                }
            });
        }
    }


    @Override
    public void onWiFiOffSucceeded() {
        if(WiFiAndBluetooth.setWifi(this, false)) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        }
        else {
            playNormalAudio(getAudioID(writeToTheMessagesRV("already_closed",1)));
        }
    }

    @Override
    public void onWiFiOnSucceeded() {
        if(WiFiAndBluetooth.setWifi(this, true)) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        }
        else {
            playNormalAudio(getAudioID(writeToTheMessagesRV("already_opened",1)));
        }
    }

    @Override
    public void onBluetoothOnSucceeded() {
        if (WiFiAndBluetooth.setBluetooth(this, true)) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        }
        else {
            playNormalAudio(getAudioID(writeToTheMessagesRV("already_opened",1)));
        }
    }

    @Override
    public void onBluetoothOffSucceeded() {
        if (WiFiAndBluetooth.setBluetooth(this, false)) {
            playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        }
        else {
            playNormalAudio(getAudioID(writeToTheMessagesRV("already_closed",1)));
        }
    }

    @Override
    public void onMusicSucceeded() {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        BuiltInApps.openMusic(this);
    }

    @Override
    public void onGallerySucceeded() {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
        BuiltInApps.openGallery(this);
    }

    @Override
    public void onCameraSucceeded() {
        playNormalAudio(getAudioID(writeAnApprovalOnChat()));
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
        playNormalAudio(getAudioID(writeToTheMessagesRV("weather",1)));
        messages.add(new Message(Weather.getWeather(weather),false));
        scrollToBottom();
    }

    @Override
    public void onGreetingSucceeded() {
        playNormalAudio(getAudioID(writeToTheMessagesRV("greeting", 3)));
    }

    @Override public void onReadyForSpeech(Bundle bundle) {
        isListening = true ;
    }
    @Override public void onBeginningOfSpeech() {
        startOrStopListening.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btnlistening, 0, 0, 0);
        isListening = true ;

    }
    @Override public void onEndOfSpeech() {
        startOrStopListening.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btnspeech, 0, 0, 0);
        isListening = false;
    }
    @Override public void onError(int i) {
        isListening = false;
        playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
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
    @Override public void onFunctionListSucceeded() {
        writeToTheMessagesRV("functions_list",1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for the Camera", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
            case CALL_PHONE_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for the phone call", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for the phone call", Toast.LENGTH_SHORT).show();
                }break;
            case READ_CONTACTS_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for reading contacts", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for reading contacts", Toast.LENGTH_SHORT).show();
                }break;
            case WRITE_CALENDAR_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for writing in calender", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for writing in calender", Toast.LENGTH_SHORT).show();
                }break;
            case WIFI_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for change wifi status", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for change wifi status", Toast.LENGTH_SHORT).show();
                }break;
            case BLUETOOTH_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for change Bluetooth status", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for change Bluetooth status", Toast.LENGTH_SHORT).show();
                }break;
            case BLUETOOTH_ADMIN_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for Bluetooth Amin", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for Bluetooth Amin", Toast.LENGTH_SHORT).show();
                }break;
            case RECORD_AUDIO_REQUEST:
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted for voice recognition", Toast.LENGTH_SHORT).show();
                } else {
                    playNormalAudio(getAudioID(writeToTheMessagesRV("problem",1)));
                    Toast.makeText(MainActivity.this, "Permission Denied for voice recognition", Toast.LENGTH_SHORT).show();
                }break;


        }
    }
    public void scrollToBottom() {
        recyclerView.scrollToPosition(messages.size() - 1);
    }
    @Override public void onRmsChanged(float v) {}
    @Override public void onBufferReceived(byte[] bytes) {}
    @Override public void onPartialResults(Bundle bundle) {}
    @Override public void onEvent(int i, Bundle bundle) {}
}
