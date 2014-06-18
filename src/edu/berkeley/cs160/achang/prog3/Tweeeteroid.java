package edu.berkeley.cs160.achang.prog3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.twitterapime.rest.Credential;
import com.twitterapime.rest.Timeline;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDeviceListener;
import com.twitterapime.search.Tweet;
import com.twitterapime.xauth.Token;

public class Tweeeteroid extends Activity {
	
	private static final int HOME = 0;
	private static final int MENTIONS = 1; 
	
	private EditText ePost;
	private TextView tChars, tLoad, tHome, tMentions;
	private Button bTweet, bMore;
	private LinearLayout llScroll;
	private RelativeLayout fHome, fMentions;
	private ImageView iRefresh;
	private ProgressBar pRefreshAnimated;
	
	private UserAccountManager m;
	private ArrayList<Tweeet> tweetList = new ArrayList<Tweeet>();
	private Tweeeteroid context;
	private boolean loadInProgress = false;
	private int state, numTweetsToDisplay = 10, increment = 5, prevGetMore = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = this;
        state = HOME;
        
        ePost = (EditText) findViewById(R.id.post);
        tChars = (TextView) findViewById(R.id.charCount);
        bTweet = (Button) findViewById(R.id.bTweet);
        llScroll = (LinearLayout) findViewById(R.id.scroll);
        tLoad = (TextView) findViewById(R.id.load);
        tHome = (TextView) findViewById(R.id.mTimeline);
        tMentions = (TextView) findViewById(R.id.mentions);
        iRefresh = (ImageView) findViewById(R.id.refreshPic);
        pRefreshAnimated = (ProgressBar) findViewById(R.id.refreshAnimation);

        fHome = (RelativeLayout) findViewById(R.id.frameHome);
        fMentions = (RelativeLayout) findViewById(R.id.frameMentions);
        
        bMore = new Button(this);
        LayoutParams bp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        bp.setMargins(10, 10, 10, 10);
        bMore.setLayoutParams(bp);
        bMore.setText("Get more tweets");
        bMore.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		bMore.setText("Loading...");
        		numTweetsToDisplay += increment;

