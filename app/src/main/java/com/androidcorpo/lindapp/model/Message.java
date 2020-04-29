package com.androidcorpo.lindapp.model;

import java.util.Date;

/**
 * Created by severin MBEKOU on 17-04-2020.
 */
public class Message {
    private String message;
    private Date time;
    private int isRorS;

    public Message(String message, Date time, int isRorS) {
        this.message = message;
        this.isRorS = isRorS;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getIsRorS() {
        return isRorS;
    }

    public void setIsRorS(int isRorS) {
        this.isRorS = isRorS;
    }
}
