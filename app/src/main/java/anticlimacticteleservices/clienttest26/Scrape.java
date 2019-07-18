package anticlimacticteleservices.clienttest26;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

public class Scrape extends AsyncTask<String[], Void, String[]> {
  ProgressDialog dialog;

  @Override
  protected void onPreExecute() {
     super.onPreExecute();
    dialog = new ProgressDialog(context);
    dialog.setTitle("Searching...");
    dialog.setMessage("Please wait...");
    dialog.setIndeterminate(true);
    dialog.show();
  }

  @Override
  protected String[] doInBackground(String[]... passing) {
    String[] result = new String[10];
    String[] passed = passing[0]; //get passed array
    System.out.println(passed[0]);
    
    //Some calculations...

    return result; //return result
}

protected void onPostExecute(String[] result) {
    dialog.dismiss();
    Collections.sort(videoFeed);
 // need to pass the list of videos back to the video fragment that orignally sent the request
 // a listener implementation most likely
 //https://github.com/codepath/android_guides/wiki/Creating-and-Using-Fragments
 
}
}

