package com.teamtreehouse.ribbit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alex on 6/28/2015.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages; //created to store list of retrieved messages

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox,
                container, false);//inflates fragment view. 1. layout id used for framgent 2. container where it's going to be displayed 3. should be false whenever adding view to an activity in code

        return rootView;
    }

    @Override
    public void onResume() { //we want these to show up for the user when the activity is displayed but not just when it's started
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);
        //we want to get messages from parse but only ones with this users id
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES); //need to specify what class we're querying
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        //looks through all ids to see if there's a match with the current one params: (key,key we're looking for)
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT); //lists results by most recent

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null){
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if (getListView().getAdapter() == null) { //checks to see if adapter already exists, so we can just refill it instead of creating a new one every time
                        //getAdapter sees it list view has an adapter
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(),mMessages);
                        setListAdapter(adapter);
                    }else{
                        //needs to be cast as message adapter, b/c get adapter returns basic adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages); //you bet i created this refill method
                    }
                    //adding this clause and not recreating adapter, sends us to the position we were in before clicking on an item
                    //instead of starting back at the top
                }
            }
        });

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);

        Uri fileUri = Uri.parse(file.getUrl()); //converts file url to uri

        if (messageType.equals(ParseConstants.TYPE_IMAGE)){
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class); //getactivity gets context for fragments
            intent.setData(fileUri);
            startActivity(intent);
        }else{
            //view video
           Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
           intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        //delete it! - deleting it from phone, but also need to make sure all recipients have viewed it b4 deleteing it from parse backend
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        if(ids.size() == 1){
            //last recipient, delete from backend
            message.deleteInBackground();
        }else{
            //remove recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId()); //removes the user id locally

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId()); //parse remove all requires a collection of objects so have to create collection of one object

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove); // this removes id from parse
            message.saveInBackground();
        }

    }
}
