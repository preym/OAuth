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
import com.facebook.model.GraphUser;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 14/4/14
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DashboardActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    Log.d("test:", "onCreate start");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dashboard);
    Intent intent = getIntent();
    String userName = intent.getStringExtra("user");
    TextView textView = (TextView) findViewById(R.id.textView);
    textView.setText(userName);
  }
}
