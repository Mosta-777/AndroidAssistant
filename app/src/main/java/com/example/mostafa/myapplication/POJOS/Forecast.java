package com.example.mostafa.myapplication.POJOS;

/**
 * Created by Mahmoud Salah on 7/4/2018.
 */

public class Forecast {

    private String date;
    private String day;
    private Long highTemp;
    private Long lowTemp;
    private String condition;

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public Long getHighTemp() {
        return highTemp;
    }

    public Long getLowTemp() {
        return lowTemp;
    }

    public String getCondition() {
        return condition;
    }

    public Forecast(String newDate, String newDay, String newlowTemp, String newHighTemp, String newCondition)
    {
        date = newDate;
        day = newDay;
        condition = newCondition;
        lowTemp = Math.round((Integer.parseInt(newlowTemp) - 32) * (5.0/9));
        highTemp = Math.round((Integer.parseInt(newHighTemp) - 32) * (5.0/9));
    }


}
