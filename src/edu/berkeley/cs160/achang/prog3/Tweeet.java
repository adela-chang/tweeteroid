package edu.berkeley.cs160.achang.prog3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.TimeZone;

import android.content.Context;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;


import com.twitterapime.model.MetadataSet;
import com.twitterapime.search.Tweet;

public class Tweeet {

    private Context context;
    public LinearLayout tweeet;
    public ImageView img;
    public String user_name;
    public TextView hr;
    
	final int BLACK = 0xFF000000;
	final int GRAY = 0xFF999999;
	final int LIGHTGRAY = 0xFFEEEEEE;
    
    static Bitmap loadBitmap(String url) {
    	URL newurl;
		try {
			newurl = new URL(url);
			return BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	public Tweeet (Context con, final Tweet tw) {
        this.context = con;
        
        LayoutParams allWrap =
        	new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        allWrap.setMargins(0,0,10,0);
        
    	// overall container for a "tweeet" entry
    	tweeet = new LinearLayout(context); 
    	tweeet.setOrientation(LinearLayout.HORIZONTAL);
    	LayoutParams layoutParams = new LayoutParams 
    		(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    	tweeet.setLayoutParams(layoutParams);
 	
    	// user profile picture
    	img = new ImageView(context);
		img.setImageBitmap(loadBitmap(tw.getUserAccount().getString(MetadataSet.USERACCOUNT_PICTURE_URI)));
    	LayoutParams imgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	imgParams.setMargins(0, 10, 10, 0);
		img.setLayoutParams(imgParams);
		tweeet.addView(img);
    	
    	// layout for text (content) portion
    	LinearLayout c = new LinearLayout(context);
    	c.setOrientation(LinearLayout.VERTICAL);
    	c.setLayoutParams(allWrap);
    	c.setGravity(Gravity.TOP);
    	tweeet.addView(c);
    	
    	// layout for usernames & names (e.g. rowofpalmtrees - Adela Chang)
    	LinearLayout names = new LinearLayout(context);
    	names.setOrientation(LinearLayout.HORIZONTAL);
    	names.setLayoutParams(allWrap);
    	c.addView(names);

    	// username
    	TextView sn = new TextView(context);
    	sn.setLayoutParams(allWrap);
    	sn.setTextColor(BLACK);
    	sn.setTypeface(Typeface.DEFAULT_BOLD);
    	user_name = tw.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
    	sn.setText(user_name);
    	names.addView(sn);
    	sn.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			new ProfileDialog(context, tw).show();
    		} 
    	});
    	
    	// Real name
    	TextView name = new TextView(context);
    	name.setLayoutParams(allWrap);
    	name.setTextColor(GRAY);
    	String n = tw.getUserAccount().getString(MetadataSet.USERACCOUNT_NAME);
    	name.setText(n);
    	names.addView(name);

    	// Text of tweet
    	TextView content = new TextView(context);
    	content.setLayoutParams(allWrap);
    	content.setTextColor(BLACK);
    	String twt = tw.getString(MetadataSet.TWEET_CONTENT);
    	content.setText(twt);
    	c.addView(content);
    	Linkify.addLinks(content, Linkify.WEB_URLS);
    	
    	// Time
    	DateFormat df = DateFormat.getDateTimeInstance();
    	df.setTimeZone(TimeZone.getTimeZone("GMT-16:00"));
    	TextView time = new TextView(context);
    	time.setTextColor(GRAY);
    	String t = tw.getString(MetadataSet.TWEET_PUBLISH_DATE);
    	time.setText(df.format(Long.parseLong(t)));
    	time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
    	c.addView(time);

    	// Line break
    	hr = new TextView(context);
    	LayoutParams hrParams = new LayoutParams(LayoutParams.FILL_PARENT, 1);
    	hrParams.setMargins(0, 10, 0, 10);
    	hr.setBackgroundColor(LIGHTGRAY);
    	hr.setLayoutParams(hrParams);
	}

}
