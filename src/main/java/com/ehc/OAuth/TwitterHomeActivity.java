package com.ehc.OAuth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 22/4/14
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterHomeActivity extends Activity {
  static String TWITTER_CONSUMER_KEY = "HwNoqP04wIfesPWCtOI5wUv2u";
  static String TWITTER_CONSUMER_SECRET = "iNT7l2YZiM7IlWdOgt7TjysUgvf6KX37lnoYuse0NXJ1QOM0wd";
  static String PREFERENCE_NAME = "twitter_oauth";
  static final String PREF_KEY_OAUTH_TOKEN = "1930618592-gfRoQfNkadSY265pvKsxquncVNAwpd0fRTghWdK";
  static final String PREF_KEY_OAUTH_SECRET = "oSSBA1TelBAUX3K2MtY0n4wzdtpG5XEpHPBsg4v1Sw1gf";
  static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
  static final String TWITTER_CALLBACK_URL = "http://eggheadcreative.com/";
  static final String URL_TWITTER_AUTH = "https://api.twitter.com/oauth/authorize";
  static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
  static final String URL_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/access_token";
  ProgressDialog pDialog;
  private static Twitter twitter;
  private static RequestToken requestToken;
  private static SharedPreferences mSharedPreferences;
  private ConnectionDetector cd;
  AlertDialogManager alert = new AlertDialogManager();
  EditText sts;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_twitter_home);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }
    cd = new ConnectionDetector(getApplicationContext());
    if (!cd.isConnectingToInternet()) {
      alert.showAlertDialog(TwitterHomeActivity.this, "Internet Connection Error",
          "Please connect to working Internet connection", false);
      return;
    }
// Check if twitter keys are set
    if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
      alert.showAlertDialog(TwitterHomeActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
      return;
    }
    mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

    findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        loginToTwitter();
      }
    });
    findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sts = (EditText) findViewById(R.id.editTxt);
        String status = sts.getText().toString();
        if (status.trim().length() > 0) {
          new updateTwitterStatus().execute(status);
        } else {
          Toast.makeText(getApplicationContext(),
              "Please enter status message", Toast.LENGTH_SHORT).show();
        }
      }
    });
    if (!isTwitterLoggedInAlready()) {
      final String verifier;
      Uri uri = getIntent().getData();
      if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
        verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
        try {
          System.out.println("after login");
          AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
// Shared Preferences
          SharedPreferences.Editor e = mSharedPreferences.edit();
          e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
          e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
          e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
          e.commit();
          Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
          findViewById(R.id.button1).setVisibility(View.GONE);
          findViewById(R.id.editTxt).setVisibility(View.VISIBLE);
          findViewById(R.id.button2).setVisibility(View.VISIBLE);
          long userID = accessToken.getUserId();
          User user = (User) twitter.showUser(userID);
          String username = user.getName();
          Log.e("UserID: ", "userID: " + userID + "" + username);
          Log.v("Welcome:", "Thanks:" + Html.fromHtml("<b>Welcome " + username + "</b>"));
        } catch (Exception e) {
          Toast.makeText(TwitterHomeActivity.this, e.getMessage(), 1000).show();
          Log.e("Twitter Login Error", "> " + e.getMessage());
          e.printStackTrace();
        }

      }
    }
  }

  private void loginToTwitter() {
    if (!isTwitterLoggedInAlready()) {
      new Thread() {
        @Override
        public void run() {
// TODO Auto-generated method stub
          super.run();
          ConfigurationBuilder builder = new ConfigurationBuilder();
          builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
          builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
          Configuration configuration = builder.build();
          TwitterFactory factory = new TwitterFactory(configuration);
          twitter = factory.getInstance();
          try {
            requestToken = twitter.getOAuthRequestToken(PREF_KEY_OAUTH_TOKEN);
//            Toast.makeText(getApplicationContext(),
//                " Logged into twitter", Toast.LENGTH_LONG).show();
            TwitterHomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(requestToken.getAuthenticationURL())));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }.start();
    } else {
      Toast.makeText(getApplicationContext(),
          "Already Logged into twitter", Toast.LENGTH_LONG).show();
    }
  }

  private boolean isTwitterLoggedInAlready() {
    return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
  }

  class updateTwitterStatus extends AsyncTask<String, String, String> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(TwitterHomeActivity.this);
      pDialog.setMessage("Updating to twitter...");
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.show();
    }

    protected String doInBackground(String... args) {
      Log.d("Tweet Text", "> " + args[0]);
      String status = args[0];
      try {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
// Access Token
        String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
// Access Token Secret
        String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
        AccessToken accessToken = new AccessToken(access_token, access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
// Update status
        twitter4j.Status response = twitter.updateStatus(status);
        Log.d("Status", "> " + response.getText());
      } catch (TwitterException e) {
// Error in updating status
        Log.d("Twitter Update Error", e.getMessage());
      }
      return null;
    }

    protected void onPostExecute(String file_url) {
// dismiss the dialog after getting all products
      pDialog.dismiss();
// updating UI from Background Thread
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(getApplicationContext(),
              "Status tweeted successfully", Toast.LENGTH_SHORT)
              .show();
// Clearing EditText field
          sts.setText("");
        }
      });
    }
  }
}
