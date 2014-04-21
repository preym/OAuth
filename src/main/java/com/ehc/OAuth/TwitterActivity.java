package com.ehc.OAuth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;


public class TwitterActivity extends Activity {
  Button twitter;
  EditText edit;
  SocialAuthAdapter adapter;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.twitter);
    adapter = new SocialAuthAdapter(new ResponseListener());
    twitter = (Button) findViewById(R.id.twitter);
    edit = (EditText) findViewById(R.id.editTxt);
    twitter.setBackgroundResource(R.drawable.twitter);
    twitter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        adapter.authorize(TwitterActivity.this, Provider.TWITTER);
      }
    });
  }


  private final class ResponseListener implements DialogListener {
    public void onComplete(Bundle values) {
      adapter.updateStatus(edit.getText().toString(), new MessageListener(), true);
      adapter.getUserProfileAsync(new ProfileDataListener());
    }

    @Override
    public void onError(SocialAuthError e) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCancel() {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onBack() {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }

  // To get status of message after authentication
  private final class MessageListener implements SocialAuthListener<Integer> {
    @Override
    public void onExecute(String provider, Integer integer) {

      Integer status = integer;
      if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
        Toast.makeText(getApplicationContext(), "Message posted", Toast.LENGTH_LONG).show();
      else
        Toast.makeText(getApplicationContext(), "Message not posted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(SocialAuthError e) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

  }

  private class ProfileDataListener implements SocialAuthListener<Profile> {
    @Override
    public void onExecute(String provider, Profile profile) {
      Log.d("Custom-UI", "Receiving Data");
      Profile profileMap = profile;
      Log.d("Custom-UI", "Validate ID         = " + profileMap.getValidatedId());
      Log.d("Custom-UI", "First Name          = " + profileMap.getFullName());
      Log.d("Custom-UI", "Language                 = " + profileMap.getLanguage());
      Log.d("Custom-UI", "Location                 = " + profileMap.getLocation());
      Log.d("Custom-UI", "Profile Image URL  = " + profileMap.getProfileImageURL());
    }

    @Override
    public void onError(SocialAuthError e) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }
}
