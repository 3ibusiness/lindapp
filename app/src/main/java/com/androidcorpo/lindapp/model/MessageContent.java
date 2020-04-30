package com.androidcorpo.lindapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by severin MBEKOU on 17-04-2020.
 */
public class MessageContent {

    public static final List<MessageItem> ITEMS = new ArrayList<MessageItem>();
    public static final Map<Date, MessageItem> ITEM_MAP = new HashMap<Date, MessageItem>();

    static {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static void fillData(MessageDetailContent.MessageDetailItem dc) {
        ITEMS.clear();
        ITEM_MAP.clear();
        ArrayList<Message> messages = dc.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            MessageItem messageItem = new MessageItem(message.getMessage(), message.getTime(), message.getIsRorS());

            ITEMS.add(messageItem);
            ITEM_MAP.put(message.getTime(), messageItem);
        }

        Collections.reverse(ITEMS);
    }

    public static class MessageItem implements Comparable {
        String message;
        Date time;
        int isRorS;

        public MessageItem(String message, Date time, int isRorS) {
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

        @Override
        public int compareTo(Object another) {
            Date date = ((MessageItem) another).getTime();
            return this.time.compareTo(getTime());
        }
    }
}
