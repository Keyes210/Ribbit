package com.teamtreehouse.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.teamtreehouse.ribbit.utils.ParseConstants;
import com.teamtreehouse.ribbit.R;

import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 7/3/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mMessages;


    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) { //checks to see if convertview exist, so it doesn't create a new one every time
            //inflate convertview from layout file and return it to the list
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon); //fvbid is and activity method, but we can call it from convertview
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel); //fvbid is and activity method, but we can call it from convertview
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();//gets us viewholder that was already created
        }

        ParseObject message = mMessages.get(position);
        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime(); //gets current time
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();

        holder.timeLabel.setText(convertedDate);

        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));


        return convertView;
    }

    private static class ViewHolder { //used for memory efficiency so scrolling is smooth
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear(); //clear current data
        mMessages.addAll(messages); //add new ones
        notifyDataSetChanged();
    }
}
