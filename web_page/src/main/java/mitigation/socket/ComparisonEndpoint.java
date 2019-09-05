package mitigation.socket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import mitigation.fp.Controller;
import mitigation.fp.FPEntity;

@ServerEndpoint(value = "/mitigation", decoders = FingerprintDecoder.class, encoders = FingerprintEncoder.class)
public class ComparisonEndpoint {

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        System.out.println("open");
// Do nothing for the moment
    }

    @OnMessage
    public void onMessage(FPEntity fp, Session session) throws IOException, EncodeException {
        Controller controller = new Controller();
        FPEntity fpentity=controller.getRecpommendation(fp);
        controller.writeToFile(fpentity);
        session.getBasicRemote().sendObject(fpentity);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println(throwable.toString());
    }
}
