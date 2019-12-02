package com.tripmate;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.tripmate.Message;


import androidx.annotation.NonNull;

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(@NonNull Context context, int resource, @NonNull Message[] objects) {
        super(context, resource, objects);
    }
}
