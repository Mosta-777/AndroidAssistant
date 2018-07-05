package com.example.mostafa.myapplication.POJOS;

/**
 * Created by Mostafa on 7/4/2018.
 */

public class Message {
    private String messageBody;
    private boolean fromUser;

    public Message(String messageBody, boolean fromUser){
        this.messageBody = messageBody;
        this.fromUser = fromUser ;
    }
    public String getMessageBody() {
        return messageBody;
    }
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
    public boolean isFromUser() {
        return fromUser;
    }
    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }
}
