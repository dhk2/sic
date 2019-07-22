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

public class Search {
    private int searchCount = 0;
    private ArrayList<Video> sVideos;
    private ArrayList<Channel> sChannels;
    private Document doc;
    private boolean searching;

    public Search(String term, boolean video, boolean youtube, boolean bitchute) {

        searching = true;
        String fixedTerm = term.replaceAll("\\s+", "+");
        if (video) {
            final String location = "https://www.youtube.com/results?search_query=" + fixedTerm + "&sp=EgIQAQ%253D%253D";
            final String location2 = "https://search.bitchute.com/renderer?query=" + fixedTerm + "&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqr.kind=video";

            this.sVideos = new ArrayList<Video>();
            if (youtube) {
                YoutubeVideoSearcher youtubeScraper = new YoutubeVideoSearcher();
                youtubeScraper.execute(location);
            }
            if (bitchute) {
                BitchuteVideoSearcher bitchuteScraper = new BitchuteVideoSearcher();
                bitchuteScraper.execute(location2);
            }
        } else {
            final String location = "https://www.youtube.com/results?search_query=" + fixedTerm + "&sp=EgIQAg%253D%253D";
            final String location2 = "https://search.bitchute.com/renderer?query=" + fixedTerm + "&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqa.kind=channel";
            this.sChannels = new ArrayList<Channel>();

            if (youtube) {
                YoutubeChannelSearcher youtubecScraper = new YoutubeChannelSearcher();
                youtubecScraper.execute(location);
            } else {
                BitchuteChannelSearcher bitchutecScraper = new BitchuteChannelSearcher();
                bitchutecScraper.execute(location2);
            }
        }

    }

    public ArrayList<Video> getVideos() {
        System.out.println(sVideos.size());
        System.out.println(searching);

        return this.sVideos;
    }

    public Document getDoc() {
        System.out.println(this.doc.html());
        return this.doc;
    }

    public boolean getStatus() {
        return searching;
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
                    //                      System.out.println(l);

//                    String hack = l.getElementsByClass("yt-lockup-title ").first().getElementsByTag("a").attr("href");

                    //System.out.println("hack>>>>>>>>>"+hack);
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles) {
                        Elements anchor = t.getElementsByTag("a");
                        if (!anchor.isEmpty()) {
                            nv = new Video("https://www.youtube.com" + anchor.attr("href"));
                            //nv.setUrl("https://www.youtube.com" + anchor.attr("href"));
                            nv.setTitle(anchor.attr("title"));
                            nv.setDescription(anchor.attr("aria-label"));
                            //                        System.out.println(nv);
                            //                        System.out.println();
                        }
                    }

