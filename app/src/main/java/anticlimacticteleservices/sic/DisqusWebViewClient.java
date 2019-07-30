package anticlimacticteleservices.sic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cooper on 4/29/15.
 */
class DisqusWebViewClient extends WebViewClient {

    private static final String APP_COMMENT_URL = "your-app-url";
    private final Set<String> allowedUrls;
    private String commentsUrl;
    private Context context;

    public DisqusWebViewClient(Context c, String shortName, String pageUrl, String pageTitle, @Nullable String identifier) {
        allowedUrls = new HashSet<>();
        commentsUrl = APP_COMMENT_URL
                + "?shortname=" + Uri.encode(shortName)
                + "&url=" + Uri.encode(pageUrl)
                + "&title=" + Uri.encode(pageTitle);
        context = c;

        if (identifier != null) {
            commentsUrl += "&identifier=" + Uri.encode(identifier);
        }

        allowedUrls.add("disqus.com/next/login-success");
        allowedUrls.add("disqus.com/_ax/google/complete");
        allowedUrls.add("disqus.com/_ax/twitter/complete");
        allowedUrls.add("disqus.com/_ax/facebook/complete");
        allowedUrls.add(commentsUrl);
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (allowedUrls.contains(url)) {
            // The link is a comment URL, handle it in the webview
            return false;
        }
        // Otherwise, launch a real browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
        return true;
    }
}