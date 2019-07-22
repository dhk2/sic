package anticlimacticteleservices.sic;


import android.Manifest;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static anticlimacticteleservices.sic.MainActivity.masterData;

public class ImportSubscriptions extends AsyncTask {
    Document doc;
    Set<String> links;
    public void ImportSubscriptions(){}
    protected void onPostExecute(String[] result) {


    }
    @Override
    protected Object doInBackground(Object[] objects) {
        links=new HashSet<>();
        System.out.print("starting to get imports "+masterData.getUseYoutube());
        if (masterData.getUseYoutube()) {

            //File input = new File("/tmp/input.html");
            try {
                File input = new File("/storage/emulated/0/Download/subscription_manager.odm");
                doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
                System.out.print("starting to import");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error opening file");
            }
           // System.out.println(doc);
            Elements listing = doc.getElementsByAttribute("xmlUrl");
            for (Element e : listing){
                links.add(e.attr("xmlUrl"));
                System.out.println("adding feedlink"+e.toString());
            }
        }
        System.out.println("done  importing links from file");
        System.out.println(links.size());
        if (masterData.feedLinks.size() == 2){
            //don't want to keep styx and pewds if thier importing from scratch
            masterData.setFeedLinks(links);
            masterData.setForceRefresh(true);
        }
        else {
            masterData.addFeedLinks(links);
            masterData.setForceRefresh(true);
        }
        return null;
    }

}
