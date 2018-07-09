package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.UIs.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Mostafa on 6/27/2018.
 */

public class Reminder {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private static String dateTime = null;
    private static String whatToBeReminded = null;
    private static boolean welcomeBack = false;
    private static String winningSentence = "";


    public Reminder(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerinterface=intentAnalyzerAndRecognizer;
        determineTheBestSentenceForReminderSet(theWinningSentences);
    }

    private void determineTheBestSentenceForReminderSet(ArrayList<ArrayList<Entity>> reminderSentences)
    {
        //ArrayList<Entity> selectedSentence = null;
        if(!welcomeBack){
            //If we have all the data to set a reminder
            if(findDateAndFreeText(reminderSentences))
            {
                analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                analyzerinterface.onReminderSucceeded(dateTime, whatToBeReminded);
                resetReminder();
            }
            else if(findDate(reminderSentences))
            {
                //If we have date in the given sentences
                welcomeBack = true;
                analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                analyzerinterface.onReminderRequestingData(false,true);
            }
            else if(findFreeText(reminderSentences))
            {
                welcomeBack = true;
                //If we have free text in the given sentences
                analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                analyzerinterface.onReminderRequestingData(true, false);
            }
            else
            {
                //If we have no data for the reminder
                welcomeBack=true;
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(reminderSentences.get(0));
                analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                analyzerinterface.onReminderRequestingData(false, true);
            }
        }
        else
        {
            if((dateTime==null && whatToBeReminded==null))
            {
                ArrayList<ArrayList<Entity>> tempSentences = new ArrayList<>();
                tempSentences.add(reminderSentences.get(0));
                if (findDate(tempSentences))
                {
                    whatToBeReminded = (String)reminderSentences.get(0).get(0).getValue();
                    winningSentence = whatToBeReminded;
                    analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                    analyzerinterface.onReminderSucceeded(dateTime, whatToBeReminded);
                    resetReminder();
                }
                else {
                    whatToBeReminded = (String) reminderSentences.get(0).get(0).getValue();
                    winningSentence = whatToBeReminded;
                    analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                    analyzerinterface.onReminderRequestingData(true, false);
                }
            }
            else if(dateTime!=null && whatToBeReminded==null)
            {
                whatToBeReminded = (String)reminderSentences.get(0).get(0).getValue();
                winningSentence = whatToBeReminded;
                analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                analyzerinterface.onReminderSucceeded(dateTime, whatToBeReminded);
                resetReminder();
            }
            else if(dateTime==null)
            {
                if(findDate(reminderSentences))
                {
                    analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                    analyzerinterface.onReminderSucceeded(dateTime, whatToBeReminded);
                    resetReminder();
                }
                else
                {
                    winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(reminderSentences.get(0));
                    analyzerinterface.onChoosingTheWinningSentence(winningSentence);
                    analyzerinterface.onReminderRequestingData(true, false);
                }
            }

        }
        winningSentence = null;
    }

    public static boolean setReminder(Context context, String reminderDateTime, String reminderFreeText) {

        int reminderYear = Integer.parseInt(reminderDateTime.substring(0, 4));
        int reminderMonth = Integer.parseInt(reminderDateTime.substring(5, 7));
        int reminderDay = Integer.parseInt(reminderDateTime.substring(8, 10));
        int reminderHour = Integer.parseInt(reminderDateTime.substring(11, 13));
        int reminderMin = Integer.parseInt(reminderDateTime.substring(14, 16));

        Calendar currentDate = Calendar.getInstance();
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(reminderYear, reminderMonth - 1, reminderDay, reminderHour, reminderMin);
        if (currentDate.getTimeInMillis() > beginTime.getTimeInMillis())
            return false;
        Long startMillis = beginTime.getTimeInMillis();
        ContentResolver cr = context.getContentResolver();
        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1);
        calEvent.put(CalendarContract.Events.TITLE, reminderFreeText);
        calEvent.put(CalendarContract.Events.DTSTART, startMillis);
        calEvent.put(CalendarContract.Events.DTEND, startMillis + (60 * 60 * 1000));
        calEvent.put(CalendarContract.Events.HAS_ALARM, 1);
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR},
                    MainActivity.WRITE_CALENDAR_REQUEST);
            return false;
        }

        final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, calEvent);

        int dbId = Integer.parseInt(uri.getLastPathSegment());

        //Now create a reminder and attach to the reminder
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, dbId);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 0);

        final Uri reminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

        int added = Integer.parseInt(reminder.getLastPathSegment());
        if(added>0) return true;
        else return false;
    }

    private boolean findDateAndFreeText(ArrayList<ArrayList<Entity>> reminderSentences)
    {
        for(int i=0; i<reminderSentences.size(); i++)
        {
            if(((Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,reminderSentences.get(i)))
            ^ (Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DURATION_ENTITY,reminderSentences.get(i))))
                    && Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i)))
            {
                int dateTimeEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY, reminderSentences.get(i));
                int durationEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.DURATION_ENTITY, reminderSentences.get(i));
                if(dateTimeEntity != -1)
                {
                    dateTime = (String) reminderSentences.get(i).get(dateTimeEntity).getValue();
                }
                else if(durationEntity != -1)
                {
                    int durationValue = (int) reminderSentences.get(i).get(durationEntity).getValue();
                    dateTime = Alarm.durationToDateTime(durationValue);
                }
                int freeTexTEntity = IntentAnalyzerAndRecognizer.containsEntity(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i));
                whatToBeReminded = (String) reminderSentences.get(i).get(freeTexTEntity).getValue();
                if(whatToBeReminded.equals("حاجة") || whatToBeReminded.equals("شئ") || whatToBeReminded.equals("حاجه"))
                    return false;
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(reminderSentences.get(i));
                return true;
            }
        }
        return false;
    }
    private boolean findDate(ArrayList<ArrayList<Entity>> reminderSentences)
    {
        for(int i=0; i<reminderSentences.size(); i++)
        {
            if((Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,reminderSentences.get(i)))
                    ^ (Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DURATION_ENTITY,reminderSentences.get(i))))
            {
                int dateTimeEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY, reminderSentences.get(i));
                int durationEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.DURATION_ENTITY, reminderSentences.get(i));
                if(dateTimeEntity != -1)
                {
                    dateTime = (String) reminderSentences.get(i).get(dateTimeEntity).getValue();
                    winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(reminderSentences.get(i));
                    return true;
                }
                else if(durationEntity != -1)
                {
                    int durationValue = (int) reminderSentences.get(i).get(durationEntity).getValue();
                    dateTime = Alarm.durationToDateTime(durationValue);
                    winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(reminderSentences.get(i));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findFreeText(ArrayList<ArrayList<Entity>> reminderSentences)
    {
        for(int i=0; i<reminderSentences.size(); i++)
        {
            if(Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT,reminderSentences.get(i)))
            {
                int dateTimeEntity = IntentAnalyzerAndRecognizer.containsEntity(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i));
                whatToBeReminded = (String) reminderSentences.get(i).get(dateTimeEntity).getValue();
                winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(reminderSentences.get(i));
                return true;
            }
        }
        return false;
    }
    public static void resetReminder()
    {
        whatToBeReminded = null;
        dateTime = null;
        welcomeBack = false;
    }
}
