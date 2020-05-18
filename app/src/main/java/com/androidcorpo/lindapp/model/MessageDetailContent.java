package com.androidcorpo.lindapp.model;

import android.content.Context;
import android.database.Cursor;

import com.androidcorpo.lindapp.LindAppUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDetailContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<MessageDetailItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, MessageDetailItem> ITEM_MAP = new HashMap<String, MessageDetailItem>();

    public static int COUNT = 0;

    static {
        COUNT = 0;
    }


    public static void fillData(Context applicationContext, Cursor messageInboxCursor) {
        ITEMS.clear();
        ITEM_MAP.clear();
        int totalSms = messageInboxCursor.getCount();
        String address;
        String contactName;
        String body;
        String id;
        String date;
        String type;
        int isRorS;
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();

        if (messageInboxCursor.moveToFirst()) {
            for (int i = 0; i < totalSms; i++) {
                id = messageInboxCursor.getString(messageInboxCursor.getColumnIndexOrThrow("_id"));
                String query_address = messageInboxCursor.getString(messageInboxCursor.getColumnIndexOrThrow("address"));
                address = LindAppUtils.getCleanAdress(messageInboxCursor.getString(messageInboxCursor.getColumnIndexOrThrow("address")));
                body = messageInboxCursor.getString(messageInboxCursor.getColumnIndexOrThrow("body"));
                date = messageInboxCursor.getString(messageInboxCursor.getColumnIndexOrThrow("date"));
                type = messageInboxCursor.getString(messageInboxCursor.getColumnIndexOrThrow("type"));
                calendar.setTimeInMillis(Long.parseLong(date));
                contactName = LindAppUtils.getContactName(applicationContext, query_address);
                String datetime = "" + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                        calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
                if (type.contains("1"))
                    isRorS = 1;
                else
                    isRorS = 0;
                try {


                    if (address.length() > 0) {
                        if (ITEM_MAP.containsKey(address)) {
                            MessageDetailItem messages = ITEM_MAP.get(address);
                            Message message = null;

                            try {
                                message = new Message(body, dtFormat.parse(datetime), isRorS);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            messages.messages.add(message);
                            messages.count = messages.messages.size();

                        } else {
                            Message message = null;

                            try {
                                message = new Message(body, dtFormat.parse(datetime), isRorS);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                ArrayList<Message> arrayList = new ArrayList<Message>();
                                arrayList.add(message);
                                MessageDetailItem messages = new MessageDetailItem(address, dtFormat.parse(datetime), body, arrayList,contactName);
                                ITEMS.add(messages);
                                ITEM_MAP.put(address, messages);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageInboxCursor.moveToNext();
            }
        }
    }



    /**
     * A dummy item representing a piece of content.
     */
    public static class MessageDetailItem {

        private String contactName;
        String numberName;
        Date time;
        String latestMessage;
        private ArrayList<Message> messages;
        int count;

        public MessageDetailItem(String numberName, Date time, String latestMessage, ArrayList<Message> messages, String contactName) {
            this.numberName = numberName;
            this.time = time;
            this.latestMessage = latestMessage;
            this.messages = messages;
            count = messages.size();
            this.contactName = contactName;
        }

        public String getNumberName() {
            return numberName;
        }

        public void setNumberName(String numberName) {
            this.numberName = numberName;
        }


        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getLatestMessage() {
            return latestMessage;
        }

        public void setLatestMessage(String latestMessage) {
            this.latestMessage = latestMessage;
        }

        public ArrayList<Message> getMessages() {
            return messages;
        }

        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }
    }


}
