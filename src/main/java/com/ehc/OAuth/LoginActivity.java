package com.ehc.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
//import com.parse.LogInCallback;
//import com.parse.ParseException;
//import com.parse.ParseTwitterUtils;
//import com.parse.ParseUser;



public class LoginActivity extends Activity {

  GraphUser currentUser = null;
  Button signupButton;
  LoginButton facebookButton;
  Button twitterBUtton;
  Button signInButton;
  EditText userName;
  EditText password;
//  AndroidTwitterLogin twitterLogin;

  static String TWITTER_CONSUMER_KEY = "qRnlWlsXXJevWnb1zB8gyPuYU";
  static String TWITTER_CONSUMER_SECRET = "qWg4YActOiDqIiaN7sLNeLQb8Dpa2FMDWLdgu78suRKlKFceBF";


  // Preference Constants
  static String PREFERENCE_NAME = "twitter_oauth";
  static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
  static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
  static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

  static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

  // Twitter oauth urls
  static final String URL_TWITTER_AUTH = "auth_url";
  static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
  static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";


//  private static Twitter twitter;
//  private static RequestToken requestToken;

  // Shared Preferences
  private static SharedPreferences mSharedPreferences;

  // Internet Connection detector
//  private ConnectionDetector cd;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    getWidgets();
    applyAction();
  }

  private void applyAction() {
    signupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
        startActivity(intent);
      }
    });


    twitterBUtton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        ParseTwitterUtils.logIn(getBaseContext(), new LogInCallback() {
//          @Override
//          public void done(ParseUser user, ParseException err) {
//            if (user == null) {
//              Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
//            } else if (user.isNew()) {
//              Log.d("MyApp", "User signed up and logged in through Twitter!");
//            } else {
//              Log.d("MyApp", "User logged in through Twitter!");
//            }
//          }
//        });
//        loginToTwitter();
      }
    });
  }


//  private void loginToTwitter() {
//    // Check if already logged in
//    if (!isTwitterLoggedInAlready()) {
//      ConfigurationBuilder builder = new ConfigurationBuilder();
//      builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
//      builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
//      Configuration configuration = builder.build();
//
//      TwitterFactory factory = new TwitterFactory(configuration);
//      twitter = factory.getInstance();
//
//      try {
//        requestToken = twitter
//            .getOAuthRequestToken(TWITTER_CALLBACK_URL);
//        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
//            .parse(requestToken.getAuthenticationURL())));
//      } catch (TwitterException e) {
//        e.printStackTrace();
//      }
//    } else {
//      // user already logged into twitter
//      Toast.makeText(getApplicationContext(),
//          "Already Logged into twitter", Toast.LENGTH_LONG).show();
//    }
//  }

  /**
   * Check user already logged in your application using twitter Login flag is
   * fetched from Shared Preferences
   */
  private boolean isTwitterLoggedInAlready() {
    // return twitter login status from Shared Preferences
    return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
  }


  private void getWidgets() {
    facebookButton = (LoginButton) findViewById(R.id.facebook);
    facebookButton.setReadPermissions("email");
    twitterBUtton = (Button) findViewById(R.id.email);
    signupButton = (Button) findViewById(R.id.signup);
    signInButton = (Button) findViewById(R.id.logIn);
    userName = (EditText) findViewById(R.id.username);
    password = (EditText) findViewById(R.id.password);
  }

  private void getSession() {
    Session.openActiveSession(this, true, new Session.StatusCallback() {
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        if (!session.isOpened())
          session = new Session(getApplicationContext());
        if (session.isOpened()) {
          Log.d("test:", "session opened");
          Request.newMeRequest(session, new Request.GraphUserCallback() {
            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
                Log.d("test:", "user not null");
                currentUser = user;
                Log.d("email", (String) user.getProperty("email"));
                checkUserExistency();
              } else {
                Log.d("test:", "user null");
              }
            }
          }).executeAsync();
        } else {
          Log.d("test:", "session not opened");
        }

      }
    });
  }

  private void checkUserExistency() {
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase database = dbHelper.getReadableDatabase();
    Cursor cursor = database.rawQuery("select * from user where USERNAME='"
        + currentUser.getUsername() + "'", null);
    if (cursor != null && cursor.getCount() > 0) {
      startDashboard();
    } else {
      startSignupActivity();
    }

  }

  private void startSignupActivity() {
    Intent intent = new Intent(this, SignUpActivity.class);
    User user = new User();
    user.setFirstName(currentUser.getFirstName());
    user.setLastName(currentUser.getLastName());
    user.setUserName(currentUser.getUsername());
    user.setEmail((String) currentUser.getProperty("email"));
    user.setLocation("Hyderabad");
    intent.putExtra("user", user);
    startActivity(intent);
  }

  private void startDashboard() {
    Intent intent = new Intent(this, DashboardActivity.class);
    intent.putExtra("userName", currentUser.getUsername());
    startActivity(intent);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session session = Session.getActiveSession();
    session.onActivityResult(this, requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      getSession();
    }
  }

}

