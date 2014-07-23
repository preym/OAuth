package com.ehc.OAuth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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

  GraphUser currentUser = null;
  LoginButton facebookButton;
  Button emailButton;
  EditText password;
  private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
  private ProgressDialog mConnectionProgressDialog;
  private PlusClient mPlusClient;
  private ConnectionResult mConnectionResult;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    initializeGooglePlus();
    getWidgets();
    mConnectionProgressDialog = new ProgressDialog(this);
    mConnectionProgressDialog.setMessage("Signing in...");
    findViewById(R.id.google_sign_in_button).setOnClickListener(this);
  }

  public void initializeGooglePlus() {
    mPlusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build();
  }

  private void getWidgets() {
    facebookButton = (LoginButton) findViewById(R.id.facebook);
    facebookButton.setReadPermissions("email");
    emailButton = (Button) findViewById(R.id.email);
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
                checkUserExistency(currentUser.getUsername());
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

  private void checkUserExistency(String name) {
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
    String accountName = mPlusClient.getAccountName();
    Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
    if (mPlusClient.isConnected()) {
      Person currentPerson = mPlusClient.getCurrentPerson();
      Log.d("test", "Name: " + currentPerson.getDisplayName());
      Log.d("test", "Gender: " + currentPerson.getGender());
      Log.d("test", "Email: " + accountName);
      Log.d("test", "DateOFBrith: " + currentPerson.getBirthday());
      Log.d("test", "ID: " + currentPerson.getId());
    }
  }

  @Override
  public void onDisconnected() {
    Toast.makeText(this, " Disconnected.", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.google_sign_in_button)
      signInGooglePlus();
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
    connectToGoogle();
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

