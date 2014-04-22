package com.ehc.OAuth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 22/4/14
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlertDialogManager {
  public void showAlertDialog(Context context, String title, String message, Boolean status) {
    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    if (status != null)
      alertDialog.setIcon((status) ? R.drawable.ic_launcher : R.drawable.ic_launcher);
    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
      }
    });
    alertDialog.show();
  }
}
