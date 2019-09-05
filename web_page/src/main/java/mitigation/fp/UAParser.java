package mitigation.fp;

import java.io.IOException;
import ua_parser.Client;
import ua_parser.Parser;

public class UAParser {

    boolean processed;
    String os, osVersion, browser, browserVersion, ua;

    public UAParser(String ua) {
        this.ua = ua;
        processed = false;
        os = osVersion = browser = "";
    }

    public String getOS() {
        if (!processed) {
            parse(ua);
        }
        return os;
    }

    public String getOSVersion() {
        if (!processed) {
            parse(ua);
        }
        return osVersion;
    }

    public String getBrowser() {
        if (!processed) {
            parse(ua);
        }
        return browser;
    }

    public String getBrowserVersion() {
        if (!processed) {
            parse(ua);
        }
        return browserVersion;
    }

    private void parse(String ua) {
        Parser uaparser;
        try {
            uaparser = new Parser();
            Client c = uaparser.parse(ua);
            browser = c.userAgent.family;

            // filename=os_osVersion-wb
            if (browser.matches("\\w+")) {
                os = c.os.family;
                osVersion = (c.os.major == null) ? "" : (c.os.major + ((c.os.minor == null ? "" : "." + c.os.minor)));
                browserVersion = (c.userAgent.major == null) ? "" : (c.userAgent.major + ((c.userAgent.minor == null ? "" : "." + c.userAgent.minor)));

                processed = true;
            } else {
                os = osVersion = browser = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
