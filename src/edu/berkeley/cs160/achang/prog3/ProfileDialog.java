package edu.berkeley.cs160.achang.prog3;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

public class ProfileDialog extends Dialog {
	
	public interface ReadyListener{
		public void ready(ProfileDialog dialog);
	}
	
	private UserAccount currentUser;
	private ImageView profilePic;
	private TextView profileUsername;
	private TextView profileName;
	private TextView profileDesc;
	private TextView numTweets;
	private TextView numFollowers;
	private TextView numFollowees;
	private Button bDone;
	
	public ProfileDialog(Context context, Tweet tw) {
		super(context);
		currentUser = tw.getUserAccount();
	}
	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.profile);
	        
	        setTitle("User Information");
	        profilePic = (ImageView) findViewById(R.id.profileImage);
	        profileUsername = (TextView) findViewById(R.id.profileUsername);
	        profileName = (TextView) findViewById(R.id.profileName);
	        profileDesc = (TextView) findViewById(R.id.profileDescription);
	        numTweets = (TextView) findViewById(R.id.numTweets);
	    	numFollowers = (TextView) findViewById(R.id.numFollowers);
	    	numFollowees = (TextView) findViewById(R.id.numFollowees);
	        bDone = (Button) findViewById(R.id.done);
	    	
	        profilePic.setImageBitmap(Tweeet.loadBitmap(currentUser.getString(MetadataSet.USERACCOUNT_PICTURE_URI)));
	        profileName.setText(currentUser.getString(MetadataSet.USERACCOUNT_NAME));
	        profileUsername.setText("@"+currentUser.getString(MetadataSet.USERACCOUNT_USER_NAME));
	        profileDesc.setText(currentUser.getString(MetadataSet.USERACCOUNT_DESCRIPTION));
	        numTweets.setText(currentUser.getString(MetadataSet.USERACCOUNT_TWEETS_COUNT));
	        numFollowers.setText(currentUser.getString(MetadataSet.USERACCOUNT_FOLLOWERS_COUNT));
	        numFollowees.setText(currentUser.getString(MetadataSet.USERACCOUNT_FRIENDS_COUNT));
	        
	        bDone.setOnClickListener(new OKListener());
		}
	
    private class OKListener implements android.view.View.OnClickListener {
            public void onClick(View v) {
            	dismiss();
            }
    }

}
