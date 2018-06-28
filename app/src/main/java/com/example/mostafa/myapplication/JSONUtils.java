package com.example.mostafa.myapplication;

import android.util.Log;

import com.example.mostafa.myapplication.POJOS.Entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mostafa on 2/15/2018.
 */

public class JSONUtils {


    private static final String ENTITY_KEY="entities";
    private static final String TEXT_KEY="_text";
    public static final String ENTITY_INTENT_KEY="intent";
    private static final String ENTITY_NUMBER_KEY="number";
    private static final String ATTR_CONFIDENCE_KEY="confidence";
    private static final String ATTR_VALUE_KEY="value";


    public static ArrayList<Entity> getEntitesFromJSONResponse(String witResponse)
            throws JSONException {
        JSONObject responseJSONObject=new JSONObject(witResponse.trim());
        String originalText = responseJSONObject.getString(TEXT_KEY);
        JSONObject entitiesJSONObject=responseJSONObject.getJSONObject(ENTITY_KEY);
        ArrayList<Entity> returnedArrayList = new ArrayList<>();
        returnedArrayList.add(new Entity("text", 1, originalText));
        Iterator<?> keys=entitiesJSONObject.keys();
        while (keys.hasNext()){
            //The order of the entity arrays doesn't matter
            String entityName=(String)keys.next();
            if (entitiesJSONObject.get(entityName) instanceof JSONArray){
                JSONArray entityArray=entitiesJSONObject.getJSONArray(entityName);
                for (int i=0;i<entityArray.length();i++){
                    //Now here the order of the entities in each entity array does matter
                  JSONObject currentEntity=entityArray.getJSONObject(i);
                  returnedArrayList.add(new Entity(entityName,
                          currentEntity.getDouble(ATTR_CONFIDENCE_KEY),currentEntity.get(ATTR_VALUE_KEY)));
                }
            }
        }
        return returnedArrayList;
    }


}
