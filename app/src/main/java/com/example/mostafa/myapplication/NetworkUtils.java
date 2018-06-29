package com.example.mostafa.myapplication;

import android.support.annotation.NonNull;

import com.example.mostafa.myapplication.BasicAndroidFunctionalities.Alarm;
import com.example.mostafa.myapplication.POJOS.Entity;
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

/**
 * Created by Mostafa on 2/19/2018.
 */

public class NetworkUtils implements CommunicationInterfaces.AnalyzerNetworkUtilsInterface{
    private static final String baseURL="https://api.wit.ai/";
    private static final String token="EDM7POFMZLZ6H2OB253HNBAVYPBKW2RC";
    private static Retrofit.Builder builder=new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit=builder.build();
    private static UserClient userClient=retrofit.create(UserClient.class);
    /*private IntentAnalyzerAndRecognizer intentAnalyzerAndRecognizer=new
            IntentAnalyzerAndRecognizer(this);*/
    private CommunicationInterfaces.AnalyzerNetworkUtilsInterface
            analyzerNetworkUtilsInterface;

    public NetworkUtils(CommunicationInterfaces.AnalyzerNetworkUtilsInterface analyzerNetworkUtilsInterface){
            this.analyzerNetworkUtilsInterface=analyzerNetworkUtilsInterface;
            new IntentAnalyzerAndRecognizer(this);
    }

    @Override
    public void toNetworkUtils(String message) {
        Call<ResponseBody> call=userClient.getSecret("https://api.wit.ai/message?v=19/6/2018&q="+message,
                " Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        ArrayList<Entity> receivedEntities=JSONUtils.getEntitesFromJSONResponse(response.body().string());
                        analyzerNetworkUtilsInterface.toAnalyzer(receivedEntities);
                        //IntentAnalyzerAndRecognizer.handleFetchedEntities(receivedEntities);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }else {
                        analyzerNetworkUtilsInterface.toAnalyzerFailedResponse("Response was not successful");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                analyzerNetworkUtilsInterface.toAnalyzerFailedResponse("No response ," +
                        " probably a bad network connection , try again please .");
            }
        });
    }
    @Override public void toAnalyzer(ArrayList<Entity> entities) {}
    @Override public void toAnalyzerFailedResponse(String failingMessage) {}
}
