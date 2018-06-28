package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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
    private ArrayList<ArrayList<Entity>> reminderSentences;
    private static String dateTime = null;
    private static String whatToBeReminded = null;


    public Reminder(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                    ArrayList<ArrayList<Entity>> theWinningSentences){
        analyzerinterface=intentAnalyzerAndRecognizer;
        reminderSentences=theWinningSentences;
        determineTheBestSentenceForReminderSet(theWinningSentences);
    }

    private void determineTheBestSentenceForReminderSet(ArrayList<ArrayList<Entity>> reminderSentences)
    {
        //ArrayList<Entity> selectedSentence = null;
        if(dateTime==null && whatToBeReminded==null){
            //If we have all the data to set a reminder
            for(int i=0; i<reminderSentences.size(); i++)
            {
                if((Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,reminderSentences.get(i)))
                        & Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i)))
                {
                    int dateTimeEntity = IntentAnalyzerAndRecognizer.containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY, reminderSentences.get(i));
                    dateTime = (String) reminderSentences.get(i).get(dateTimeEntity).getValue();
                    int freeTexTEntity = IntentAnalyzerAndRecognizer.containsEntity(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i));
                    whatToBeReminded = (String) reminderSentences.get(i).get(freeTexTEntity).getValue();
                    break;
                }
            }
        }
        if(dateTime==null)
        {
            for(int i=0; i<reminderSentences.size(); i++)
            {
                if(Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,reminderSentences.get(i)))
                {
                    int dateTimeEntity = IntentAnalyzerAndRecognizer.containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY, reminderSentences.get(i));
                    dateTime = (String) reminderSentences.get(i).get(dateTimeEntity).getValue();
                    break;
                }
            }
        }
        if(dateTime==null && whatToBeReminded==null)
        {
            for(int i=0; i<reminderSentences.size(); i++)
            {
                if(Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT,reminderSentences.get(i)))
                {
                    int dateTimeEntity = IntentAnalyzerAndRecognizer.containsEntity(IntentAnalyzerAndRecognizer.REMINDER_FREE_TEXT, reminderSentences.get(i));
                    whatToBeReminded = (String) reminderSentences.get(i).get(dateTimeEntity).getValue();
                    break;
                }
            }
        }

        if(dateTime==null && whatToBeReminded==null)
            analyzerinterface.onReminderRequestingData(true, true);
        else if(dateTime!=null && whatToBeReminded==null)
            analyzerinterface.onReminderRequestingData(false,true);
        else if (dateTime==null && whatToBeReminded!=null)
            analyzerinterface.onReminderRequestingData(true, false);
        else {
            analyzerinterface.onReminderSucceeded(dateTime, whatToBeReminded);
            dateTime = null;
            whatToBeReminded = null;
        }
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

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return false;

        final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, calEvent);

        int dbId = Integer.parseInt(uri.getLastPathSegment());

        //Now create a reminder and attach to the reminder
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, dbId);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 10);

        final Uri reminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

        int added = Integer.parseInt(reminder.getLastPathSegment());
        if(added>0) return true;
        else return false;
    }
}
