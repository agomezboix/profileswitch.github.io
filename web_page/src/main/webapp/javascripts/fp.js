var listAvailableFonts = [];
var platform = window.navigator.platform;
var cookieEnabled = window.navigator.cookieEnabled ? "yes" : "no";
var doNotTrack = "";
if (window.navigator.doNotTrack != null && window.navigator.doNotTrack != "unspecified") {
    if (window.navigator.doNotTrack == "1" || window.navigator.doNotTrack == "yes") {
        doNotTrack = "yes";
    } else {
        doNotTrack = "no";
    }
} else {
    doNotTrack = "NC";
}

var timezone = new Date().getTimezoneOffset();
var resolution = window.screen.width + "x" + window.screen.height + "x" + window.screen.colorDepth;
//Enumeration of navigator.plugins or use of Plugin detect
var plugins = "";
//if(PluginDetect.browser.isIE){
if (false) {
    var nbPlugins = 1;
    var pluginsList = ["QuickTime", "Java", "DevalVR", "Flash", "Shockwave",
        "WindowsMediaPlayer", "Silverlight", "VLC", "AdobeReader", "PDFReader",
        "RealPlayer", "PDFjs"];
    PluginDetect.getVersion(".");
    for (i = 0; i < pluginsList.length; i++) {
        var ver = PluginDetect.getVersion(pluginsList[i]);
        if (ver != null) {
            plugins += "Plugin " + nbPlugins + ": " + pluginsList[i] + " " + ver + "; ";
            nbPlugins++;
        }
    }
} else {
    var np = window.navigator.plugins;
    var plist = new Array();
    for (var i = 0; i < np.length; i++) {
        plist[i] = np[i].name + "; ";
        plist[i] += np[i].description + "; ";
        plist[i] += np[i].filename;
        plist[i] += ". ";
    }
    plist.sort();
    for (i = 0; i < np.length; i++)
        plugins += "Plugin " + i + ": " + plist[i];
}

try {
    ieUserData = "";
    oPersistDiv.setAttribute("testStorage", "value remembered");
    oPersistDiv.save("oXMLStore");
    oPersistDiv.setAttribute("testStorage", "overwritten!");
    oPersistDiv.load("oXMLStore");
    if ("value remembered" == (oPersistDiv.getAttribute("testStorage"))) {
        ieUserData = "yes";
    } else {
        ieUserData = "no";
    }
} catch (ex) {
    ieUserData = "no";
}

try {
    localStorage.fp = "test";
    sessionStorage.fp = "test";
} catch (ex) {
}
var domLocalStorage = "";
try {
    if (localStorage.fp == "test") {
        domLocalStorage = "yes";
    } else {
        domLocalStorage = "no";
    }
} catch (ex) {
    domLocalStorage = "no";
}

try {
    domSessionStorage = "";
    if (sessionStorage.fp == "test") {
        domSessionStorage = "yes";
    } else {
        domSessionStorage = "no";
    }
} catch (ex) {
    domSessionStorage = "no";
}

function getPlatform() {
    return platform;
}

function cookiesEnabled() {
    return cookieEnabled;
}

function getResolution() {
    return resolution;
}

function getPlugins() {
    return plugins;
}

function doNotTrack() {
    return doNotTrack;
}

function getTimezone() {
    return timezone;
}

function getLocalStorage() {
    return domLocalStorage;
}

function getSessionStorage() {
    return domSessionStorage;
}

function createSpan(testString, testSize, fontFamily) {
    try {
        var span;
        span = document.createElement("span");
        span.style.position = "absolute";
        span.style.visibility = "hidden";
        span.style.left = "-9999px";
        span.style.fontSize = testSize;
        span.innerHTML = testString;
        span.style.fontFamily = fontFamily;
        return span;
    } catch (e) {
        return undefined;
    }
}

var listAvailableFonts = new Set();
var availableFonts;

function getAvailableFonts() {
    try {
        var baseFonts = ["monospace", "sans-serif", "serif"];
        var fontList = "Andale Mono,AppleGothic,Arial,Arial Black,Arial Hebrew,Arial MT,Arial Narrow,Arial Rounded MT Bold,Arial Unicode MS,Bitstream Vera Sans Mono,Book Antiqua,Bookman Old Style,Calibri,Cambria,Cambria Math,Century,Century Gothic,Century Schoolbook,Comic Sans,Comic Sans MS,Consolas,Courier,Courier New,Garamond,Geneva,Georgia,Helvetica,Helvetica Neue,Impact,Lucida Bright,Lucida Calligraphy,Lucida Console,Lucida Fax,LUCIDA GRANDE,Lucida Handwriting,Lucida Sans,Lucida Sans Typewriter,Lucida Sans Unicode,Microsoft Sans Serif,Monaco,Monotype Corsiva,MS Gothic,MS Outlook,MS PGothic,MS Reference Sans Serif,MS Sans Serif,MS Serif,MYRIAD,MYRIAD PRO,Palatino,Palatino Linotype,Segoe Print,Segoe Script,Segoe UI,Segoe UI Light,Segoe UI Semibold,Segoe UI Symbol,Tahoma,Times,Times New Roman,Times New Roman PS,Trebuchet MS,Verdana,Wingdings,Wingdings 2,Wingdings 3";
        fontList = fontList.split(',');
        var testString = "femwlalwmef";
        var size = "96px";
        var fontFamily = document.createElement("div");
        var fonts = document.createElement("div");
        var width = {};
        var height = {};

        var baseFontsSpans = [];
        for (i = 0; i < baseFonts.length; i++) {
            span = createSpan(testString, size, baseFonts[i]);
            fontFamily.appendChild(span);
            baseFontsSpans.push(span);
        }

        document.body.appendChild(fontFamily);

        for (i = 0; i < baseFonts.length; i++) {
            width[baseFonts[i]] = baseFontsSpans[i].offsetWidth;
            height[baseFonts[i]] = baseFontsSpans[i].offsetHeight;
        }

        var fontsSpans = {};

        for (i = 0; i < fontList.length; i++) {
            fontSpans = [];
            for (j = 0; j < baseFonts.length; j++) {
                span = createSpan(testString, size, "'" + fontList[i] + "'," + baseFonts[j]);
                fonts.appendChild(span);
                fontSpans.push(span);
            }
            fontsSpans[fontList[i]] = fontSpans;
        }

        document.body.appendChild(fonts);
        var available = '';

        for (i = 0; i < fontList.length; i++) {
            for (j = 0; j < baseFonts.length; j++) {
                detected = (fontsSpans[fontList[i]][j].offsetWidth !== width[baseFonts[j]] || fontsSpans[fontList[i]][j].offsetHeight !== height[baseFonts[j]]);
                available += (detected ? "t" : "f");
                if (detected) {
                    listAvailableFonts.add(fontList[i]);
                }
            }
        }

        document.body.removeChild(fonts);
        document.body.removeChild(fontFamily);
        availableFonts = available;

    } catch (e) {
        availableFonts = undefined;
        listAvailableFonts = undefined;
    }
    return availableFonts;
}


function listFonts() {
    var list = "";
    var item;
    for (item of listAvailableFonts.values()) {
        list += item + ',';
    }
    return list.substring(0, list.length - 1);
}