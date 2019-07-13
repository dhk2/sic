package anticlimacticteleservices.clienttest26;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
public class Search {
    private ArrayList<Video> videos;
    private Document doc;
    public Search(String term) {
        String fixedTerm=term.replaceAll("\\s+", "+");
        final String location = "https://www.youtube.com/results?search_query="+fixedTerm;
        final String location2 = "https://search.bitchute.com/renderer?query="+fixedTerm+"&use=bitchute-json&name=Search&login=bcadmin&key=7ea2d72b62aa4f762cc5a348ef6642b8&fqr.kind=video";
        this.videos=new ArrayList<Video>();
        AsyncTaskRunner youtubeScraper = new AsyncTaskRunner();
 //       youtubeScraper.execute(location);
        AsyncTaskRunner2 bitchuteScraper = new AsyncTaskRunner2();
        bitchuteScraper.execute(location2);

    }
    public ArrayList<Video> getVideos(){
        return this.videos;
    }
    public Document getDoc(){
        System.out.println(this.doc.html());
        return this.doc;
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
  //          String tempAuthor=doc.title();
            String thumbnail="";
            try {
                doc = Jsoup.connect(params[0]).get();
                // pull out the responses from the list
                Elements listing = doc.getElementsByTag("li");
                Video nv=new Video();
                for (Element l : listing) {
                    //                      System.out.println(l);

//                    String hack = l.getElementsByClass("yt-lockup-title ").first().getElementsByTag("a").attr("href");

                    //System.out.println("hack>>>>>>>>>"+hack);
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles){
                        Elements anchor = t.getElementsByTag("a");
                        if (!anchor.isEmpty()) {
                            nv= new Video("https://www.youtube.com" + anchor.attr("href"));
                            //nv.setUrl("https://www.youtube.com" + anchor.attr("href"));
                            nv.setTitle(anchor.attr("title"));
                            nv.setDescription(anchor.attr("aria-label"));
                            //                        System.out.println(nv);
                            //                        System.out.println();
                        }
                    }

                    //pull out the thumbnail.url
                    Elements thumbs = l.getElementsByClass("yt-thumb-simple");
                    for (Element t : thumbs){
                        //System.out.println(t);
                        for (Element tImg : t.getElementsByTag("img")) {
                            //System.out.println(tImg);
                          thumbnail =tImg.attr("data-thumb");

                        }
                    }
//                    System.out.println("thumbnail:"+tImg.attr("data-thumb"));
                    if ((!thumbnail.isEmpty()) && (nv.getUrl().indexOf("atch")>0)) {
                        System.out.println("adding video " + nv.getUrl());
                        nv.setThumbnail(thumbnail);
                        videos.add(nv);
                        thumbnail = "";
                    }
                }
            }
            catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            }
            catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            }
            return "done";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            System.out.println(videos.size());
        }


        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape youtube");
        }


    }

    private class AsyncTaskRunner2 extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //          String tempAuthor=doc.title();
            String thumbnail="";
            try {
                System.out.println("scraping bitchute search at "+params[0]);
                doc = Jsoup.connect(params[0]).get();
                //System.out.println(doc.title());
                Elements results = doc.getElementsByClass("osscmnrdr oss-result");
                Elements parts = results.first().getAllElements();
                Video nv = new Video();
                for (Element r : parts){
   //                 System.out.println(">>>>>"+r+"<<<<");
 //                   System.out.println(r.className());
                    switch (r.className()){
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
                                System.out.println("trying to get mp4 value form "+nv.getUrl());
                                Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                                nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                                videos.add(nv);
                                System.out.println("looped it dupe");

                                break;

                            } catch (MalformedURLException e) {
                                System.out.println("Malformed URL: " + e.getMessage());

                            } catch (IOException e) {
                                System.out.println("I/O Error: " + e.getMessage());

                            }












                            videos.add(nv);

                    }
//                    System.out.println("link"+r.attr("href"));

                   //System.out.println(r.attr("href"));
                }

                System.out.println(results.isEmpty());

                // pull out the responses from the list
                /*

                Elements listing = doc.getElementsByTag("li");
                Video nv=new Video();
                for (Element l : listing) {
                    //                      System.out.println(l);

//                    String hack = l.getElementsByClass("yt-lockup-title ").first().getElementsByTag("a").attr("href");

                    //System.out.println("hack>>>>>>>>>"+hack);
                    Elements titles = l.getElementsByClass("yt-lockup-title ");
                    for (Element t : titles){
                        Elements anchor = t.getElementsByTag("a");
                        if (!anchor.isEmpty()) {
                            nv= new Video("https://www.youtube.com" + anchor.attr("href"));
                            //nv.setUrl("https://www.youtube.com" + anchor.attr("href"));
                            nv.setTitle(anchor.attr("title"));
                            nv.setDescription(anchor.attr("aria-label"));
                            //                        System.out.println(nv);
                            //                        System.out.println();
                        }
                    }

                    //pull out the thumbnail.url
                    Elements thumbs = l.getElementsByClass("yt-thumb-simple");
                    for (Element t : thumbs){
                        //System.out.println(t);
                        for (Element tImg : t.getElementsByTag("img")) {
                            //System.out.println(tImg);
                            thumbnail =tImg.attr("data-thumb");

                        }
                    }
//                    System.out.println("thumbnail:"+tImg.attr("data-thumb"));
                    if ((!thumbnail.isEmpty()) && (nv.getUrl().indexOf("atch")>0)) {
                        System.out.println("adding video " + nv.getUrl());
                        nv.setThumbnail(thumbnail);
                        videos.add(nv);
                        thumbnail = "";
                    }
                }

                */
            }
            catch (MalformedURLException e) {
                System.out.println("Malformed URL: " + e.getMessage());
            }
            catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            }
            return "done";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            System.out.println(videos.size());
        }


        @Override
        protected void onPreExecute() {
            System.out.println("starting to scrape BitChute");
        }


    }




}



