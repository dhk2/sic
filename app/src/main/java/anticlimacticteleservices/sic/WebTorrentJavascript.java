package anticlimacticteleservices.sic;


public class WebTorrentJavascript {
    static String fstatus = "        // Statistics\n" +
            "        function onProgress () {\n" +
            "          // Peers\n" +
            "          $numPeers.innerHTML = torrent.numPeers + (torrent.numPeers === 1 ? ' peer' : ' peers')\n" +
            "\n" +
            "          // Progress\n" +
            "          var percent = Math.round(torrent.progress * 100 * 100) / 100\n" +
            "          $progressBar.style.width = percent + '%'\n" +
            "          $downloaded.innerHTML = prettyBytes(torrent.downloaded)\n" +
            "          $total.innerHTML = prettyBytes(torrent.length)\n" +
            "\n" +
            "          // Remaining time\n" +
            "          var remaining\n" +
            "          if (torrent.done) {\n" +
            "            remaining = 'Done.'\n" +
            "          } else {\n" +
            "            remaining = moment.duration(torrent.timeRemaining / 1000, 'seconds').humanize()\n" +
            "            remaining = remaining[0].toUpperCase() + remaining.substring(1) + ' remaining.'\n" +
            "          }\n" +
            "          $remaining.innerHTML = remaining\n" +
            "\n" +
            "          // Speed rates\n" +
            "          $downloadSpeed.innerHTML = prettyBytes(torrent.downloadSpeed) + '/s'\n" +
            "          $uploadSpeed.innerHTML = prettyBytes(torrent.uploadSpeed) + '/s'\n" +
            "        }\n" +
            "        function onDone () {\n" +
            "          $body.className += ' is-seed'\n" +
            "          onProgress()\n" +
            "        }\n" +
            "      })";
    static String fprettybytes = "      // Human readable bytes util\n"+
            "      function prettyBytes(num) {\n"+
            "        var exponent, unit, neg = num < 0, units = ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']\n"+
            "        if (neg) num = -num\n"+
            "        if (num < 1) return (neg ? '-' : '') + num + ' B'\n"+
            "        exponent = Math.min(Math.floor(Math.log(num) / Math.log(1000)), units.length - 1)\n"+
            "        num = Number((num / Math.pow(1000, exponent)).toFixed(2))\n"+
            "        unit = units[exponent]\n"+
            "        return (neg ? '-' : '') + num + ' ' + unit\n"+
            "      }";
    static String webviewermain = "      var torrentId = '#magnet'\n" +
            "\n" +
            "      var client = new WebTorrent()\n" +
            "\n" +
            "      // HTML elements\n" +
            "      var $body = document.body\n" +
            "      var $progressBar = document.querySelector('#progressBar')\n" +
            "      var $numPeers = document.querySelector('#numPeers')\n" +
            "      var $downloaded = document.querySelector('#downloaded')\n" +
            "      var $total = document.querySelector('#total')\n" +
            "      var $remaining = document.querySelector('#remaining')\n" +
            "      var $uploadSpeed = document.querySelector('#uploadSpeed')\n" +
            "      var $downloadSpeed = document.querySelector('#downloadSpeed')\n" +
            "\n" +
            "      // Download the torrent\n" +
            "      client.add(torrentId, function (torrent) {\n" +
            "\n" +
            "        // Torrents can contain many files. Let's use the .mp4 file\n" +
            "        var file = torrent.files.find(function (file) {\n" +
            "          return file.name.endsWith('.mp4')\n" +
            "        })\n" +
            "\n" +
            "        // Stream the file in the browser\n" +
            "        file.appendTo('#output')\n" +
            "\n" +
            "        // Trigger statistics refresh\n" +
            "        torrent.on('done', onDone)\n" +
            "        setInterval(onProgress, 500)\n" +
            "        onProgress()";
    static String scriptinclude ="    <script src=\"https://cdn.jsdelivr.net/webtorrent/latest/webtorrent.min.js\"></script>\n" +
            "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.js\"></script>";
    static String playerhtml="<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>WebTorrent video player</title>\n" +
            "    <style>\n" +
            "      #output video {\n" +
            "        width: 100%;\n" +
            "      }\n" +
            "      #progressBar {\n" +
            "          height: 5px;\n" +
            "          width: 0%;\n" +
            "          background-color: #35b44f;\n" +
            "          transition: width .4s ease-in-out;\n" +
            "      }\n" +
            "      body.is-seed .show-seed {\n" +
            "          display: inline;\n" +
            "      }\n" +
            "      body.is-seed .show-leech {\n" +
            "          display: none;\n" +
            "      }\n" +
            "      .show-seed {\n" +
            "          display: none;\n" +
            "      }\n" +
            "      #status code {\n" +
            "          font-size: 90%;\n" +
            "          font-weight: 700;\n" +
            "          margin-left: 3px;\n" +
            "          margin-right: 3px;\n" +
            "          border-bottom: 1px dashed rgba(255,255,255,0.3);\n" +
            "      }\n" +
            "\n" +
            "      .is-seed #hero {\n" +
            "          background-color: #154820;\n" +
            "          transition: .5s .5s background-color ease-in-out;\n" +
            "      }\n" +
            "      #hero {\n" +
            "          background-color: #2a3749;\n" +
            "      }\n" +
            "      #status {\n" +
            "          color: #fff;\n" +
            "          font-size: 17px;\n" +
            "          padding: 5px;\n" +
            "      }\n" +
            "      a:link, a:visited {\n" +
            "          color: #30a247;\n" +
            "          text-decoration: none;\n" +
            "      }\n" +
            "    </style>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"hero\">\n" +
            "      <div id=\"output\">\n" +
            "        <div id=\"progressBar\"></div>\n" +
            "        <!-- The video player will be added here -->\n" +
            "      </div>\n" +
            "      <!-- Statistics -->\n" +
            "      <div id=\"status\">\n" +
            "        <div>\n" +
            "          <span class=\"show-leech\">Downloading </span>\n" +
            "          <span class=\"show-seed\">Seeding </span>\n" +
            "          <code>\n" +
            "            <!-- Informative link to the torrent file -->\n" +
            "            #videotitle\n" +
            "          </code>\n" +
            "          <span class=\"show-leech\"> from </span>\n" +
            "          <span class=\"show-seed\"> to </span>\n" +
            "          <code id=\"numPeers\">0 peers</code>.\n" +
            "        </div>\n" +
            "        <div>\n" +
            "          <code id=\"downloaded\"></code>\n" +
            "          of <code id=\"total\"></code>\n" +
            "          — <span id=\"remaining\"></span><br/>\n" +
            "          &#x2198;<code id=\"downloadSpeed\">0 b/s</code>\n" +
            "          / &#x2197;<code id=\"uploadSpeed\">0 b/s</code>\n" +
            "        </div>\n" +
            "      </div>\n" +
            "    </div>";
    static String htmlend="    </script>\n" +
            "  </body>\n" +
            "</html>";


    public static String getWebviewHtml(Video vid){
        String magnet = vid.getMagnet();
        String videoTitle=vid.getTitle();
        String fixed=webviewermain.substring(0,webviewermain.indexOf("#magnet"))+magnet+webviewermain.substring(webviewermain.indexOf("#magnet")+7);
        String fixed2 = playerhtml.substring(0,playerhtml.indexOf("#videotitle"))+videoTitle+playerhtml.substring(playerhtml.indexOf("#videotitle")+11);
        System.out.println(fixed);
        String html =fixed2+scriptinclude+"<script>"+fixed+fstatus+ fprettybytes+htmlend;
        return html;
    }
}
