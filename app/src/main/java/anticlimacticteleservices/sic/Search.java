package anticlimacticteleservices.sic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

import android.app.*;
import android.os.*;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

class Search {
    private int searchCount = 0;
    private Document doc;
    private boolean searching;

    public Search(String term, boolean video, boolean youtube, boolean bitchute) {

        searching = true;
        String fixedTerm = term.replaceAll("\\s+", "+");
        if (video) {
            MainActivity.masterData.setsVideos(new ArrayList <Video>());
            final String location = "https://www.youtube.com/results?search_query=" + fixedTerm + "&sp=EgIQAQ%253D%253D";
            final String location2 = "https://search.bitchute.com/renderer?query=" + fixedTerm + "&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqr.kind=video";
            final String location3 = "https://www.google.com/search?q="+fixedTerm+"+%22www.bitchute.com/channel%22";
            if (youtube){
                searchCount++;
            }
            if (bitchute){
                searchCount++;
            }

            if (youtube) {
                YoutubeVideoSearcher youtubeScraper = new YoutubeVideoSearcher();
                youtubeScraper.execute(location);
            }
            if (bitchute) {
                BitchuteVideoSearcher bitchuteScraper = new BitchuteVideoSearcher();
                bitchuteScraper.execute(location2);
            }
        } else {
            MainActivity.masterData.setsChannels(new ArrayList <Channel>());
            final String location = "https://www.youtube.com/results?search_query=" + fixedTerm + "&sp=EgIQAg%253D%253D";
            final String location2 = "https://search.bitchute.com/renderer?query=" + fixedTerm + "&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqa.kind=channel";
            final String location3 = "https://www.google.com/search?q="+fixedTerm+"+%22www.bitchute.com/channel%22&num=25";

            if (youtube){
                searchCount++;
            }
            if (bitchute){
                searchCount++;
                searchCount++;
            }

            if (youtube) {
                YoutubeChannelSearcher youtubecScraper = new YoutubeChannelSearcher();
                youtubecScraper.execute(location);
            }
            if (bitchute)
            {
                BitchuteChannelSearcher bitchutecScraper = new BitchuteChannelSearcher();
                bitchutecScraper.execute(location2);
                BitchuteGoogleChannelSearcher bgScraper = new BitchuteGoogleChannelSearcher();
                bgScraper.execute(location3);
            }
        }

    }

    private class YoutubeVideoSearcher extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //          String tempAuthor=doc.title();
            String thumbnail = "";
            try {
                doc = Jsoup.connect(params[0]).get();
                // pull out the responses from the list
                Elements listing = doc.getElementsByTag("li");
                Video nv = new Video();
                for (Element l : listing) {
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
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                VideoFragment fragment = new VideoFragment();
                MainActivity.masterData.sortsVideos();
                fragment.setVideos(MainActivity.masterData.getsVideos());
                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            } else {
                Log.v("search","Youtube search finished but searching isn't done yet");
            }
        }
        @Override
        protected void onPreExecute() {
            searching = true;
            Log.v("Search","starting to scrape youtube");
        }
    }
    private class BitchuteVideoSearcher extends AsyncTask<String, String, String> {
        private String resp;
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            String thumbnail = "";
            try {
                Log.v("Search","scraping bitchute search at " + params[0]);
                doc = Jsoup.connect(params[0]).get();
                Elements results = doc.getElementsByClass("osscmnrdr oss-result");
                Elements parts = results.first().getAllElements();
                Video nv = new Video();
                for (Element r : parts) {
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
                        case "osscmnrdr ossfieldrdr8 oss-item-displayviews":
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
            searchCount--;
            if (searchCount < 1) {
                Log.v("Search","done searching with "+MainActivity.masterData.getsVideos().size());
                searching = false;
                VideoFragment fragment = new VideoFragment();
                MainActivity.masterData.sortsVideos();
                fragment.setVideos(MainActivity.masterData.getsVideos());

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();

            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
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
            //          String tempAuthor=doc.title();
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
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            System.out.println("Search count:"+searchCount);
            System.out.println(MainActivity.masterData.getsChannels().size());
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                ChannelFragment fragment = new ChannelFragment();
                fragment.setChannels(MainActivity.masterData.getsChannels());

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
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
            String thumbnail = "";
            System.out.println("starting to search youtube using "+params[0]);
            try {
                doc = Jsoup.connect(params[0]).get();
                // pull out the responses from the list
                Elements listing = doc.getElementsByTag("li");
                Channel nc = new Channel();
                //System.out.println(doc);
                System.out.println("looping through "+listing.size()+"list elements");

                for (Element l : listing) {
                    //System.out.println(">>>>>>>"+l.toString()+"<<<<<<<<<<<<<<");
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles) {
                        Elements anchor = t.getElementsByTag("a");


                        if (!anchor.isEmpty()) {
                            String channelUrl=anchor.attr("href");
                            if (channelUrl.indexOf("channel")>0) {
                                System.out.println(channelUrl);
                                String[] segments = channelUrl.split("/");
                                nc=new Channel(" https://www.youtube.com/feeds/videos.xml?channel_id="+segments[segments.length - 1]);
                                //nc.setSourceID(segments[segments.length - 1]);
                              //  nc.setUrl(" https://www.youtube.com/feeds/videos.xml?channel_id=" + nc.getSourceID());
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

            System.out.println("Search count:"+searchCount);
            System.out.println(MainActivity.masterData.getsChannels().size());
            searchCount--;
            if (searchCount < 1) {
                System.out.println("done searching for channels," );
                searching = false;
                ChannelFragment fragment = new ChannelFragment();

                fragment.setChannels(MainActivity.masterData.getsChannels());

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
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
            try {
                System.out.println("scraping youtube search at " + params[0]);
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
            System.out.println("Search count:"+searchCount);
            System.out.println(MainActivity.masterData.getsChannels().size());
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                ChannelFragment fragment = new ChannelFragment();
                fragment.setChannels(MainActivity.masterData.getsChannels());

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
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


