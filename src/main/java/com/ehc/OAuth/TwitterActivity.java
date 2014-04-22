package com.ehc.OAuth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Feed;
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
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterActivity extends Activity {
  Button twitter;
  EditText edit;
  SocialAuthAdapter adapter;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.twitter);
    twitter = (Button) findViewById(R.id.twitter);
    edit = (EditText) findViewById(R.id.editTxt);
    adapter = new SocialAuthAdapter(new ResponseListener());
    twitter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        adapter.authorize(TwitterActivity.this, SocialAuthAdapter.Provider.TWITTER);
      }
    });
  }

  private final class ResponseListener implements DialogListener {
    @Override
    public void onComplete(Bundle values) {
      adapter.updateStatus(edit.getText().toString(), new MessageListener(), true);
      adapter.getUserProfileAsync(new ProfileDataListener());
      adapter.getContactListAsync(new ContactDataListener());
      adapter.getFeedsAsync(new FeedDataListener());
      Bitmap bitmap = null;
      try {
        adapter.uploadImageAsync("Landscape Images", "icon.png", bitmap, 0, new UploadImageListener());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    @Override
    public void onError(SocialAuthError e) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onBack() {
    }
  }

  private class MessageListener implements SocialAuthListener<Integer> {

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

  private final class ProfileDataListener implements SocialAuthListener<Profile> {
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

  private class ContactDataListener implements SocialAuthListener<List<Contact>> {
    @Override
    public void onExecute(String provider, List<Contact> contacts) {
      Log.d("Custom-UI", "Receiving Data");
      List<Contact> contactsList = contacts;
      if (contactsList != null && contactsList.size() > 0) {
        for (Contact c : contactsList) {
          Log.d("Custom-UI", "Contact ID = " + c.getId());
          Log.d("Custom-UI", "Display Name = " + c.getDisplayName());
        }
      }
    }

    @Override
    public void onError(SocialAuthError e) {

    }
  }

  private class FeedDataListener implements SocialAuthListener<List<Feed>> {
    @Override
    public void onExecute(String provider, List<Feed> feeds) {
      Log.d("Share-Bar", "Receiving Data");

      List<Feed> feedList = feeds;
      if (feedList != null && feedList.size() > 0) {
        for (Feed f : feedList) {
          Log.d("Custom-UI ", "Feed ID = " + f.getId());
          Log.d("Custom-UI", "Screen Name = " + f.getScreenName());
          Log.d("Custom-UI", "Message = " + f.getMessage());
          Log.d("Custom-UI ", "Get From = " + f.getFrom());
          Log.d("Custom-UI ", "Created at = " + f.getCreatedAt());
        }
      }
    }

    @Override
    public void onError(SocialAuthError e) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }

  private class UploadImageListener implements SocialAuthListener<Integer> {
    @Override
    public void onExecute(String provider, Integer integer) {
      Log.d("Custom-UI", "Uploading Data");
      Integer status = integer;
      Log.d("Custom-UI", String.valueOf(status));
      Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(SocialAuthError e) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }
}
