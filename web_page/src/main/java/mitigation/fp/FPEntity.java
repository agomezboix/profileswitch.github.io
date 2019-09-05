package mitigation.fp;

public class FPEntity {

    private String id;
    private String os;
    private String browser;
    private String platformJs;
    private String dntJs;
    private String timezoneJs;
    private String resolutionJs;
    private String pluginsJs;
    private String localJs;
    private String sessionJs;
    private String adBlock;
    private String vendorWebGljs;
    private String rendererWebGljs;
    private String fontsJs;
    private String canvasJs;
    private String acceptHttp;
    private String encodingHttp;
    private String languageHttp;
    private String userAgentHttp;
    private String orderHttp;// check name***
    private String cookiesJs; // all FPs collected by <bcom> cookies were

    public FPEntity(String id, String os, String browser, String platformJs, String dntJs, String timezoneJs, String resolutionJs, String pluginsJs, String localJs, String sessionJs, String adBlock, String vendorWebGljs, String rendererWebGljs, String fontsJs, String canvasJs, String acceptHttp, String encodingHttp, String languageHttp, String userAgentHttp, String orderHttp, String cookiesJs) {
        this.id = id;
        this.os = os;
        this.browser = browser;
        this.platformJs = platformJs;
        this.dntJs = dntJs;
        this.timezoneJs = timezoneJs;
        this.resolutionJs = resolutionJs;
        this.pluginsJs = pluginsJs;
        this.localJs = localJs;
        this.sessionJs = sessionJs;
        this.adBlock = adBlock;
        this.vendorWebGljs = vendorWebGljs;
        this.rendererWebGljs = rendererWebGljs;
        this.fontsJs = fontsJs;
        this.canvasJs = canvasJs;
        this.acceptHttp = acceptHttp;
        this.encodingHttp = encodingHttp;
        this.languageHttp = languageHttp;
        this.userAgentHttp = userAgentHttp;
        this.orderHttp = orderHttp;
        this.cookiesJs = cookiesJs;
    }

    public FPEntity() {
        this.id = "empty";
        this.os = "empty";
        this.browser = "empty";
        this.platformJs = "empty";
        this.dntJs = "empty";
        this.timezoneJs = "empty";
        this.resolutionJs = "empty";
        this.pluginsJs = "empty";
        this.localJs = "empty";
        this.sessionJs = "empty";
        this.adBlock = "empty";
        this.vendorWebGljs = "empty";
        this.rendererWebGljs = "empty";
        this.fontsJs = "empty";
        this.canvasJs = "empty";
        this.acceptHttp = "empty";
        this.encodingHttp = "empty";
        this.languageHttp = "empty";
        this.userAgentHttp = "empty";
        this.orderHttp = "empty";
        this.cookiesJs = "empty";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getPlatformJs() {
        return platformJs;
    }

    public void setPlatformJs(String platformJs) {
        this.platformJs = platformJs;
    }

    public String getDntJs() {
        return dntJs;
    }

    public void setDntJs(String dntJs) {
        this.dntJs = dntJs;
    }

    public String getTimezoneJs() {
        return timezoneJs;
    }

    public void setTimezoneJs(String timezoneJs) {
        this.timezoneJs = timezoneJs;
    }

    public String getResolutionJs() {
        return resolutionJs;
    }

    public void setResolutionJs(String resolutionJs) {
        this.resolutionJs = resolutionJs;
    }

    public String getPluginsJs() {
        return pluginsJs;
    }

    public void setPluginsJs(String pluginsJs) {
        this.pluginsJs = pluginsJs;
    }

    public String getLocalJs() {
        return localJs;
    }

    public void setLocalJs(String localJs) {
        this.localJs = localJs;
    }

    public String getSessionJs() {
        return sessionJs;
    }

    public void setSessionJs(String sessionJs) {
        this.sessionJs = sessionJs;
    }

    public String getAdBlock() {
        return adBlock;
    }

    public void setAdBlock(String adBlock) {
        this.adBlock = adBlock;
    }

    public String getVendorWebGljs() {
        return vendorWebGljs;
    }

    public void setVendorWebGljs(String vendorWebGljs) {
        this.vendorWebGljs = vendorWebGljs;
    }

    public String getRendererWebGljs() {
        return rendererWebGljs;
    }

    public void setRendererWebGljs(String rendererWebGljs) {
        this.rendererWebGljs = rendererWebGljs;
    }

    public String getFontsJs() {
        return fontsJs;
    }

    public void setFontsJs(String fontsJs) {
        this.fontsJs = fontsJs;
    }

    public String getCanvasJs() {
        return canvasJs;
    }

    public void setCanvasJs(String canvasJs) {
        this.canvasJs = canvasJs;
    }

    public String getAcceptHttp() {
        return acceptHttp;
    }

    public void setAcceptHttp(String acceptHttp) {
        this.acceptHttp = acceptHttp;
    }

    public String getEncodingHttp() {
        return encodingHttp;
    }

    public void setEncodingHttp(String encodingHttp) {
        this.encodingHttp = encodingHttp;
    }

    public String getLanguageHttp() {
        return languageHttp;
    }

    public void setLanguageHttp(String languageHttp) {
        this.languageHttp = languageHttp;
    }

    public String getUserAgentHttp() {
        return userAgentHttp;
    }

    public void setUserAgentHttp(String userAgentHttp) {
        this.userAgentHttp = userAgentHttp;
    }

    public String getOrderHttp() {
        return orderHttp;
    }

    public void setOrderHttp(String orderHttp) {
        this.orderHttp = orderHttp;
    }

    public String getCookiesJs() {
        return cookiesJs;
    }

    public void setCookiesJs(String cookiesJs) {
        this.cookiesJs = cookiesJs;
    }

    @Override
    public String toString() {
        return id + ",'" + os + "','" + browser + "'," + platformJs + "," + dntJs + "," + timezoneJs + "," + resolutionJs + ",'"
                + pluginsJs + "'," + localJs + "," + adBlock + ",'" + vendorWebGljs + "','" + rendererWebGljs + "'," + fontsJs + ","
                + canvasJs + ",'" + acceptHttp + "','" + encodingHttp + "','" + languageHttp + "','" + userAgentHttp + "','"
                + orderHttp + "'," + cookiesJs;
    }

}
