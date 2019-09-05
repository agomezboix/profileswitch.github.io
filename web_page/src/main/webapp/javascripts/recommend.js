var recOS;
var recWB;
var recUA;
var recEncoding;
var recLanguage;
var recHeaders;
var recPlug;
var recPlatform;
var recCookies;
var recDNT;
var recTimezone;
var recStorage;
var recWebGLVendor;
var recWebGLRenderer;
var recFonts;
var recAdBlock;
var map;

function parseFP(fp) {
    var atts = fp.substring(1, fp.data.lenght - 1).split("\",\"");
    var values;
    map = new Map();
    for(var v in atts) {
        v = v.replace("\"", "");
        values = v.split(":");
        map.set(values[0], values[1]);
    }
    return map;
}



