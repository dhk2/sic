package anticlimacticteleservices.sic;


import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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
        Channel chan;
        if (masterData.getUseYoutube()) {
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
//                MainActivity.masterData.addChannel(new Channel(e.attr("xmlUrl")));
                System.out.println("adding Channel"+e.toString());
            }
        }
        System.out.println("done  importing links from file");
        System.out.println(links.size());
        return null;
    }
}
