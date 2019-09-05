package mitigation.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import javax.websocket.*;
import mitigation.fp.FPEntity;

public class FingerprintDecoder implements Decoder.Text<FPEntity> {

    private static Gson gson = new GsonBuilder().create();

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Do nothing for the moment
    }

    @Override
    public void destroy() {
        // Do nothing for the moment

    }

    @Override
    public FPEntity decode(String s) throws DecodeException {
        try {

            return gson.fromJson(s, FPEntity.class);
        }
        catch (JsonSyntaxException exc) {
            throw new DecodeException(s, "Could not decode given JSON", exc);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return s != null && !s.equals("");
    }
}
