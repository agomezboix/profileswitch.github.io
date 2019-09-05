
<html>
    <head>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        <script type="text/javascript" src="javascripts\fp.js"></script>
        <script type="text/javascript" src="javascripts\fpMore.js"></script>
        <script type="text/javascript" src="javascripts\canvas.js"></script>
        <script type="text/javascript" src="javascripts\webGL.js"></script>
        <script type="text/javascript" src="javascripts\ua-parser.js"></script>
        <script type="text/javascript" src="javascripts\recommend.js"></script>
        <script>
            <% java.util.Enumeration enumeration = request.getHeaderNames();
                String headers = "";
                while (enumeration.hasMoreElements()) {
                    headers += (String) enumeration.nextElement() + " ";
                }
                headers = headers.substring(0, headers.length() - 1);
                String accept = request.getHeader("Accept");
                String encoding = request.getHeader("Accept-Encoding");
                String language = request.getHeader("Accept-Language");
            %>

            var headers = "<%=headers%>";
            var accept = "<%=accept%>";
            var encoding = "<%=encoding%>";
            var language = "<%=language%>";
            var adBlock = document.getElementById('ads') ? 'no' : 'yes';
            var useragent = navigator.userAgent;

            var parser = new UAParser();
            parser.setUA(useragent);
            var os = parser.getOS();
            var browser = parser.getBrowser();


            function displayCanvas() {
                var tableRef = document.getElementById("fptable");
                var newRow = tableRef.insertRow(-1);

                let attCell = newRow.insertCell(0);
                let attText = document.createTextNode('Canvas');
                attCell.appendChild(attText);

                var canvasCell = newRow.insertCell(1);
                var img = new Image();
                img.src = getCanvasData();
                canvasCell.appendChild(img);

            }

            function displayBCOMCanvas() {
                var tableRef = document.getElementById("fptable");
                var newRow = tableRef.insertRow(-1);

                let attCell = newRow.insertCell(0);
                let attText = document.createTextNode('Canvas');
                attCell.appendChild(attText);

                var canvasCell = newRow.insertCell(1);
                var img = new Image();
                img.src = BCOMCanvas;
                canvasCell.appendChild(img);

            }
            function displayBCOMCanvas1() {
                var tableRef = document.getElementById("fptable");
                var newRow = tableRef.insertRow(-1);

                let attCell = newRow.insertCell(0);
                let attText = document.createTextNode('Canvas');
                attCell.appendChild(attText);

                var canvasCell = newRow.insertCell(1);
                var img = new Image();
                img.src = BCOMCanvas1;
                canvasCell.appendChild(img);

            }

            function displayBCOMCanvas2() {
                var tableRef = document.getElementById("fptable");
                var newRow = tableRef.insertRow(-1);

                let attCell = newRow.insertCell(0);
                let attText = document.createTextNode('Canvas');
                attCell.appendChild(attText);

                var canvasCell = newRow.insertCell(1);
                var img = new Image();
                img.src = BCOMCanvas2;
                canvasCell.appendChild(img);

            }

            function addRow(att, value) {
                // Get a reference to the table
                let tableRef = document.getElementById("fptable");
                // Insert a row at the end of the table
                let newRow = tableRef.insertRow(-1);
                // Insert a cell in the row at index 0
                let attCell = newRow.insertCell(0);
                let valCell = newRow.insertCell(1);
                // Append a text node to the cell
                let attText = document.createTextNode(att);
                let valText = document.createTextNode(value);
                attCell.appendChild(attText);
                valCell.appendChild(valText);
            }

            function create_table() {
                addRow('OS', os.name + " " + os.version);
                addRow('Web browser', browser.name + " " + browser.version);
                addRow('UserAgent', useragent);
                addRow('Accept', accept);
                addRow('Content encoding', encoding);
                addRow('Content language', language);
                addRow('List of HTTP headers', headers);
                addRow('List of Plugins', getPlugins());
                addRow('Platform', getPlatform());
                addRow('Cookies enabled', cookieEnabled);
                addRow('Do Not Track', doNotTrack);
                addRow('Timezone', getTimezone());
                addRow('Screen resolution', getResolution());
                addRow('Use of local storage', getLocalStorage());
                addRow('Use of session storage', getSessionStorage());
                displayCanvas();
                addRow('WebGL Vendor', getWebGLVendor());
                addRow('WebGL Renderer', getWebGLRenderer());
                addRow('Available Fonts', getAvailableFonts() + '\n' + listFonts());
                addRow('Use of AdBlock', adBlock);
            }

            function addRecRow(att, value, rec) {
                // Get a reference to the table
                let tableRef = document.getElementById("fptable");
                // Insert a row at the end of the table
                let newRow = tableRef.insertRow(-1);
                // Insert a cell in the row at index 0
                let attCell = newRow.insertCell(0);
                let valCell = newRow.insertCell(1);
                let recCell = newRow.insertCell(2);
                // Append a text node to the cell
                let attText = document.createTextNode(att);
                let valText = document.createTextNode(value);
                let recText = document.createTextNode(rec);
                attCell.appendChild(attText);
                valCell.appendChild(valText);
                recCell.appendChild(recText);
            }

            function parseFP(fp) {
                var atts = fp.replace("{", "").replace("}", "").split("\",\"");
                var values;
                map = new Map();
                var it = atts.values();
                for (let v of it) {
                    values = v.split("\":\"");
                    map.set(values[0].replace("\"", ""), values[1].replace("\"", ""));
                }
                return map;
            }
            var dnt;
            var locals;
            var adB;
            var lang;
            function create_recommendation_table(obj) {

                var map = parseFP(obj.data);
                for (var [key, value] of map) {
                    console.log(key + " = " + value);
                }

                var firstChar = map.get('dntJs').charAt(0);
                console.log(firstChar);
                if (firstChar == "1" || firstChar == "y" || firstChar == "Y" || firstChar == "t" || firstChar == "T") {
                    dnt = "yes";
                } else {
                    dnt = "no";
                }
                firstChar = map.get('localJs').charAt(0);

                if (firstChar == 't' || firstChar == 'T') {
                    locals = "yes";
                } else {
                    locals = "no";
                }
                firstChar = map.get('adBlock').charAt(0);

                if (firstChar == 't' || firstChar == 'T') {
                    adB = "yes";
                } else {
                    adB = "no";
                }
                lang = map.get('languageHttp');
                while (lang.includes("\\u003d")) {
                    lang = lang.replace("\\u003d", "=");
                }

                addRecRow('OS', os.name + " " + os.version, map.get('os'));
                addRecRow('Web browser', browser.name + " " + browser.version, map.get('browser').split('_')[0]);
                addRecRow('UserAgent', useragent, map.get('userAgentHttp'));
                addRecRow('Accept', accept, map.get('acceptHttp'));
                addRecRow('Content encoding', encoding, map.get('encodingHttp'));
                addRecRow('Content language', language, lang);
                addRecRow('List of HTTP headers', headers, map.get('orderHttp'));
                addRecRow('List of Plugins', getPlugins(), map.get('pluginsJs'));
                addRecRow('Platform', getPlatform(), map.get('platformJs'));
                addRecRow('Cookies enabled', cookieEnabled, map.get('cookiesJs'));
                addRecRow('Do Not Track', doNotTrack, dnt);
                addRecRow('Timezone', getTimezone(), map.get('timezoneJs'));
                addRecRow('Screen resolution', getResolution(), map.get('resolutionJs').split("@")[0] + "; Maximized = " + window.screen.width + "x" + window.screen.height);
                addRecRow('Use of local storage', getLocalStorage(), locals);
                addRecRow('Use of session storage', getSessionStorage(), locals);
                displayCanvas();
                addRecRow('WebGL Vendor', getWebGLVendor(), map.get('vendorWebGljs'));
                addRecRow('WebGL Renderer', getWebGLRenderer(), map.get('rendererWebGljs'));
                addRecRow('Available Fonts', getListFonts() + '\n' + getAvailableFonts(), getListFontsByName(map.get('fontsJs')) + '\n' + map.get('fontsJs'));
                addRecRow('Use of AdBlock', adBlock, adB);
            }

            function getListFontsByName(list) {
                var fontsName = "Andale Mono,AppleGothic,Arial,Arial Black,Arial Hebrew,Arial MT,Arial Narrow,Arial Rounded MT Bold,Arial Unicode MS,Bitstream Vera Sans Mono,Book Antiqua,Bookman Old Style,Calibri,Cambria,Cambria Math,Century,Century Gothic,Century Schoolbook,Comic Sans,Comic Sans MS,Consolas,Courier,Courier New,Garamond,Geneva,Georgia,Helvetica,Helvetica Neue,Impact,Lucida Bright,Lucida Calligraphy,Lucida Console,Lucida Fax,LUCIDA GRANDE,Lucida Handwriting,Lucida Sans,Lucida Sans Typewriter,Lucida Sans Unicode,Microsoft Sans Serif,Monaco,Monotype Corsiva,MS Gothic,MS Outlook,MS PGothic,MS Reference Sans Serif,MS Sans Serif,MS Serif,MYRIAD,MYRIAD PRO,Palatino,Palatino Linotype,Segoe Print,Segoe Script,Segoe UI,Segoe UI Light,Segoe UI Semibold,Segoe UI Symbol,Tahoma,Times,Times New Roman,Times New Roman PS,Trebuchet MS,Verdana,Wingdings,Wingdings 2,Wingdings 3".split(",");
                var index = 1;
                var f = "";
                for (var i = 0; i < list.length; i += 3) {
                    var st = list.substring(i, i + 3);
                    if (st.includes("t")) {
                        if (index < fontsName.length) {
                            f += fontsName[index] + ", ";
                        }
                    }
                    index = index + 1;
                }
                return f;
            }
            function autoClick() {
                document.getElementById('linkToClick').click();
            }

            var urlA = window.location.href;
            var urlA = urlA.substring(urlA.indexOf(":"), urlA.lastIndexOf("/"));
            console.log("ws" + urlA + "/mitigation");
            ws = new WebSocket("ws" + urlA + "/mitigation");
            // ws = new WebSocket("ws://localhost:8084/fp/mitigation");
            ws.onmessage = function (evt) {
                console.log("received over websockets: " + evt.data);
                var table = document.getElementById("fptable");
                for (var i = table.rows.length - 1; i > 0; i--)
                {
                    table.deleteRow(i);
                }
                create_recommendation_table(evt);
                document.getElementById("dwn-rec").disabled = false;
            };
            ws.onerror = function (evt) {
                console.log(ws);
                console.log("received errors");
            };
            function send() {
                ws.send(JSON.stringify({id: "empty",
                    os: os.name,
                    browser: browser.name,
                    platformJs: getPlatform(),
                    dntJs: doNotTrack,
                    timezoneJs: getTimezone(),
                    resolutionJs: getResolution(),
                    pluginsJs: getPlugins(),
                    localJs: getLocalStorage(),
                    sessionJs: getSessionStorage(),
                    adBlock: adBlock,
                    vendorWebGljs: getWebGLVendor(),
                    rendererWebGljs: getWebGLRenderer(),
                    fontsJs: getAvailableFonts(),
                    canvasJs: "empty",
                    acceptHttp: accept,
                    encodingHttp: encoding,
                    languageHttp: language,
                    userAgentHttp: useragent,
                    orderHttp: headers,
                    cookiesJs: cookiesEnabled()}));
                console.log("Sending info");

            }

            function download(filename, text) {
                var element = document.createElement('a');
                element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
                element.setAttribute('download', filename);
                element.style.display = 'none';
                document.body.appendChild(element);
                element.click();
                document.body.removeChild(element);
            }
        </script>
    </head>
    <style>
        body {
            padding: 10px;

        }
        #toolbar {
            margin: 10px;
        }
        table {
            width: 80%;
        }

    </style>
    <body onload="create_table()" >
        <div id="toolbar">
            <button class="btn btn-primary" onclick="send()">What do you recommend me?</button> 
            <button class="btn btn-primary" id="dwn-btn">Download my fingerprint</button>
            <button class="btn btn-primary" disabled id="dwn-rec">Download recommended fingerprint</button>
        </div>

        <script>
            document.getElementById("dwn-btn").addEventListener("click", function () {
                // Generate download of hello.txt file with some content
                var text = "id: e\n" +
                        "os:" + os.name + " " + os.version + "\n" +
                        "browser:" + browser.name + "\n" +
                        "platformJs:" + getPlatform() + "\n" +
                        "dntJs:" + doNotTrack + "\n" +
                        "timezoneJs:" + getTimezone() + "\n" +
                        "resolutionJs:" + getResolution() + "\n" +
                        "pluginsJs:" + getPlugins() + "\n" +
                        "localJs:" + getLocalStorage() + "\n" +
                        "sessionJs:" + getSessionStorage() + "\n" +
                        "adBlock:" + adBlock + "\n" +
                        "vendorWebGljs:" + getWebGLVendor() + "\n" +
                        "rendererWebGljs:" + getWebGLRenderer() + "\n" +
                        "fontsJs:" + getAvailableFonts() + "\n" +
                        "canvasJs:" + getCanvasData() + "\n" +
                        "acceptHttp:" + accept + "\n" +
                        "encodingHttp:" + encoding + "\n" +
                        "languageHttp:" + language + "\n" +
                        "userAgentHttp:" + useragent + "\n" +
                        "orderHttp:" + headers + "\n" +
                        "cookiesJs:" + cookieEnabled + "";
                download("myfp", text);
            }, false);

            document.getElementById("dwn-rec").addEventListener("click", function () {
                // Generate download of hello.txt file with some content
                var text = "id:e\n" +
                        "os:" + map.get('os') + "\n" +
                        "browser:" + map.get('browser') + "\n" +
                        "platformJs:" + map.get('platformJs') + "\n" +
                        "dntJs:" + dnt + "\n" +
                        "timezoneJs:" + map.get('timezoneJs') + "\n" +
                        "resolutionJs:" + map.get('resolutionJs') + "\n" +
                        "pluginsJs:" + map.get('pluginsJs') + "\n" +
                        "localJs:" + locals + "\n" +
                        "sessionJs:" + locals + "\n" +
                        "adBlock:" + adB + "\n" +
                        "vendorWebGljs:" + map.get('vendorWebGljs') + "\n" +
                        "rendererWebGljs:" + map.get('rendererWebGljs') + "\n" +
                        "fontsJs:" + map.get('fontsJs') + "\n" +
                        "canvasJs:" + map.get('canvasJs') + "\n" +
                        "acceptHttp:" + map.get('acceptHttp') + "\n" +
                        "encodingHttp:" + map.get('encodingHttp') + "\n" +
                        "languageHttp:" + lang + "\n" +
                        "userAgentHttp:" + map.get('userAgentHttp') + "\n" +
                        "orderHttp:" + map.get('orderHttp') + "\n" +
                        "cookiesJs:" + map.get('cookiesJs') + "";
                download("fp", text);
            }, false);
        </script>
        <div class="alert alert-warning container" role="alert">
            Our database is obsolete, last update in July 2017. 
        </div>
        <table id="fptable" class="table">
            <!--            <col width="130">-->
            <tr class="thead-dark">
                <th>Attribute</th><th>Value</th><th>Recommended Value</th>
            </tr>
        </table>

    </body>
</html>