                    //pull out the thumbnail.url
                    Elements thumbs = l.getElementsByClass("yt-thumb-simple");
                    for (Element t : thumbs) {
                        //System.out.println(t);
                        for (Element tImg : t.getElementsByTag("img")) {
                            //System.out.println(tImg);
                            thumbnail = tImg.attr("data-thumb");

                        }
                    }
//                    System.out.println("thumbnail:"+tImg.attr("data-thumb"));
                    if ((!thumbnail.isEmpty()) && (nv.getUrl().indexOf("atch") > 0)) {
                        System.out.println("adding video " + nv.getUrl());
                        nv.setThumbnail(thumbnail);
                        sVideos.add(nv);
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
            // execution of result of Long time consuming operation
            System.out.println(sVideos.size());
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                VideoFragment fragment = new VideoFragment();
                ((VideoFragment) fragment).setVideos(sVideos);

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                System.out.println("Youtube search finished but searching isn't done yet");
            }

        }


        @Override
        protected void onPreExecute() {
            searching = true;
            searchCount++;
            System.out.println("starting to scrape youtube");
        }


    }

    private class BitchuteVideoSearcher extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //          String tempAuthor=doc.title();
            String thumbnail = "";
            try {
                System.out.println("scraping bitchute search at " + params[0]);
                doc = Jsoup.connect(params[0]).get();
                //System.out.println(doc);
                //System.out.println(doc.title());
                Elements results = doc.getElementsByClass("osscmnrdr oss-result");
                Elements parts = results.first().getAllElements();
                Video nv = new Video();
                for (Element r : parts) {
                    //                 System.out.println(">>>>>"+r+"<<<<");
                    //                   System.out.println(r.className());
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

                            try {
//                               URL url = new URL("https://www.bitchute.com" + vid.getUrl());
                                System.out.println("trying to get mp4 value form " + nv.getUrl());
                                if (nv.getUrl().indexOf("video") > 0) {
                                    Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                                    nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                                    sVideos.add(nv);
                                }
                                System.out.println("looped it dupe");

                                break;

                            } catch (MalformedURLException e) {
                                System.out.println("Malformed URL: " + e.getMessage());

                            } catch (IOException e) {
                                System.out.println("I/O Error: " + e.getMessage());

                            } catch (NullPointerException e) {
                                System.out.println("null pointer trying to parse search results " + e.getMessage());
                            }
                            sVideos.add(nv);

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
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            System.out.println(sVideos.size());
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                VideoFragment fragment = new VideoFragment();
                ((VideoFragment) fragment).setVideos(sVideos);

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
            }

            //VideoAdapter searchResults = new VideoAdapter(sVideos);
            VideoFragment fragment = new VideoFragment();
            ((VideoFragment) fragment).setVideos(sVideos);

            FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
            transaction.replace(R.id.search_subfragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();


        }


        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute channel search");
            searching = true;
            searchCount++;
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
                            nc.setUrl(r.child(0).attr("href"));
                            nc.setTitle(r.child(0).text());
                            break;
                        case "osscmnrdr ossfieldrdr2":
                            nc.setThumbnail(r.child(0).child(0).attr("src"));
                            break;
                        case "osscmnrdr ossfieldrdr3":
                            nc.setDescription(r.text());
                            break;
                        case "osscmnrdr ossfieldrdr8 oss-item-displayviews":
                            sChannels.add(nc);
                            nc=new Channel();
                            break;
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
            // execution of result of Long time consuming operation
            super.onPostExecute(result);
            System.out.println(sChannels.size());
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                ChannelFragment fragment = new ChannelFragment();
                ((ChannelFragment) fragment).setChannels(sChannels);

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                System.out.println("Bitchute search finished but searching isn't done yet");
            }
        }

        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute");
            searching = true;
            searchCount++;
            System.out.println("starting to scrape youtube");
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

                for (Element l : listing) {
                    //System.out.println(">>>>>>>"+l.toString()+"<<<<<<<<<<<<<<");
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles) {
                        Elements anchor = t.getElementsByTag("a");


                        if (!anchor.isEmpty()) {
                            nc.setUrl("https://www.youtube.com" + anchor.attr("href"));
                            nc.setTitle(anchor.attr("title"));
                            nc.setDescription(anchor.attr("aria-label"));
                            //System.out.println(anchor);
                        }
                    }
     /*               Elements thumbs = l.getElementsByClass("yt-thumb-simple");
                    for (Element t : thumbs) {
                        for (Element tImg : t.getElementsByTag("img")) {
                            thumbnail = tImg.attr("data-thumb");
                        }
                    }*/
                    if (nc.getUrl().indexOf("channel")>0) {
                        try {
                            Document doc = Jsoup.connect(nc.getUrl()).get();
                            nc.setDescription(doc.getElementsByAttributeValue("name","description").attr("content").toString());
                            nc.setThumbnail(doc.getElementsByAttributeValue("itemprop","thumbnailUrl").attr("href").toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Failed to load youtube channel page for " + nc.getTitle()+" at "+nc.getUrl());
                        }
                        sChannels.add(nc);
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
            System.out.println(sChannels.size());
            searchCount--;
            if (searchCount < 1) {
                searching = false;
                ChannelFragment fragment = new ChannelFragment();
                ((ChannelFragment) fragment).setChannels(sChannels);

                FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                transaction.replace(R.id.search_subfragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                System.out.println("youtube search finished but searching isn't done yet");
            }
        }

        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute");
            searching = true;
            searchCount++;
            System.out.println("starting to scrape youtube");
        }

    }
}


