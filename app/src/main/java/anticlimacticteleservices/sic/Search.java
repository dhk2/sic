package anticlimacticteleservices.sic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.List;

import android.app.*;
import android.os.*;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

class Search {
    private int searchCount = 0;
    private Document doc;
    private boolean searching;
    public VideoFragment videoFragment;
    public ChannelFragment channelFragment;
    final SimpleDateFormat bvsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public Search(String term, boolean video, boolean youtube, boolean bitchute,boolean feed) {

        searching = true;
      //  MainActivity.masterData.getMainActionBar().setTitle("Searching..........");
        String fixedTerm = term.replaceAll("\\s+", "+");

        if (video) {
            videoFragment = new VideoFragment();
            MainActivity.masterData.sortsVideos();
            videoFragment.setVideos(MainActivity.masterData.getsVideos());
            FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
            transaction.replace(R.id.search_subfragment,videoFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
            Log.v("Search","Performing video search");
            MainActivity.masterData.setsVideos(new ArrayList <Video>());
            final String location = "https://www.youtube.com/results?search_query=" + fixedTerm + "&sp=EgIQAQ%253D%253D";
            final String location2 = "https://search.bitchute.com/renderer?query=" + fixedTerm + "&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqr.kind=video";
            final String location3 = "https://www.google.com/search?q="+fixedTerm+"+%22www.bitchute.com/video%22";
            final String location4 = "https://duckduckgo.com/?q="+fixedTerm+"+site%3Awww.bitchute.com%2Fvideo&ia=web";
            if (feed){
                List temp = new  <Video>ArrayList();
                for (Video bob : MainActivity.masterData.getVideoDao().getVideos()) {
                    if (bob.toHtmlString().toLowerCase().indexOf(term.toString().toLowerCase()) > 0) {
                        temp.add(bob);
                        videoFragment.addVideo(bob);
                    }
                }
                MainActivity.masterData.setsVideos(temp);
            }
            if (youtube){
                searchCount++;
                YoutubeVideoSearcher youtubeScraper = new YoutubeVideoSearcher();
                youtubeScraper.execute(location);
            }
            if (bitchute) {
                if (MainActivity.masterData.isBitchuteSearchBitchute()) {
                    searchCount++;
                    BitchuteVideoSearcher bitchuteScraper = new BitchuteVideoSearcher();
                    bitchuteScraper.execute(location2);
                }
                if (MainActivity.masterData.isBitchuteSearchGoogle()) {
                    searchCount++;
                    GoogleVideoSearcher googleScraper = new GoogleVideoSearcher();
                    googleScraper.execute(location3);
                }
                if (MainActivity.masterData.isBitchuteSearchDuck()) {
                    searchCount++;
                    DuckVideoSearcher duckScraper = new DuckVideoSearcher();
                    duckScraper.execute(location4);
                }
            }
        }
        else {
            MainActivity.masterData.setsChannels(new ArrayList <Channel>());
            final String location = "https://www.youtube.com/results?search_query=" + fixedTerm + "&sp=EgIQAg%253D%253D";
            final String location2 = "https://search.bitchute.com/renderer?query=" + fixedTerm + "&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqa.kind=channel";
            final String location3 = "https://www.google.com/search?q="+fixedTerm+"+%22www.bitchute.com/channel%22&num=25";
            final String location4 = "https://duckduckgo.com/?q="+fixedTerm+"+site%3Awww.bitchute.com%2Fchannel&ia=web";
            channelFragment = new ChannelFragment();
            channelFragment.setChannels(MainActivity.masterData.getsChannels());
            FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
            transaction.replace(R.id.search_subfragment, channelFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
            MainActivity.masterData.getMainActionBar().hide();


            if (feed){
                List temp = new  <Channel>ArrayList();
                for (Channel bob : MainActivity.masterData.getChannelDao().getChannels()) {
                    if (bob.toString().toLowerCase().indexOf(term.toString().toLowerCase()) > 0) {
                        channelFragment.addChannel(bob);
                        temp.add(bob);
                    }
                }
                MainActivity.masterData.setsChannels(temp);
            }

            if (youtube) {
                searchCount++;
                YoutubeChannelSearcher youtubecScraper = new YoutubeChannelSearcher();
                youtubecScraper.execute(location);
            }
            if (bitchute)
            {
                if (MainActivity.masterData.isBitchuteSearchBitchute()){
                    searchCount++;
                    BitchuteChannelSearcher bitchutecScraper = new BitchuteChannelSearcher();
                    bitchutecScraper.execute(location2);
                }
                if (MainActivity.masterData.isBitchuteSearchGoogle()) {
                    searchCount++;
                    BitchuteGoogleChannelSearcher bgScraper = new BitchuteGoogleChannelSearcher();
                    bgScraper.execute(location3);
                }
                if (MainActivity.masterData.isBitchuteSearchDuck()){
                    searchCount++;
                    BitchuteDuckChannelSearcher bdScraper = new BitchuteDuckChannelSearcher();
                    bdScraper.execute(location4);
                }
            }
        }
        if (searchCount==0){
            MainActivity.masterData.getMainActionBar().hide();
        }
    }

    private class YoutubeVideoSearcher extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            Log.v("Search-YVS ", MainActivity.masterData.getsVideos().size()+ "search:"+params[0]);
            String thumbnail = "";
            try {
                doc = Jsoup.connect(params[0]).get();
                System.out.println(doc);
                // pull out the responses from the list
                Elements listing = doc.getElementsByTag("li");
                Video nv = new Video();
                for (Element l : listing) {
                    //System.out.println("trying for date"+l.getElementsByClass("style-scope ytd-video-meta-block").text());
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles) {
                        Elements anchor = t.getElementsByTag("a");
                        if (!anchor.isEmpty()) {
                            nv = new Video("https://www.youtube.com" + anchor.attr("href"));
                            nv.setTitle(anchor.attr("title"));
                            nv.setDescription(anchor.attr("aria-label"));
                        }
                    }
                    Elements thumbs = l.getElementsByClass("yt-thumb-simple");
                    for (Element t : thumbs) {
                        for (Element tImg : t.getElementsByTag("img")) {
                            thumbnail = tImg.attr("data-thumb");
                        }
                    }
                    if ((!thumbnail.isEmpty()) && (nv.getUrl().indexOf("atch") > 0)) {
                        nv.setThumbnail(thumbnail);
                        MainActivity.masterData.addsVideos(nv);
                        thumbnail = "";
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            }
            Log.v("Search-YVS ", MainActivity.masterData.getsVideos().size()+" done searching");
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            Log.v("Search","done searching Youtube videos");
            videoFragment.setVideos(MainActivity.masterData.getsVideos());
            searchCount--;
            if (searchCount < 1) {
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
            }
        }
        @Override
        protected void onPreExecute() {
            searching = true;
            Log.v("Search","starting to search youtube videos");
        }
    }
    private class BitchuteVideoSearcher extends AsyncTask<String, String, String> {
        private String resp;
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            String thumbnail = "";
            try {
                Log.v("Search-BVS","scraping bitchute search at " + params[0]);
                doc = Jsoup.connect(params[0]).get();
                Elements results = doc.getElementsByClass("osscmnrdr oss-result");
                Elements parts = results.first().getAllElements();
                Video nv = new Video();
                Date pd = new Date();
                for (Element r : parts) {
                  // System.out.println("["+r.className()+"]<-=->["+r.text()+"]");
                    switch (r.className()) {
                        case "osscmnrdr ossfieldrdr1":
                            nv = new Video(r.child(0).attr("href"));
                            nv.setTitle(r.text());
                            break;
                        case "osscmnrdr ossfieldrdr2":
                            nv.setThumbnail(r.child(0).child(0).attr("src"));
                            break;
                        case "osscmnrdr ossfieldrdr3":
                            nv.setDescription(r.text());
                            break;

                        case "osscmnrdr ossfieldrdr4 oss-item-date":
                            System.out.println(r.text()+" time date stuffing "+bvsdf.toString()+" "+r.text().substring(0,19));
                            try {
                                pd = bvsdf.parse(r.text().substring(0,19));
                            } catch (ParseException ex) {
                                Log.v("Search-bvs", ex.getLocalizedMessage());
                            }
                            nv.setDate(pd);
                            break;
                        case "osscmnrdr ossfieldrdr8 oss-item-displayviews":
                            nv.setViewCount(r.text());
                            MainActivity.masterData.addsVideos(nv);
                    }
                }
            } catch (MalformedURLException e) {

                Log.e("Search","Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e("Search","I/O Error: " + e.getMessage());
            } catch(NullPointerException e){
                Log.e("Search","Null pointer exception"+e.getMessage());
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            System.out.println("searchCount:"+searchCount);
            videoFragment.setVideos(MainActivity.masterData.getsVideos());
            searchCount--;
            if (searchCount < 1) {
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
            }
        }


        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute channel search");
            searching = true;
           // searchCount++;
        }


    }
      private class GoogleVideoSearcher extends AsyncTask<String, String, String> {
        private String resp;
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            String thumbnail = "";
            try {
                Log.v("Search-gVS","scraping with google search at " + params[0]);
                doc = Jsoup.connect(params[0]).get();
           //    System.out.println(doc);
            //    Elements results = doc.getElementsByClass("osscmnrdr oss-result");
            //    Elements parts = results.first().getAllElements();
                Elements parts = doc.getElementsByTag("a");
                Video nv = new Video();
                Date pd = new Date();
                for (Element r : parts) {
                   Log.v("Search-GVS","["+r.attr("href")+"]<-=->["+r.text()+"]");
                   String url = r.attr("href");
                   if (null==url || url.length()<35){
                       continue;
                   }
                   if (url.substring(0,31).equals("https://www.bitchute.com/video/")){
                       nv=new Video(url);
                       Document bitchuteDoc = Jsoup.connect(nv.getBitchuteUrl()).get();
                       System.out.println(bitchuteDoc);
                       nv.setCategory(bitchuteDoc.getElementsByClass("video-detail-list").first().getElementsByTag("a").first().text());
                       nv.setDescription(bitchuteDoc.getElementsByClass("full hidden").toString());
                       nv.setMagnet(bitchuteDoc.getElementsByClass("video-actions").first().getElementsByAttribute("href").first().attr("href"));
                       nv.setMp4(bitchuteDoc.getElementsByTag("source").attr("src"));
                       nv.setThumbnailurl(bitchuteDoc.getElementsByTag("video").attr("poster"));
                        nv.setTitle(bitchuteDoc.getElementsByTag("title").first().text());
                       System.out.println(nv.toDebugString());

                       MainActivity.masterData.addsVideos(nv);
                   }
                }
            } catch (MalformedURLException e) {

                Log.e("Search","Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e("Search","I/O Error: " + e.getMessage());
            } catch(NullPointerException e){
                Log.e("Search","Null pointer exception"+e.getMessage());
                e.printStackTrace();
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            System.out.println("searchCount:"+searchCount);
            videoFragment.setVideos(MainActivity.masterData.getsVideos());
            searchCount--;
            if (searchCount < 1) {
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
            }
        }


        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute channel search");
            searching = true;
            // searchCount++;
        }


    }
    private class DuckVideoSearcher extends AsyncTask<String, String, String> {
        private String resp;
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            String thumbnail = "";
            try {
                Log.v("Search-BVS","scraping duck duck go search at " + params[0]);
                doc = Jsoup.connect(params[0]).get();
               // System.out.println(doc);
             //   Elements results = doc.getElementsByClass("osscmnrdr oss-result");
              //  Elements parts = results.first().getAllElements();
                Elements parts = doc.getAllElements();
                Video nv = new Video();
                Date pd = new Date();
                for (Element r : parts) {
                    // System.out.println("["+r.className()+"]<-=->["+r.text()+"]");
                    switch (r.className()) {
                        case "osscmnrdr ossfieldrdr1":
                            nv = new Video(r.child(0).attr("href"));
                            nv.setTitle(r.text());
                            break;
                        case "osscmnrdr ossfieldrdr2":
                            nv.setThumbnail(r.child(0).child(0).attr("src"));
                            break;
                        case "osscmnrdr ossfieldrdr3":
                            nv.setDescription(r.text());
                            break;

                        case "osscmnrdr ossfieldrdr4 oss-item-date":
                            System.out.println(r.text()+" time date stuffing "+bvsdf.toString()+" "+r.text().substring(0,19));
                            try {
                                pd = bvsdf.parse(r.text().substring(0,19));
                            } catch (ParseException ex) {
                                Log.v("Search-bvs", ex.getLocalizedMessage());
                            }
                            nv.setDate(pd);
                            break;
                        case "osscmnrdr ossfieldrdr8 oss-item-displayviews":
                            nv.setViewCount(r.text());
                            MainActivity.masterData.addsVideos(nv);
                    }
                }
            } catch (MalformedURLException e) {

                Log.e("Search","Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e("Search","I/O Error: " + e.getMessage());
            } catch(NullPointerException e){
                Log.e("Search","Null pointer exception"+e.getMessage());
                e.printStackTrace();
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            System.out.println("searchCount:"+searchCount);
            searchCount--;
            if (searchCount < 1) {
                Log.v("Search-DVS","done searching with "+MainActivity.masterData.getsVideos().size());
                searching = false;
                VideoFragment fragment = new VideoFragment();
                MainActivity.masterData.sortsVideos();
                fragment.setVideos(MainActivity.masterData.getsVideos());

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("duck duck go search finished but searching isn't done yet"+MainActivity.masterData.getsVideos().size());
            }
/*
            //VideoAdapter searchResults = new VideoAdapter(sVideos);
            VideoFragment fragment = new VideoFragment();
            ((VideoFragment) fragment).setVideos(sVideos);

            FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
            transaction.replace(R.id.search_subfragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

*/
        }


        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute channel search");
            searching = true;
            // searchCount++;
        }


    }
    private class BitchuteChannelSearcher extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.v("Search-BCS ", MainActivity.masterData.getsChannels().size()+ "search:"+params[0]);
            String thumbnail = "";
            try {
                System.out.println("scraping bitchute channel search at " + params[0]);
                doc = Jsoup.connect(params[0]).get();
              //  System.out.println(doc);
                //System.out.println(doc.title());

                Elements results = doc.getElementsByClass("osscmnrdr oss-result");
                Elements parts = results.first().getAllElements();
                Channel nc = new Channel();
                for (Element r : parts) {
                    //                 System.out.println(">>>>>"+r+"<<<<");
                    //                   System.out.println(r.className());
                    switch (r.className()) {

                        case "osscmnrdr ossfieldrdr1":
                            nc =new Channel(r.child(0).attr("href"));
                            nc.setTitle(r.child(0).text());
                            break;
                        case "osscmnrdr ossfieldrdr2":
                            nc.setThumbnail(r.child(0).child(0).attr("src"));
                            break;
                        case "osscmnrdr ossfieldrdr3":
                            nc.setDescription(r.text());
                            break;
                        case "osscmnrdr ossfieldrdr8 oss-item-displayviews":
                          //  System.out.println(nc);
                           MainActivity.masterData.addsChannel(nc);
                            nc=new Channel();
                            break;
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            } catch(NullPointerException e){
                System.out.println("Null pointer exception"+e.getMessage());
                e.printStackTrace();
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            Log.v("Search-BCS ", MainActivity.masterData.getsChannels().size()+ "done searching");
            System.out.println("searchCount:"+searchCount);
            channelFragment.setChannels(MainActivity.masterData.getsChannels());
            searchCount--;
            if (searchCount < 1) {
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
            }
        }

        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute");
            searching = true;
           // searchCount++;
        }
    }
    private class YoutubeChannelSearcher extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.v("Search-YCS ", MainActivity.masterData.getsChannels().size()+ "search:"+params[0]);
            String thumbnail = "";
            try {
                doc = Jsoup.connect(params[0]).get();
                Elements listing = doc.getElementsByTag("li");
                Channel nc = new Channel();
                System.out.println("looping through "+listing.size()+"list elements");

                for (Element l : listing) {
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles) {
                        Elements anchor = t.getElementsByTag("a");


                        if (!anchor.isEmpty()) {
                            String channelUrl=anchor.attr("href");
                            if (channelUrl.indexOf("channel")>0) {
                                System.out.println(channelUrl);
                                String[] segments = channelUrl.split("/");
                                nc=new Channel(" https://www.youtube.com/feeds/videos.xml?channel_id="+segments[segments.length - 1]);
                                nc.setTitle(anchor.attr("title"));
                                nc.setDescription(anchor.attr("aria-label"));
                            }
                        }
                    }
                    if (nc.getUrl().indexOf("channel")>0) {
   /*                     try {
                            Document doc = Jsoup.connect(nc.getUrl()).get();
                            nc.setDescription(doc.getElementsByAttributeValue("name","description").attr("content").toString());
                            nc.setThumbnail(doc.getElementsByAttributeValue("itemprop","thumbnailUrl").attr("href").toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Failed to load youtube channel page for " + nc.getTitle()+" at "+nc.getUrl());
                        }
   */

//                        nc.setUrl(" https://www.youtube.com/feeds/videos.xml?channel_id="+nc.getSourceID());
                        MainActivity.masterData.addsChannel(nc);
 //                       System.out.println("adding channel to search results:"+nc);
                    }
                    nc=new Channel();
                    thumbnail = "";

                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            }

            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);

