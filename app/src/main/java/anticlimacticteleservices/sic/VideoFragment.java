package anticlimacticteleservices.sic;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
    private List<Video> vfVideos = new ArrayList<>();
    private VideoAdapter vAdapter = new VideoAdapter();
    private RecyclerView videoRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public VideoFragment() {

    }
    public interface VideoFragmentListener {
        void videoFragmentListener();
    }
    public static VideoFragment newInstance(String param1, String param2) {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //System.out.println("video fragment created");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_video, container, false);
        videoRecyclerView = v.findViewById(R.id.vrv);
        swipeRefreshLayout = v.findViewById(R.id.simpleSwipeRefreshLayout);
        if (null != swipeRefreshLayout) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.masterData.getFragmentID().equals("home")) {
                                MainActivity.masterData.setSwipeRefreshLayout(swipeRefreshLayout);
                                MainActivity.masterData.setForceRefresh(true);
                                vfVideos = MainActivity.masterData.getVideoDao().getVideos();
                                new ChannelUpdate().execute();
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }, 10);
                }
            });
        }
        if (null == this.getParentFragment() && (vfVideos.size() == 0)) {
            vfVideos = MainActivity.masterData.getVideoDao().getVideos();
        }
        vAdapter = new VideoAdapter(vfVideos);
        System.out.println("creating new video adaptor with " + vfVideos.size() + " videos");

        RecyclerView.LayoutManager mLayoutManager =null;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        }
        else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }


        videoRecyclerView.setLayoutManager(mLayoutManager);
        videoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoRecyclerView.setAdapter(vAdapter);
        // System.out.println("created view for video fragment");

        return v;
    }

    public void setVideos(List<Video> videos) {
        vfVideos.clear();
        vfVideos.addAll((videos));
        vAdapter.notifyDataSetChanged();
    System.out.println("videos set"+vfVideos.size());
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
