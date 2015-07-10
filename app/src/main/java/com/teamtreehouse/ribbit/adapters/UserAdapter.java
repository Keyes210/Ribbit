package com.teamtreehouse.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.utils.MD5Util;
import com.teamtreehouse.ribbit.utils.ParseConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 7/3/2015.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {
    protected Context mContext;
    protected List<ParseUser> mUsers;


    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) { //checks to see if convertview exist, so it doesn't create a new one every time
            //inflate convertview from layout file and return it to the list
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView); //fvbid is and activity method, but we can call it from convertview
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView); //fvbid is and activity method, but we can call it from convertview
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel); //fvbid is and activity method, but we can call it from convertview

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();//gets us viewholder that was already created
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if (email.equals("")){
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }else{
            //get gravatar hash based on email, then image based on url, then load image with picasso
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);
        }


        holder.nameLabel.setText(user.getUsername());

        //gridview is the parent view of individual item begin tapped, so we can use this to reference it, parent is passed in as parameter for this method
        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)){ //sees of this item of gridview is checked
            holder.checkImageView.setVisibility(View.VISIBLE); //makes checkmarkvisible if item is checked
        }else{
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder { //used for memory efficiency so scrolling is smooth
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;

    }

    public void refill(List<ParseUser> users) {
        mUsers.clear(); //clear current data
        mUsers.addAll(users); //add new ones
        notifyDataSetChanged();
    }
}