            Log.v("Search-YCS ", MainActivity.masterData.getsChannels().size()+ "done searcing for channels");
            System.out.println("searchCount:"+searchCount);
            searchCount--;
            channelFragment.setChannels(MainActivity.masterData.getsChannels());
            if (searchCount < 1) {
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("youtube search finished but searching isn't done yet");
            }
        }

        @Override
        protected void onPreExecute() {
            searching = true;
            //searchCount++;
            System.out.println("starting to scrape youtube");
        }

    }
    private class BitchuteGoogleChannelSearcher extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            //          String tempAuthor=doc.title();
            String thumbnail = "";
            Log.v("Search-BGCS ", MainActivity.masterData.getsVideos().size()+ "search:"+params[0]);
            try {
                doc = Jsoup.connect(params[0]).get();
                  //System.out.println(doc);
              //    Elements links = doc.getElementsByAttribute("href");
                Elements links = doc.getElementsByClass("rc");
                  for (Element l : links){
                    //  System.out.println("[[[[["+l+"]]]]]");
                      String link=l.getElementsByAttribute("href").first().attr("href");
                      if ((link.length()>33) && (link.substring(0,33).equals("https://www.bitchute.com/channel/"))) {
                          Channel nc = new Channel(link);
                          nc.setDescription(l.getElementsByClass("st").text());
                          nc.setTitle(l.getElementsByTag("h3").text());
                          nc.setThumbnail("https://i2.wp.com/www.xanjero.com/wp-content/uploads/2018/04/G-Suite-apps-cards.png");
                          MainActivity.masterData.addsChannel(nc);
                      }
                  }
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            } catch(NullPointerException e){
                System.out.println("Null pointer exception"+e.getMessage());
                e.printStackTrace();
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            Log.v("Search-BGCS ", MainActivity.masterData.getsChannels().size()+ "done searcing for channels");
            System.out.println("searchCount:"+searchCount);
            channelFragment.setChannels(MainActivity.masterData.getsChannels());
            searchCount--;
            if (searchCount < 1) {
                MainActivity.masterData.getMainActionBar().hide();

            } else {
                System.out.println("Bitchute google search finished but searching isn't done yet");
            }
        }

        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape google for BitChute channels");
            searching = true;
            // searchCount++;
        }
    }
    private class BitchuteDuckChannelSearcher extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            //          String tempAuthor=doc.title();
            String thumbnail = "";
            Log.v("Search-BGCS ", MainActivity.masterData.getsVideos().size()+ "search:"+params[0]);
            try {
                doc = Jsoup.connect(params[0]).get();
                System.out.println(doc);
                //    Elements links = doc.getElementsByAttribute("href");
                Elements links = doc.getElementsByClass("rc");
                for (Element l : links){
                    //  System.out.println("[[[[["+l+"]]]]]");
                    String link=l.getElementsByAttribute("href").first().attr("href");
                    if ((link.length()>33) && (link.substring(0,33).equals("https://www.bitchute.com/channel/"))) {
                        Channel nc = new Channel(link);
                        nc.setDescription(l.getElementsByClass("st").text());
                        nc.setTitle(l.getElementsByTag("h3").text());
                        nc.setThumbnail("https://i2.wp.com/www.xanjero.com/wp-content/uploads/2018/04/G-Suite-apps-cards.png");
                        MainActivity.masterData.addsChannel(nc);
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            } catch(NullPointerException e){
                System.out.println("Null pointer exception"+e.getMessage());
                e.printStackTrace();
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            Log.v("Search-BGCS ", MainActivity.masterData.getsChannels().size()+ "done searcing for channels");
            System.out.println("searchCount:"+searchCount);
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                ChannelFragment fragment = new ChannelFragment();
                fragment.setChannels(MainActivity.masterData.getsChannels());

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
                MainActivity.masterData.getMainActionBar().hide();
            } else {
                System.out.println("Bitchute google search finished but searching isn't done yet");
            }
        }

        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape google for BitChute channels");
            searching = true;
            // searchCount++;
        }
    }
}


