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
            "      client.add(torrentId,function (torrent) {\n" +
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
            "        Android.debug('setting up interval')\n"+
            "        setInterval(onProgress, 500)\n" +
            "        onProgress()\n";
    static String scriptinclude ="    <script src=\"https://cdn.jsdelivr.net/webtorrent/latest/webtorrent.min.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/web-streams-polyfill@2.0.2/dist/ponyfill.min.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/streamsaver@2.0.3/StreamSaver.min.js\"></script>\n"+
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
            "    </div>\n";
    static String htmlend="    </script>\n" +
            "  </body>\n" +
            "</html>";
    static String htmlstart="<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset=\"UTF-8\">";

    static String fjavastatus="        function onProgress () {\n" +
            "          var remaining\n" +
            "          if (torrent.done) {\n" +
            "            remaining = 'Done.'\n" +
            "          } else {\n" +
            "            remaining = torrent.timeRemaining\n" +
            "          }\n" +
            "          Android.debug('torrent:'+torrent.name+' uploaded:'+torrent.uploaded+' seeds: '+torrent.lenth)\n" +
            "          Android.debug('progress:'+torrent.progress+' time remaining:'+torrent.timeRemaining+' downloaded: '+torrent.downloaded)\n" +
            "        }\n" +
            "        function onDone () {\n" +
        //    "        const fileStream = streamSaver.createWriteStream('/storage/emulated/0/Download/video.mp4')\n" +
"        const fileStream = streamSaver.createWriteStream('/storage/emulated/0/Download/'+torrent.name)\n" +
            "        const writer = fileStream.getWriter()\n" +
            "\t\tconst reader = file.createReadStream()\n" +
            "          const pump = () => reader.read().then(res => res.done\n" +
            "            ? writer.close()\n" +
            "            : writer.write(res.value).then(pump))\n" +
            "\n" +
            "        pump()\n"+
            "          onProgress()\n" +
            "        }\n" +
            "      })\n";
    static String fileWriterFullScript = "\n" +
            "      function size (bytes, precision) {\n" +
            "        if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return '-';\n" +
            "        if (typeof precision === 'undefined') precision = 1;\n" +
            "        var units = ['bytes', 'kiB', 'MiB', 'GiB', 'TiB', 'PiB'],\n" +
            "        number = Math.floor(Math.log(bytes) / Math.log(1024));\n" +
            "        return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];\n" +
            "      }\n" +
            "\n" +
            "      const client = window.client = new WebTorrent()\n" +
            "\n" +
            "      // Sintel, a free, Creative Commons movie\n" +
            "      const torrentId = '#magnet'\n" +
            "\n" +
            "      $start.onclick = () => {\n" +
            "        document.body.innerHTML = '<p id=\"$info\">Downloading Torrent-file metadata</p>'\n" +
            "\n" +
            "        // PS: If you are using insecure sites you better create the writestream with a user interaction event.\n" +
            "        //     with https it doesn't matther.\n" +
            "        window.fileStream = streamSaver.createWriteStream('Sintel.mp4', {\n" +
            "          size: 129241752,\n" +
            "          // writableStrategy: new ByteLengthQueuingStrategy({ highWaterMark: 1024000 }),\n" +
            "          // readableStrategy: new ByteLengthQueuingStrategy({ highWaterMark: 1024000 })\n" +
            "        })\n" +
            "        window.writer = fileStream.getWriter()\n" +
            "\n" +
            "        client.add(torrentId, torrent => {\n" +
            "          $info.remove()\n" +
            "\n" +
            "          const meter = document.createElement('meter')\n" +
            "          const speed = document.createElement('p')\n" +
            "          const downloaded = document.createElement('p')\n" +
            "          const timeLeft = document.createElement('p')\n" +
            "          const writerSize = document.createElement('p')\n" +
            "\n" +
            "          document.body.appendChild(meter)\n" +
            "          document.body.appendChild(speed)\n" +
            "          document.body.appendChild(downloaded)\n" +
            "          document.body.appendChild(timeLeft)\n" +
            "          document.body.appendChild(writerSize)\n" +
            "\n" +
            "          writerSize.innerText = `writer.desiredSize = ${writer.desiredSize}`\n" +
            "\n" +
            "          torrent.on('download', function (bytes) {\n" +
            "            downloaded.innerText =  `total downloaded: ${size(torrent.downloaded)} of ${size(torrent.length)}`\n" +
            "            speed.innerText = 'download speed: ' + size(torrent.downloadSpeed)\n" +
            "            timeLeft.innerText = 'Time Left: ' + (torrent.timeRemaining / 1000).toFixed(0) + ' sec'\n" +
            "            meter.value = torrent.progress\n" +
            "          })\n" +
            "\n" +
            "          const file = torrent.files[5]\n" +
            "\n" +
            "          // Unfortunately we have two different stream protocol so we can't pipe.\n" +
            "          file.createReadStream()\n" +
            "            .on('data', data => {\n" +
            "              writer.write(data).then(() => {\n" +
            "                writerSize.innerText = `writer.desiredSize = ${(writer.desiredSize)}`\n" +
            "              })\n" +
            "              writerSize.innerText = `writer.desiredSize = ${(writer.desiredSize)}`\n" +
            "            })\n" +
            "            .on('end', () => writer.close())\n" +
            "        })\n" +
            "      }\n";



    public static String getWebviewHtml(Video vid){
        String magnet = vid.getMagnet();
        String videoTitle=vid.getTitle();
        String fixed = inject(webviewermain,"#magnet",magnet);
        String fixed2 = inject(playerhtml,"#videotitle",videoTitle);
       // System.out.println(fixed);
        String html =fixed2+scriptinclude+"\n<script>\n"+fixed+fstatus+ fprettybytes+htmlend;
       // fixed = inject(fileWriterFullScript,"#magnet",magnet);
      //  html  = htmlstart+scriptinclude+"\n<script>\n"+fixed+htmlend;
        System.out.println(html);
        return html;
    }
    public static String getWebViewScript(Video vid){
        String magnet = vid.getMagnet();
        String fixed=webviewermain.substring(0,webviewermain.indexOf("#magnet"))+magnet+webviewermain.substring(webviewermain.indexOf("#magnet")+7);
        //String html = htmlstart+scriptinclude+"<script>"+fixed+fjavastatus+htmlend;
        return fixed;

    }
    static String inject(String g,String hashtag,String value ){
        while  (g.indexOf(hashtag)>0){
            g = g.substring(0,g.indexOf(hashtag))+value+g.substring(g.indexOf(hashtag)+hashtag.length());
        }
        return g;
    }
}
