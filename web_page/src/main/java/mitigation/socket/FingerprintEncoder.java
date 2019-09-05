package mitigation.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import mitigation.fp.FPEntity;

public class FingerprintEncoder implements Encoder.Text<FPEntity> {

    private static Gson gson = new GsonBuilder().create();

    @Override
    public String encode(FPEntity fingerprint) throws EncodeException {
        return gson.toJson(fingerprint);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        //Do nothing for the moment

    }

    @Override
    public void destroy() {
        // Do nothing for the moment

    }
}
