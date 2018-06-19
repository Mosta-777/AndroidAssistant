package com.example.mostafa.myapplication.POJOS;


/**
 * Created by Mostafa on 2/17/2018.
 */

public class Entity {
    private String name;
    private double confidence;
    private Object value;

    public Entity(String name,double confidence,Object value){
        this.name=name;
        this.confidence=confidence;
        this.value=value;
    }

    public String getName(){
        return this.name;
    }

    public double getConfidence(){
        return this.confidence;
    }

    public  Object getValue(){
        return this.value;
    }


}
