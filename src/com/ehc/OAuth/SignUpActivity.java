package com.ehc.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


public class SignUpActivity extends Activity {

  EditText userName;
  EditText firstName;
  EditText lastName;
  EditText email;
  EditText password;
  EditText confirmPassword;
  EditText contactNumber;
  EditText address;
  Button signUp;
  Button signUpWithMail;
  Button signUpWithFacebook;
  GraphUser currentUser;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);
    getWidgets();
    applyAction();

  }

  private void applyAction() {
    signUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (userName.getText().length() == 0) {
          userName.setError("Field Required");
          return;
        }
        if (firstName.getText().length() == 0) {
          firstName.setError("Field Required");
          return;
        }
        if (lastName.getText().length() == 0) {
          lastName.setError("Field Required");
          return;
        }
        if (email.getText().length() == 0) {
          email.setError("Field Required");
          return;
        }
        if (address.getText().length() == 0) {
          address.setError("Field Required");
          return;
        }
        if (contactNumber.getText().length() == 0) {
          contactNumber.setError("Field Required");
          return;
        }
        if (password.getText().length() == 0) {
          password.setError("Field Required");
          return;
        }
        if (confirmPassword.getText().length() == 0) {
          confirmPassword.setError("Field Required");
          return;
        }
        if (!(password.getText().toString().equals(confirmPassword.getText().toString()))) {
          confirmPassword.setError("Password doesn't match");
          return;
        }
        saveUser();
      }
    });
  }

  private void saveUser() {
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    if (database != null) {
      String query = "insert into user(USERNAME,FIRSTNAME,LASTNAME,EMAIL,ADDRESS,PASSWORD,PHONENUMBER) " +
          "values('" + userName.getText() + "','"
          + firstName.getText() + "','" + lastName.getText() + "','" +
          email.getText() + "','" + address.getText() + "','" + password.getText() + "'," + contactNumber.getText() + ")";
      database.execSQL(query);
      database.close();
      Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_LONG).show();
      startDashboard();
    }
  }

  private void startDashboard() {
    Intent intent = new Intent(this, DashboardActivity.class);
    startActivity(intent);

  }

  private void getWidgets() {
    userName = (EditText) findViewById(R.id.user_name);
    firstName = (EditText) findViewById(R.id.first_name);
    lastName = (EditText) findViewById(R.id.last_name);
    email = (EditText) findViewById(R.id.email);
    password = (EditText) findViewById(R.id.password);
    confirmPassword = (EditText) findViewById(R.id.confirm_password);
    contactNumber = (EditText) findViewById(R.id.contact_number);
    address = (EditText) findViewById(R.id.address);
    signUp = (Button) findViewById(R.id.sign_up);
    signUpWithMail = (Button) findViewById(R.id.sign_with_email);
    signUpWithFacebook = (Button) findViewById(R.id.sign_with_facebook);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session session = Session.getActiveSession();
    session.onActivityResult(this, requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      getSession();
      signUpWithFacebook.setVisibility(View.GONE);
    }
  }

  private void checkUserExistency() {
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase database = dbHelper.getReadableDatabase();
    Cursor cursor = database.rawQuery("select * from user where USERNAME='" + currentUser.getUsername() + "'", null);
    Log.d("test:", cursor.getCount() + "");

    if (cursor != null && cursor.getCount() > 0) {
      startDashboard();
    }

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
                populateFields();
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

  private void populateFields() {
    userName.setText(currentUser.getUsername());
    firstName.setText(currentUser.getFirstName());
    lastName.setText(currentUser.getLastName());
    address.setText("hyderabad");
  }
}

