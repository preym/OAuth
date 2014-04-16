package com.ehc.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;


public class LoginActivity extends Activity {

  GraphUser currentUser = null;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    Button button = (Button) findViewById(R.id.facebook);
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session session = Session.getActiveSession();
    session.onActivityResult(this, requestCode, resultCode, data);
    getSession();
    Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
    intent.putExtra("user", currentUser.getName());
    Log.d("user", currentUser.getUsername());
    startActivity(intent);
  }

}

