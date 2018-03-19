package com.example.fir_sqlite;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<FirebaseMessage> {
    private static final String TAG = "*** " + MessageAdapter.class.getSimpleName();

    public MessageAdapter(Context context, int resource, List<FirebaseMessage> objects) {
        super(context, resource, objects);
        Log.d(TAG, "<<constructor>> MessageAdapter()");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        ImageView imageImageView = (ImageView) convertView.findViewById(R.id.imageImageView);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        FirebaseMessage message = getItem(position);

        boolean isImage = message.getImageUrl() != null;
        if (isImage) {
            messageTextView.setVisibility(View.GONE);
            imageImageView.setVisibility(View.VISIBLE);
            // when an image is received in the first place it is
            // a spinner, display it as a small image
            Glide.with(imageImageView.getContext())
                    .load(message.getImageUrl())
                    // resizes the image to 20x20 pixels
                    //.override(20, 20)
                    .into(imageImageView);
            Log.d(TAG, message.getImageUrl());
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            imageImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());

            Log.d(TAG, message.getText());
        }
        if (message.getName() != null) {
            authorTextView.setText(message.getName());

            Log.d(TAG, message.getName());
        }

        return convertView;
    }
}