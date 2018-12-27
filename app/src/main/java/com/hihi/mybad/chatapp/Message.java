package com.hihi.mybad.chatapp;

import java.io.Serializable;
/**
 * Created by Long on 20/4/2017.
 */

public class Message implements Serializable{
    private String mess;
    private String date;
    private String sender;

    public Message() {
    }

    public Message(String mess, String date, String sender) {
        this.mess = mess;
        this.date = date;
        this.sender = sender;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
