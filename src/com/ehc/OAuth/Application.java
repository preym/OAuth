package com.ehc.OAuth;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 15/4/14
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Application extends android.app.Application {
  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Parse.initialize(getApplicationContext(), getString(R.string.app_id), "hMSjOPMrAsvrks3qdepfWkhXHBfgtl8s97zsDkU6");
    ParseFacebookUtils.initialize(getString(R.string.app_id));
  }
}
