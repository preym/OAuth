package com.ehc.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;


public class LoginActivity extends Activity {

  GraphUser currentUser = null;
  Button signupButton;
  Button facebookButton;
  Button emailButton;
  Button signInButton;
  EditText userName;
  EditText password;

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
  }

  private void getWidgets() {
    facebookButton = (Button) findViewById(R.id.facebook);
    emailButton = (Button) findViewById(R.id.email);
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