        		if (state == HOME) {
        			bMore.invalidate();
        			getTweets();
        		} else if (state == MENTIONS) {
        			bMore.invalidate();
        			getMentions();
        		}
        	}
        });
        
        Token token = new Token("260542700-neUzUXWVhUhVGlzTQwKOvj3AgyMi5HYFcOJoaZJ7", "lcvwJvG44uaSIMKK7blXQqEUlRYpyuZlRAHt5DqGaA");
        Credential c = new Credential("rowofpalmtrees", "qmQrOr9b7YCPRg2JK3YbQ", "yK65h20FlntjQtdSOdtuLIQZTnm62XizqiF90JEdCNI", token);
        m = UserAccountManager.getInstance(c);
        
        refresh();
        
        bTweet.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		postTweet();
        	}
        });

        iRefresh.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		refreshAnimation();
        	}
        }); 
        
		ePost.addTextChangedListener(new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	           update();
	        }
	        public void afterTextChanged(Editable s) {}
		}); 
		
		ePost.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				vanish();
			}
		});
		
		tHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!loadInProgress) {
					switchHome();
				}
			}		
		});
		
		tMentions.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!loadInProgress) {
					switchMentions();
				}
			}		
		});

        
    }
    
    void reset() {
    	prevGetMore = 0;
    	numTweetsToDisplay = 10;
    }
    
    private void getTweets() {
    	tLoad.setText("loading tweets...");
		try {
			if (m.verifyCredential()) {
				Timeline tml = Timeline.getInstance(m);
				Query q = QueryComposer.count(numTweetsToDisplay);
				tweetList.clear();
				iRefresh.setVisibility(View.INVISIBLE);
				pRefreshAnimated.setVisibility(View.VISIBLE);
				tml.startGetHomeTweets(q, new SearchDeviceListener() {
					public void tweetFound(Tweet tweet) {
						tweetList.add(new Tweeet(context, tweet));
					}
					
					public void searchCompleted() {
						context.runOnUiThread(new Runnable() {
							public void run() {
								llScroll.removeAllViews();
								for (int i = 0; i<tweetList.size(); i++) {
									llScroll.addView(tweetList.get(i).tweeet);
									llScroll.addView(tweetList.get(i).hr);
								}
								if (tweetList.size() != prevGetMore || numTweetsToDisplay == 10) {
									bMore.setText("Get more tweets");
									llScroll.addView(bMore);
									prevGetMore = tweetList.size();
								}
						    	pRefreshAnimated.setVisibility(View.INVISIBLE);
						    	iRefresh.setVisibility(View.VISIBLE);
						    	loadInProgress = false;
							}
						});
					}

					public void searchFailed(Throwable arg0) {
						context.runOnUiThread(new Runnable() {
							public void run() {
						    	loadInProgress = false;
								tLoad.setText("SEARCH FAILED");
							}
						});
					}
				});
			} else {
				tLoad.setText("UNABLE TO VERIFY CREDENTIALS");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LimitExceededException e) {
			e.printStackTrace();
		}
    }
    
    private void getMentions() {
    	tLoad.setText("loading mentions...");
		try {
			if (m.verifyCredential()) {
				Timeline tml = Timeline.getInstance(m);
				Query q = QueryComposer.count(numTweetsToDisplay);
				tweetList.clear();
				iRefresh.setVisibility(View.INVISIBLE);
				pRefreshAnimated.setVisibility(View.VISIBLE);
				tml.startGetMentions(q, new SearchDeviceListener() {
					public void tweetFound(Tweet tweet) {
						tweetList.add(new Tweeet(context, tweet));
					}
					
					public void searchCompleted() {
						context.runOnUiThread(new Runnable() {
							public void run() {
								llScroll.removeAllViews();
								for (int i = 0; i<tweetList.size(); i++) {
									llScroll.addView(tweetList.get(i).tweeet);
									llScroll.addView(tweetList.get(i).hr);
								}
								if (tweetList.size() != prevGetMore || numTweetsToDisplay == 10) {
									bMore.setText("Get more tweets");
									llScroll.addView(bMore);
									prevGetMore = tweetList.size();
								}
						    	pRefreshAnimated.setVisibility(View.INVISIBLE);
						    	iRefresh.setVisibility(View.VISIBLE);
						    	loadInProgress = false;
							}
						});
					}

					public void searchFailed(Throwable arg0) {
						context.runOnUiThread(new Runnable() {
							public void run() {
						    	loadInProgress = false;
								tLoad.setText("SEARCH FAILED");
							}
						});
					}
				});
			} else {
				tLoad.setText("UNABLE TO VERIFY CREDENTIALS");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LimitExceededException e) {
			e.printStackTrace();
		}
    }
    
    void postTweet() {
    	try {
    		if (m.verifyCredential()) {
    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    			imm.hideSoftInputFromWindow(ePost.getWindowToken(), 0);
    			Tweet t = new Tweet(ePost.getText().toString());
    			TweetER ter = TweetER.getInstance(m);
    			t = ter.post(t);
    			ePost.setText("");
    			if (state == HOME) { refresh(); }
    			else if (state == MENTIONS) { switchHome(); }
    		}
    	} catch (IOException e) {e.printStackTrace();
    	} catch (LimitExceededException e) {}    	
    }
 
    void refreshAnimation() {
    	iRefresh.setBackgroundColor(0xFFEEEEEE);
        final Handler handler = new Handler(); 
        Timer t = new Timer(); 
        t.schedule(new TimerTask() { 
                public void run() { 
                        handler.post(new Runnable() { 
                                public void run() { 
                                	refresh();
                                } 
                        }); 
                } 
        }, 200); 
        iRefresh.setBackgroundColor(0xFFF8F8F8);
    }
    
    void refresh() {
    	if (!loadInProgress) {
    		reset();
    		loadInProgress = true;
    		llScroll.removeAllViews();
    		tLoad = new TextView(this);
    		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		lp.setMargins(10, 0, 10, 0);
    		tLoad.setLayoutParams(lp);
    		llScroll.addView(tLoad);
    		if (state == HOME) {
    			tLoad.setText("loading tweets...");
    			getTweets();
    		} else if (state == MENTIONS) {
    			tLoad.setText("loading mentions...");
    			getMentions();
    		} else {
    			loadInProgress = false;
    			tLoad.setText("Unable to refresh. Please contact systems administrator.");
    		}
    	}
    }
    
    void vanish() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ePost.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    	if (ePost.getCurrentTextColor() == 0xff999999) {
    		ePost.getText().clear();
    		ePost.setTextColor(0xff000000);
    		ePost.setCursorVisible(true);
    		ePost.setFocusableInTouchMode(true);
    	}
    }
    
    void switchHome() {
    	if (state == HOME) {}
    	else {
    		state = HOME;
            refresh();
    		tHome.setBackgroundColor(0xFFEEEEEE);
            final Handler handler = new Handler(); 
            Timer t = new Timer(); 
            t.schedule(new TimerTask() { 
                    public void run() { 
                            handler.post(new Runnable() { 
                                    public void run() { 
                                		fMentions.setVisibility(View.INVISIBLE);
                                		fHome.setVisibility(View.VISIBLE);
                                		tHome.setBackgroundColor(0xFFF8F8F8);
                                    } 
                            }); 
                    } 
            }, 200); 
    	}
    }
    
    void switchMentions() {
    	if (state == MENTIONS) {}
    	else {
    		state = MENTIONS;
            refresh();
    		tMentions.setBackgroundColor(0xFFEEEEEE);
            final Handler handler = new Handler(); 
            Timer t = new Timer(); 
            t.schedule(new TimerTask() { 
                    public void run() { 
                            handler.post(new Runnable() { 
                                    public void run() { 
                                		fHome.setVisibility(View.INVISIBLE);
                                		fMentions.setVisibility(View.VISIBLE);
                                		tMentions.setBackgroundColor(0xFFF8F8F8);
                                    } 
                            }); 
                    } 
            }, 200); 
    	}
    }
    
    void update() {
    int length = ePost.getText().toString().length();
    length = 140-length;
    tChars.setText(String.valueOf(length));
    }
}

