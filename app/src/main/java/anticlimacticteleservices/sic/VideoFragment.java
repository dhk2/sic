package anticlimacticteleservices.sic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
    public List<Video> vfVideos = new ArrayList<>();
    private VideoAdapter vAdapter = new VideoAdapter();
    private RecyclerView videoRecyclerView;
    public VideoFragment() {

    }
    public interface VideoFragmentListener {
        void videoFragmentListener();
    }
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    System.out.println("video fragment created");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        videoRecyclerView =v.findViewById(R.id.vrv);
        vAdapter = new VideoAdapter(vfVideos);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        videoRecyclerView.setLayoutManager(mLayoutManager);
        videoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoRecyclerView.setAdapter(vAdapter);
        System.out.println("created view for video fragment");
        return v;
    }

    public void setVideos(List<Video> videos) {
        vfVideos.clear();
        vfVideos.addAll((videos));
        vAdapter.notifyDataSetChanged();
    System.out.println("videos set");
    }
    /*
    public void addVideos(List<Video> videos) {
        vfVideos.addAll((videos));
        vAdapter.notifyDataSetChanged();
    System.out.println("videos added");
    }
    public void clearVideos(List<Video> videos) {
        vfVideos.clear();
    System.out.println("videos cleared");
    } 
    */
}
