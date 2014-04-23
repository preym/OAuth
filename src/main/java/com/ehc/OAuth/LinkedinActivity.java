package com.ehc.OAuth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 21/4/14
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinkedinActivity extends Activity {
  SocialAuthAdapter socialAuthAdapter;
  Button linked_in;
  EditText edit;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_source);

    socialAuthAdapter = new SocialAuthAdapter(new ResponseListener());
    linked_in = (Button) findViewById(R.id.linkedIn);
    edit = (EditText) findViewById(R.id.editTxt);
    linked_in.setBackgroundResource(R.drawable.linkedin);
    linked_in.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        socialAuthAdapter.authorize(LinkedinActivity.this, SocialAuthAdapter.Provider.LINKEDIN);
      }
    });

  }


  private final class ResponseListener implements DialogListener {
    @Override
    public void onComplete(Bundle values) {
      socialAuthAdapter.updateStatus(edit.getText().toString(), new MessageListener(), true);
      socialAuthAdapter.getUserProfileAsync(new ProfileDataListener());
      socialAuthAdapter.getContactListAsync(new ContactDataListener());
    }

    @Override
    public void onError(SocialAuthError e) {
      Log.d("error", e.getMessage());
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onBack() {
    }
  }

  private final class MessageListener implements SocialAuthListener<Integer> {
    @Override
    public void onExecute(String provider, Integer t) {
      Integer status = t;
      if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
        Toast.makeText(getApplicationContext(), "Message posted", Toast.LENGTH_LONG).show();
      else
        Toast.makeText(getApplicationContext(), "Message not posted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(SocialAuthError e) {
      Log.d("error", e.getMessage());
    }

    @Override
    public void onExecute(List<Contact> t) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }

  private class ProfileDataListener implements SocialAuthListener<Profile> {
    @Override
    public void onExecute(String provider, Profile profile) {
      Log.d("Custom-UI", "Receiving Data");
      Profile profileMap = profile;
      Log.d("Custom-UI", "Validate ID         = " + profileMap.getValidatedId());
      Log.d("Custom-UI", "First Name          = " + profileMap.getFirstName());
      Log.d("Custom-UI", "Last Name           = " + profileMap.getLastName());
      Log.d("Custom-UI", "Email               = " + profileMap.getEmail());
      Log.d("Custom-UI", "Country                  = " + profileMap.getCountry());
      Log.d("Custom-UI", "Language                 = " + profileMap.getLanguage());
      Log.d("Custom-UI", "Location                 = " + profileMap.getLocation());
      Log.d("Custom-UI", "Profile Image URL  = " + profileMap.getProfileImageURL());
    }

    @Override
    public void onError(SocialAuthError e) {
      Log.d("error", e.getMessage());
    }

    @Override
    public void onExecute(List<Contact> t) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }

  private class ContactDataListener implements SocialAuthListener<List<Contact>> {
    @Override
    public void onExecute(String provider, List<Contact> contacts) {
      Log.d("Custom-UI", "Receiving Data");
      List<Contact> contactsList = contacts;
      if (contactsList != null && contactsList.size() > 0) {
        for (Contact c : contactsList) {
          Log.d("Custom-UI", "Contact ID = " + c.getId());
          Log.d("Custom-UI", "First Name = " + c.getFirstName());
          Log.d("Custom-UI", "Last Name = " + c.getLastName());
        }
      }
    }

    @Override
    public void onError(SocialAuthError e) {
      Log.d("error", e.getMessage());
    }

    @Override
    public void onExecute(List<Contact> t) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }
}
