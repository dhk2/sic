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
import android.widget.Toast;

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
    Dialog dialogHandle;
    WebView webviewHandle;
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
        final Button importYoutube = view.findViewById(R.id.load_youtube);
        importBitchute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.importdialog);
                final WebView webView = dialog.findViewById(R.id.idplayer_window);
                dialogHandle=dialog;
                webviewHandle = webView;
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
                Button closeButton = dialog.findViewById(R.id.idclosebutton);
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
        importYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.importdialog);
                final WebView webView = dialog.findViewById(R.id.idplayer_window);
                dialogHandle=dialog;
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
                Button closeButton = dialog.findViewById(R.id.idclosebutton);
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
        RadioGroup youtubeRadioGroup = view.findViewById(R.id.youtubeplayerradioGroup);
        RadioButton youtubeUseVlc =view.findViewById(R.id.youtubeuse_vlc);
        RadioButton youtubeUseDefault =view.findViewById(R.id.youtubeuse_default);
        RadioButton youtubeUseWebview =view.findViewById(R.id.youtubeuse_webview);

        RadioGroup bitchuteRadioGroup = view.findViewById(R.id.bitchuteplayerradioGroup);
        RadioButton bitchuteUseVlc =view.findViewById(R.id.bitchuteuse_vlc);
        RadioButton bitchuteUseDefault =view.findViewById(R.id.bitchuteuse_default);
        RadioButton bitchuteUseWebview =view.findViewById(R.id.bitchuteuse_webview);
        RadioButton bitchuteUseNative = view.findViewById(R.id.bitchuteuse_native);
        System.out.println("player choice:"+MainActivity.masterData.getYoutubePlayerChoice()+"  "+MainActivity.masterData.getBitchutePlayerChoice());
        switch(MainActivity.masterData.getYoutubePlayerChoice()){
            case 1:
                youtubeUseVlc.setChecked(true);
                break;
            case 2:
                youtubeUseDefault.setChecked(true);
                break;
            case 4:
                youtubeUseWebview.setChecked(true);
                break;
        }
        youtubeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                System.out.println(checkedId);
                switch(checkedId) {
                    case R.id.youtubeuse_vlc:
                        MainActivity.masterData.setYoutubePlayerChoice(1);
                        System.out.println(("setting vlc"));
                        break;
                    case R.id.youtubeuse_default:
                        MainActivity.masterData.setYoutubePlayerChoice(2);
                        System.out.println(("setting default"));
                        break;
                    case R.id.youtubeuse_webview:
                        MainActivity.masterData.setYoutubePlayerChoice(4);
                        System.out.println(("setting webview"));
                }
                System.out.println("use yt vlc:"+MainActivity.masterData.youtubeUseVlc());
                System.out.println("use yt default:"+MainActivity.masterData.youtubeUseDefault());
                System.out.println("use yt webview:"+MainActivity.masterData.youtubeUseWebView());
            }
        });
        switch(MainActivity.masterData.getBitchutePlayerChoice()){
            case 1:
                bitchuteUseVlc.setChecked(true);
                System.out.println(("setting vlc"));
                break;
            case 2:
                bitchuteUseDefault.setChecked(true);
                System.out.println(("setting default"));
                break;
            case 4:
                bitchuteUseWebview.setChecked(true);
                break;
            case 8:
                bitchuteUseNative.setChecked(true);
                break;
        }
        bitchuteRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.bitchuteuse_vlc:
                        MainActivity.masterData.setBitchutePlayerChoice(1);
                        break;
                    case R.id.bitchuteuse_default:
                        MainActivity.masterData.setBitchutePlayerChoice(2);
                        break;
                    case R.id.bitchuteuse_webview:
                        MainActivity.masterData.setBitchutePlayerChoice(4);
                        break;
                    case R.id.bitchuteuse_native:
                        MainActivity.masterData.setBitchutePlayerChoice(8);
                        break;
                }
                System.out.println("playersetting "+MainActivity.masterData.getBitchutePlayerChoice());
                System.out.println("use bitchute vlc:"+MainActivity.masterData.bitchuteUseVlc());
                System.out.println("use bitchute default:"+MainActivity.masterData.bitchuteUseDefault());
                System.out.println("use bitchute webview:"+MainActivity.masterData.bitchuteUseWebView());
                System.out.println("use bitchute built in player:"+MainActivity.masterData.bitchuteUseNative());

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
                System.out.println(subscriptions.size()+" channels listed");
                for (Element s : subscriptions) {
                   new ChannelInit().execute("https://www.bitchute.com"+s.getElementsByTag("a").first().attr("href"));
                }
//                webviewHandle.destroy();
                dialogHandle.dismiss();
                Toast.makeText(MainActivity.masterData.context,"adding "+subscriptions.size()+ " channels from bitchute.",Toast.LENGTH_SHORT).show();
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
  //              webviewHandle.destroy();
                dialogHandle.dismiss();
                Toast.makeText(MainActivity.masterData.context,"adding "+channels.size()+ " channels from youtube.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

