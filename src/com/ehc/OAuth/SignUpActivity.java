package com.ehc.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 14/4/14
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SignUpActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);
    Button button = (Button) findViewById(R.id.sign_with_facebook);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
        startActivity(intent);
      }
    });


  }
}

