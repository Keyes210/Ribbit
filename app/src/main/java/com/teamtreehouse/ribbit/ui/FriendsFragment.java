package com.teamtreehouse.ribbit.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.utils.ParseConstants;
import com.teamtreehouse.ribbit.R;

import java.util.List;


/**
 * Created by Alex on 6/28/2015.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid,
                container, false);//inflates fragment view. 1. layout id used for framgent 2. container where it's going to be displayed 3. should be false whenever adding view to an activity in code

        mGridView =(GridView)rootView.findViewById(R.id.friendsGrid);
        TextView emptyTextView =(TextView)rootView.findViewById(android.R.id.empty);
        //with gridview this has to be done programmitically, instead of in the xml, like w/ listview
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //get activity is needed b/c this is a fragment
        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            //fragment doesn't extend the activity class, so I need to use ^ to get the appropriate context/updated: used to be getListView()
                            android.R.layout.simple_list_item_1,
                            usernames); // objects, our array of strings*/

                    if(mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    }else{
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends); //have to cast it, doesnt know what kind of adapter we want
                    }

                } else {
                    Log.e(TAG, e.getMessage());
                    if(e.getMessage().equals("java.lang.ClassCastException: java.lang.String cannot be cast to org.json.JSONObject")){
                        //Do nothing since friends are not yet added.
                        //ignore, (case of new user) friends fragment was looking for friends when there were none, and always throwing this error
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.error_label)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }

        });
    }

}
