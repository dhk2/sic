package anticlimacticteleservices.sic;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class VideoModel extends ViewModel {
    private ArrayList<Video> videos;

    public ArrayList<Video> getVideos(){
    return this.videos;
    }
    public void setVideo(ArrayList<Video> videos){
        this.videos.clear();
        this.videos.addAll(videos);
    }
    public void addVideo(Video video){
        this.videos.add(video);
    }
    public boolean clearVideos(){
        this.videos.clear();
        return true;
    }

}
