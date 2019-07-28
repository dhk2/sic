package anticlimacticteleservices.sic;

//import android.app.Fragment;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }



    public static SettingsFragment newInstance(String param1, String param2) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button importBitchute = view.findViewById(R.id.load_bitchute);
        importBitchute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.importdialog);
                final WebView webView = (WebView) dialog.findViewById(R.id.idplayer_window);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                });
                webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);


                webView.getSettings().setUseWideViewPort(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.setScrollbarFadingEnabled(false);



                webView.loadUrl("https://www.bitchute.com/subscriptions/");
                Button closeButton = (Button) dialog.findViewById(R.id.idclosebutton);
                closeButton.setText("close");
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.destroy();
                        dialog.dismiss();
                    }
                });
  /*              Button importButton = (Button) dialog.findViewById(R.id.idimportbutton);
                importButton.setText("Import");
                importButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(webView.getTitle());
                    }
                });
   */             dialog.show();
            }
        });
/*        Button importButton = view.findViewById(R.id.load_youtube);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImportSubscriptions is = new ImportSubscriptions();
                is.execute();
            }

        });
  */
        final Button importYoutube = view.findViewById(R.id.load_youtube);
        importYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.importdialog);
                final WebView webView = (WebView) dialog.findViewById(R.id.idplayer_window);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                });
                webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);


                webView.getSettings().setUseWideViewPort(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.setScrollbarFadingEnabled(false);



                webView.loadUrl("https://www.youtube.com/subscription_manager");
                Button closeButton = (Button) dialog.findViewById(R.id.idclosebutton);
                closeButton.setText("close");
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.destroy();
                        dialog.dismiss();
                    }
                });
  /*              Button importButton = (Button) dialog.findViewById(R.id.idimportbutton);
                importButton.setText("Import");
                importButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(webView.getTitle());
                    }
                });
   */             dialog.show();
            }
        });





        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        RadioButton useVlc =view.findViewById(R.id.use_vlc);
        RadioButton useDefault =view.findViewById(R.id.use_default);
        RadioButton useWebview =view.findViewById(R.id.use_webview);
        System.out.println("player choice:"+MainActivity.masterData.getPlayerChoice());
        switch(MainActivity.masterData.getPlayerChoice()){
            case 1:
                useVlc.setChecked(true);
                break;
            case 2:
                useDefault.setChecked(true);
                break;
            case 4:
                useWebview.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.use_vlc:
                        MainActivity.masterData.setPlayerChoice(1);
                        break;
                    case R.id.use_default:
                        MainActivity.masterData.setPlayerChoice(2);
                        break;
                    case R.id.use_webview:
                        MainActivity.masterData.setPlayerChoice(4);
                }
            }
        });

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void handleHtml(String html) {
            Document doc = Jsoup.parse(html);
            System.out.println("["+doc.title()+"]");
            if (doc.title().equals("Subscriptions - BitChute")) {
                System.out.println("made into if bock");
                Elements subscriptions = doc.getElementsByClass("subscription-container");
                System.out.println(subscriptions.size()+" chanels listed");
                for (Element s : subscriptions) {
                   new ChannelInit().execute("https://www.bitchute.com"+s.getElementsByTag("a").first().attr("href"));
                }
            }
            if (doc.title().equals("Subscription manager - YouTube")) {
                Elements channels = doc.getElementsByClass("guide-item yt-uix-sessionlink yt-valign spf-link    ");
                System.out.println(channels.size()+channels.first().toString());
                String url;
                for (Element c : channels){
                  url = "https://www.youtube.com"+c.attr("href");
                    if (url.indexOf("channel/")>0) {
                        new ChannelInit().execute(url);
                    }
                }
            }
        }
    }
}

