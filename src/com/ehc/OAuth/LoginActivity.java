package com.ehc.OAuth;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import br.com.dina.oauth.github.GithubApp;
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
  GraphUser currentUser = null;
  LoginButton facebookButton;
  Button signOut;
  private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
  private ProgressDialog mConnectionProgressDialog;
  private PlusClient mPlusClient;
  private ConnectionResult mConnectionResult;
  User userDetails;
  private ImageView imageView;
  private GithubApp mApp;
  private ImageView btnConnect;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    initializeGooglePlus();
    initializeGitHub();
    getWidgets();
    setGPlusProcessDialog();
    storeTwitterKeys();

  }

  private void setGPlusProcessDialog() {
    mConnectionProgressDialog = new ProgressDialog(this);
    mConnectionProgressDialog.setMessage("Signing in...");
  }

  private void initializeGitHub() {
    mApp = new GithubApp(this, getKey(R.string.GitHub_CLIENT_ID),
        getKey(R.string.Github_CLIENT_SECRET), getKey(R.string.Github_CALLBACK_URL));
  }

  private void storeTwitterKeys() {
    pref = getPreferences(0);
    SharedPreferences.Editor edit = pref.edit();
    edit.putString("CONSUMER_KEY", getResources().getString(R.string.Twitter_CONSUMER_KEY));
    edit.putString("CONSUMER_SECRET", getResources().getString(R.string.Twitter_CONSUMER_SECRET));
    edit.commit();
  }

  private void logInToGitHub() {
    if (mApp.hasAccessToken()) {
      generateGitHubDialogBox();
    } else {
      mApp.authorize();
    }
  }

  private void generateGitHubDialogBox() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Disconnect from GitHub?").setCancelable(false).setPositiveButton("Yes",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            mApp.resetAccessToken();
          }
        })
        .setNegativeButton("No",
            new DialogInterface.OnClickListener() {
              public void onClick(
                  DialogInterface dialog, int id) {
                dialog.cancel();
              }
            });
    final AlertDialog alert = builder.create();
    alert.show();
  }


  private String getKey(int key) {
    return getResources().getString(key);
  }

  GithubApp.OAuthAuthenticationListener listener = new GithubApp.OAuthAuthenticationListener() {

    @Override
    public void onSuccess() {
      populateUserFromGitHub();
    }

    @Override
    public void onFail(String error) {
      Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
    }
  };

  private void populateUserFromGitHub() {
    userDetails = new User();
    userDetails.setUserName(mApp.getName());
    userDetails.setEmail(mApp.getEmail());
    userDetails.setLocation(mApp.getLocation());
    checkUserExistence();
  }


  public void initializeGooglePlus() {
    mPlusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build();
  }

  private void getWidgets() {
    facebookButton = (LoginButton) findViewById(R.id.facebook);
    facebookButton.setReadPermissions("email");
    signOut = (Button) findViewById(R.id.sign_out);
    signOut.setOnClickListener(this);
    imageView = (ImageView) findViewById(R.id.login_twitter);
    imageView.setOnClickListener(this);
    btnConnect = (ImageView) findViewById(R.id.login_github);
    btnConnect.setOnClickListener(this);
    findViewById(R.id.google_sign_in_button).setOnClickListener(this);
    mApp.setListener(listener);
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
                populateUserFromFB();
                checkUserExistence();
              } else {
              }
            }
          }).executeAsync();
        } else {
        }
      }
    });
  }

  private void populateUserFromFB() {
    userDetails = new User();
    userDetails.setFirstName(currentUser.getFirstName());
    userDetails.setLastName(currentUser.getLastName());
    userDetails.setUserName(currentUser.getUsername());
    userDetails.setEmail((String) currentUser.getProperty("email"));
  }

  private void checkUserExistence() {
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
      callSignUpActivity();
    }
  }

  private void callSignUpActivity() {
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
      checkUserExistence();
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
    checkUserExistence();
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
    if (view.getId() == R.id.login_twitter) {
      replaceLoginFragment();
    }
    if (view.getId() == R.id.login_github) {
      logInToGitHub();
    }
  }

  private void replaceLoginFragment() {
    Fragment login = new LoginFragment();
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.replace(R.id.content_frame, login);
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    ft.addToBackStack(null);
    ft.commit();
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
    Log.d("test", "singout");
    if (mPlusClient.isConnected()) {
      Log.d("test", "singoutif");
      mPlusClient.clearDefaultAccount();
      mPlusClient.disconnect();
      Toast.makeText(getApplicationContext(), "Successfully signOut", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    if (mConnectionProgressDialog.isShowing()) {
      if (result.hasResolution()) {
        try {
          result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
        } catch (IntentSender.SendIntentException e) {
          connectToGoogle();
        }
      }
    }
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
  //  mPlusClient.disconnect();
  }
}

