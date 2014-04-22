package com.ehc.OAuth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 22/4/14
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionDetector {
  private Context _context;

  public ConnectionDetector(Context context) {
    this._context = context;
  }

  public boolean isConnectingToInternet() {
    ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity != null) {
      NetworkInfo[] info = connectivity.getAllNetworkInfo();
      if (info != null)
        for (int i = 0; i < info.length; i++)
          if (info[i].getState() == NetworkInfo.State.CONNECTED) {
            Log.d("Network", "NETWORKnAME: " + info[i].getTypeName());
            return true;
          }

    }
    return false;
  }
}
