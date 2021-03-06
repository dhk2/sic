package anticlimacticteleservices.sic;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class ChannelInit extends AsyncTask <String,String,Integer>{
    private final SimpleDateFormat bdf = new SimpleDateFormat("EEE', 'dd MMM yyyy HH:mm:SS' 'ZZZZ");
    private final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Channel chan;
    private int newVideoCount=0;
    private int dupeCount=0;
    private int newChannelCount=0;
    private boolean bitchuteError,youtubeError,failed;
    @Override
    protected Integer doInBackground(String[] params) {
    Log.v("Channel-Init",MainActivity.masterData.getChannels().size()+" channels, init channel"+params[0]);
        Document channelPage;
        Document channelRss;

        channels:for (String g : params) {
           chan = new Channel(g);
           bitchuteError=false;
           youtubeError=false;
           int channelVideoCount =0;
            Log.v("Channel-Init","trying to add channel:"+g);
           for (Channel c : MainActivity.masterData.getChannels()){
               if (chan.getYoutubeID().equals(c.getYoutubeID()) && !c.getYoutubeID().isEmpty() ){
                   dupeCount++;
                   Log.v("Channel-Init","trying to add duplicate youtube channel "+chan.getTitle());
                   continue channels;
               }
               if (chan.getBitchuteID() == c.getBitchuteID() && !c.getBitchuteID().isEmpty()){
                   dupeCount++;
                   Log.v("Channel-Init","trying to add duplicate bitchute channel "+chan.getTitle());
                   continue channels;
               }
           }
           try {
                chan = new Channel(g);
                channelRss=null;
                channelPage=null;
                if (chan.isBitchute()){
                    channelPage = Jsoup.connect(chan.getBitchuteUrl()).get();
                //bitchute rss feeds don't work with the channel UID but only with the text name, need to get text name before parsing rss
                    chan = new Channel("https://www.bitchute.com" + channelPage.getElementsByClass("name").first().getElementsByTag("a").first().attr("href"));
                    channelRss= Jsoup.connect(chan.getBitchuteRssFeedUrl()).get();
                }
                if (chan.isYoutube()) {
                    channelRss = Jsoup.connect(chan.getYoutubeRssFeedUrl()).get();
                    channelPage= Jsoup.connect(chan.getYoutubeUrl()).get();
                }
                if (null == channelRss){
                    failed=true;
                    return null;
                }
                chan.setTitle(channelRss.title());
               Log.v("Channel-Init","creating channel :"+chan.toCompactString());
               if (chan.isBitchute()) {
                   try {
                       chan.setDescription(channelRss.getElementsByTag("description").first().text());
                       chan.setThumbnail(channelPage.getElementsByAttribute("data-src").last().attr("data-src"));
                       MainActivity.masterData.addChannel(chan);
                       chan = MainActivity.masterData.getChannelDao().getChannelsBySourceID(chan.getSourceID()).get(0);
                       Elements videos = channelRss.getElementsByTag("item");
                       Log.v("Channel-Init","attempting to add "+videos.size()+ " videos to bitchute channel "+ chan.toCompactString());
                       for (Element video : videos) {
                           Video nv=new Video(video.getElementsByTag("link").first().text());
                           nv.setTitle(video.getElementsByTag("title").first().text());
                           nv.setDescription(video.getElementsByTag("description").first().text());
                           // System.out.println(nv);
                           nv.setThumbnail(video.getElementsByTag("enclosure").first().attr("url"));
                           Date pd=new Date(1);
                           try {
                               pd = bdf.parse(video.getElementsByTag("pubDate").first().text());
                               nv.setDate(pd);
                           } catch (ParseException ex) {
                               Log.v("Exception", ex.getLocalizedMessage());
                           }
                           //TODO put in exception for archived channels here when implemented
                           if (pd.getTime()+(MainActivity.masterData.getFeedAge()*24*60*60*1000)<new Date().getTime()) {
                               System.out.println("out of feed range for " + chan.getTitle());
                               break;
                           }
                           nv.setAuthor(channelRss.title());
                           nv.setAuthorID(chan.getID());
                           //System.out.println(nv);
                           List matches = MainActivity.masterData.getVideoDao().getVideosBySourceID(nv.getSourceID());
                           if (matches.isEmpty()) {
                               MainActivity.masterData.addVideo(nv);
                               newVideoCount++;
                               channelVideoCount++;
                           }
                           else {
                               Video match = (Video) matches.get(0);
                               if (match.getBitchuteID().isEmpty()) {
                                   Log.v("User-Data", "adding bitchute id to video");
                                   match.setBitchuteID(nv.getSourceID());
                                   MainActivity.masterData.updateVideo(match);
                               }
                               if (match.getAuthorID() > 0) {
                                   Channel tester = MainActivity.masterData.getChannelDao().getChannelById(match.getAuthorID());
                                   if (tester.getBitchuteID().isEmpty()) {
                                       tester.setBitchuteID(chan.getBitchuteID());
                                       Log.v("User-Data", "adding bitchute ID to existing channel " + tester.toCompactString());
                                       MainActivity.masterData.updateChannel(tester);
                                   }
                               }
                           }
                       }
                   } catch (NullPointerException e) {
                       Log.e("channel-init","null pointer error trying to parse youtube rss feed"+e);
                       e.printStackTrace();
                   }
               }
               if (chan.isYoutube()) {
                   chan.setTitle(channelRss.title());
                   chan.setAuthor(channelRss.getElementsByTag("name").first().text());
                   chan.setDescription(channelPage.getElementsByAttributeValue("name", "description").attr("content"));
                   chan.setThumbnail(channelPage.getElementsByAttributeValue("itemprop", "thumbnailUrl").attr("href"));
                   MainActivity.masterData.addChannel(chan);
                   //TODO something about more than one hit in the table
                   chan = MainActivity.masterData.getChannelDao().getChannelsBySourceID(chan.getSourceID()).get(0);
                   Log.v("Channel-Init", "attempting to add videos to youtube channel " + chan.toCompactString());
                   Elements entries = channelRss.getElementsByTag("entry");
                   Date pd = new Date(1);
                   for (Element entry : entries) {
                       try {
                           pd = ydf.parse(entry.getElementsByTag("published").first().text());
                       } catch (ParseException ex) {
                           Log.e("Exception parsing date", ex.getLocalizedMessage());
                           System.out.println(entry);
                       }
                       if ((pd.getTime() + (MainActivity.masterData.getFeedAge() * 24 * 60 * 60 * 1000) < new Date().getTime())) {
                           if (channelVideoCount > 1) {
                               System.out.println("out of feed range for " + chan.getTitle() + MainActivity.masterData.getFeedAge());
                               break;
                           }
                       }
                       Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                      // if (channelVideoCount <= 1) {
                      //     nv.setKeep(true);
                      // }
                       nv.setDate(pd);
                       nv.setAuthor(chan.getAuthor());
                       nv.setAuthorID(chan.getID());
                       nv.setTitle(entry.getElementsByTag("title").first().html());
                       nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
                       nv.setDescription(entry.getElementsByTag("media:description").first().text());
                       nv.setRating(entry.getElementsByTag("media:starRating").first().attr("average"));
                       nv.setViewCount(entry.getElementsByTag("media:statistics").first().attr("views"));
                       if (chan.isBitchute()) {
                           nv.setBitchuteID(nv.getSourceID());
                       }
                       List matches = MainActivity.masterData.getVideoDao().getVideosBySourceID(nv.getSourceID());
                       if (matches.isEmpty()) {
                           MainActivity.masterData.addVideo(nv);
                           newVideoCount++;
                           channelVideoCount++;
                       } else {
                           dupeCount++;
                           Video match = (Video) matches.get(0);
                           if (match.getAuthorID() > 0) {
                               Channel tester = MainActivity.masterData.getChannelDao().getChannelById(match.getAuthorID());
                               //setting youtube author id on existing channel and deleting this duplicate channel
                               if (tester.getYoutubeID().isEmpty()) {
                                   tester.setYoutubeID(chan.getYoutubeID());
                                   MainActivity.masterData.getChannelDao().update(tester);
                                   MainActivity.masterData.removeChannel(chan);
                                   continue channels;
                               }

                           }
                       }
                       if (chan.getBitchuteID().isEmpty() && !bitchuteError) {
                           String testID = "";
                           try {
                               Document doctest = Jsoup.connect(nv.getBitchuteTestUrl()).get();
                               //System.out.println(doctest);
                               System.out.println(("looking for bitchute version of " + nv.getTitle() + " from " + nv.getAuthor() + " results in " + doctest.title()));
                               nv.setBitchuteID(nv.getSourceID());
                               System.out.println("need to set bitchute id for channel imported from youtube");
                               testID = doctest.getElementsByClass("image-container").first().getElementsByTag("a").first().attr("href");
                               System.out.println(testID.length() + ">" + testID);
                               testID = testID.substring(0, testID.length() - 1);
                               System.out.println(testID.length() + ">" + testID);
                               testID = testID.substring(testID.lastIndexOf("/") + 1);
                               System.out.println(testID.length() + ">" + testID);
                               chan.setBitchuteID(testID);
                               Log.v("channel-init", "attempting to update youtube channel with bitchute source id :" + chan.toCompactString());
                               MainActivity.masterData.updateChannel(chan);
                           } catch (IOException e) {
                               e.printStackTrace();
                               Log.e("channel-init", "unable to load bitchute version of youtube video");
                               bitchuteError = true;
                           }
                       }
                   }
               }
            } catch (IOException e) {
                e.printStackTrace();
            } catch ( NullPointerException e){
               e.printStackTrace();
           }
            MainActivity.masterData.sortVideos();
        }
        return 69;
    }
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (failed &&!MainActivity.masterData.isMuteErrors()){
            Toast.makeText(MainActivity.masterData.context,"Unable to find usable channel link in paste buffer",Toast.LENGTH_SHORT).show();
        }
        if (newChannelCount ==1){
            Toast.makeText(MainActivity.masterData.context,"added "+chan.getTitle()+ " with "+newVideoCount+" videos",Toast.LENGTH_SHORT).show();
        }
        if (newChannelCount>1){
            Toast.makeText(MainActivity.masterData.context,"added "+newChannelCount+ " channels with "+newVideoCount+" videos",Toast.LENGTH_SHORT).show();
        }
    }
}
