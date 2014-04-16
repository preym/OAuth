package com.ehc.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 14/4/14
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DashboardActivity extends Activity {

  private GraphUser currentUser;
  TextView textView;

  public void onCreate(Bundle savedInstanceState) {
    Log.d("test:", "onCreate start");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dashboard);
    textView = (TextView) findViewById(R.id.textView);
//    getSession();
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
                textView.setText(user.getName() + "\n" + user.getFirstName() + "\n" + user.getLastName()
                    + "\n" + user.getMiddleName() + "\n" + user.getBirthday() + "\n" +
                    user.getId() + "\n" + user.getLink()
                    + "\n" + user.getLocation() + "\n" + user.getUsername());
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
}

