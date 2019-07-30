package anticlimacticteleservices.sic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

class Scrape extends AsyncTask<String[], Void, String[]> {
  private ProgressDialog dialog;

  @Override
  protected void onPreExecute() {
     super.onPreExecute();

  }

  @Override
  protected String[] doInBackground(String[]... passing) {
    String[] result = new String[10];
    String[] passed = passing[0]; //get passed array
    System.out.println(passed[0]);
    String fixedTerm=passed[0].replaceAll("\\s+", "+");
    final String location = "https://www.youtube.com/results?search_query="+fixedTerm;
    final String location2 = "https://search.bitchute.com/renderer?query="+fixedTerm+"&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqr.kind=video";
/*
Channels:
https://www.youtube.com/results?sp=EgIQAg%253D%253D&search_query=dino

Videos:
https://www.youtube.com/results?sp=EgIQAQ%253D%253D&search_query=dino
*/
    
    return result; //return result
}

protected void onPostExecute(String[] result) {
    dialog.dismiss();
  //  Collections.sort(videoFeed);
 // need to pass the list of videos back to the video fragment that originally sent the request
 // a listener implementation most likely
 //https://github.com/codepath/android_guides/wiki/Creating-and-Using-Fragments
 
}
}

