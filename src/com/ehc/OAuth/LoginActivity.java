package com.ehc.OAuth;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;


public class LoginActivity extends Activity implements View.OnClickListener,
    GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
  SharedPreferences pref;
  private static String CONSUMER_KEY = "E8AtlUJA1szR5vlyllp3XbQmz";
  private static String CONSUMER_SECRET = "gS5V1ircmdobIoHpELEzBeHYphyI0xLwZIyfTogi4Exz2vaNXt";
  GraphUser currentUser = null;
  LoginButton facebookButton;
  Button emailButton, signOut;
  EditText password;
  private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
  private ProgressDialog mConnectionProgressDialog;
  private PlusClient mPlusClient;
  private ConnectionResult mConnectionResult;
  User userDetails;
  private ImageView imageView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    initializeGooglePlus();
    getWidgets();
    mConnectionProgressDialog = new ProgressDialog(this);
    mConnectionProgressDialog.setMessage("Signing in...");
    findViewById(R.id.google_sign_in_button).setOnClickListener(this);

    pref = getPreferences(0);
    SharedPreferences.Editor edit = pref.edit();
    edit.putString("CONSUMER_KEY", CONSUMER_KEY);
    edit.putString("CONSUMER_SECRET", CONSUMER_SECRET);
    edit.commit();
  }

  public void initializeGooglePlus() {
    mPlusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build();
  }

  private void getWidgets() {
    facebookButton = (LoginButton) findViewById(R.id.facebook);
    facebookButton.setReadPermissions("email");
    emailButton = (Button) findViewById(R.id.email);
    password = (EditText) findViewById(R.id.password);
    signOut = (Button) findViewById(R.id.sign_out);
    signOut.setOnClickListener(this);
    imageView= (ImageView) findViewById(R.id.login_twitter);
    imageView.setOnClickListener(this);
  }

  private void getSession() {
    Session.openActiveSession(this, true, new Session.StatusCallback() {
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        if (!session.isOpened())
          session = new Session(getApplicationContext());
        if (session.isOpened()) {
          Request.newMeRequest(session, new Request.GraphUserCallback() {
            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
                currentUser = user;
                populateUserFromfb();
                checkUserExistency();
              } else {
              }
            }
          }).executeAsync();
        } else {
        }

      }
    });
  }

  private void populateUserFromfb() {
    userDetails = new User();
    userDetails.setFirstName(currentUser.getFirstName());
    userDetails.setLastName(currentUser.getLastName());
    userDetails.setUserName(currentUser.getUsername());
    userDetails.setEmail((String) currentUser.getProperty("email"));
  }

  private void checkUserExistency() {

    String name = "";
    if (userDetails != null)
      name = userDetails.getUserName();
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase database = dbHelper.getReadableDatabase();
    Cursor cursor = database.rawQuery("select * from user where USERNAME='"
        + name + "'", null);
    if (cursor != null && cursor.getCount() > 0) {
      startDashboard();
    } else {
      startSignupActivity();
    }

  }

  private void startSignupActivity() {
    Intent intent = new Intent(this, SignUpActivity.class);
    userDetails.setLocation("Hyderabad");
    intent.putExtra("user", userDetails);
    startActivity(intent);
  }

  private void startDashboard() {
    Intent intent = new Intent(this, DashboardActivity.class);
    intent.putExtra("userName", userDetails.getUserName());
    startActivity(intent);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
      mConnectionResult = null;
      connectToGoogle();
    } else {
      Session session = Session.getActiveSession();
      session.onActivityResult(this, requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
        getSession();
      }
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    mConnectionProgressDialog.cancel();
    if (mPlusClient.isConnected()) {
      populateUserFromGPlus();
      checkUserExistency();
    }
  }

  private void populateUserFromGPlus() {
    Person currentPerson = mPlusClient.getCurrentPerson();
    String accountName = mPlusClient.getAccountName();
    Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
    userDetails = new User();
    userDetails.setEmail(accountName);
    userDetails.setUserName(currentPerson.getDisplayName());
    userDetails.setFirstName(currentPerson.getName().getGivenName());
    userDetails.setLastName(currentPerson.getName().getFamilyName());
  }

  public void populateUserFromTwitter(twitter4j.User user) {
    userDetails = new User();
    userDetails.setUserName(user.getName());
    checkUserExistency();
  }

  @Override
  public void onDisconnected() {
    Toast.makeText(this, " Disconnected.", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.sign_out) {
      signOutGooglePlus();
    }
    if (view.getId() == R.id.google_sign_in_button) {
      connectToGoogle();
      signInGooglePlus();
    }
    if(view.getId()==R.id.login_twitter){
      Fragment login = new LoginFragment();
      FragmentTransaction ft = getFragmentManager().beginTransaction();
      ft.replace(R.id.content_frame, login);
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
      ft.addToBackStack(null);
      ft.commit();
    }
  }

  public void signInGooglePlus() {
    if (!mPlusClient.isConnected()) {
      if (mConnectionResult == null) {
        mConnectionProgressDialog.show();
        connectToGoogle();
      } else {
        try {
          mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
        } catch (IntentSender.SendIntentException e) {
          mConnectionResult = null;
          connectToGoogle();
        }
      }
    }
  }

  public void signOutGooglePlus() {
    if (mPlusClient.isConnected()) {
      mPlusClient.clearDefaultAccount();
      mPlusClient.disconnect();
      Toast.makeText(getApplicationContext(), "Successfully signOut", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    if (mConnectionProgressDialog.isShowing()) {
      // The user clicked the sign-in button already. Start to resolve
      // connection errors. Wait until onConnected() to dismiss the
      // connection dialog.
      if (result.hasResolution()) {
        try {
          result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
        } catch (IntentSender.SendIntentException e) {
          connectToGoogle();
        }
      }
    }
    // Save the result and resolve the connection failure upon a user click.
    mConnectionResult = result;

  }

  @Override
  protected void onStart() {
    super.onStart();
    // connectToGoogle();
  }

  public void connectToGoogle() {
    mPlusClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mPlusClient.disconnect();
  }
}

