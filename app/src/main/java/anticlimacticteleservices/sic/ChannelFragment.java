package anticlimacticteleservices.sic;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
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

public class ChannelFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private List<Channel> cfChannels = new ArrayList<>();
    private RecyclerView channelRecyclerView;
    private ChannelAdapter cAdapter = new ChannelAdapter();

    public ChannelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChannelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelFragment newInstance(String param1, String param2) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // System.out.println("cf created");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel, container, false);
        channelRecyclerView =v.findViewById(R.id.crv);
        if (cfChannels.size()==0) {
            cfChannels = MainActivity.masterData.getChannelDao().getChannels();
        }
   //     System.out.println("about to set cAdaptor");
        cAdapter = new ChannelAdapter(cfChannels);

        RecyclerView.LayoutManager mLayoutManager =null;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        }
        else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
        channelRecyclerView.setLayoutManager(mLayoutManager);
        channelRecyclerView.setItemAnimator(new DefaultItemAnimator());
        channelRecyclerView.setAdapter(cAdapter);
     //   System.out.println("created view for channel fragment");
        return v;
    }
    public void setChannels(List<Channel> channels) {
        cfChannels.clear();
        cfChannels.addAll(channels);
        cAdapter.notifyDataSetChanged();
        System.out.println("channels set "+cfChannels.size());
    }
    public void addChannels(List<Channel> channels) {
        cfChannels.addAll(channels);
        cAdapter.notifyDataSetChanged();
        System.out.println("Channels added "+cfChannels.size());
    }
    public void addChannel(Channel newChannel){
        cfChannels.add(newChannel);
        cAdapter.notifyDataSetChanged();
    }
    public void clearChannels() {
        cfChannels.clear();
     //   System.out.println("Channels cleared");
    }
}
