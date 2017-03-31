package simpleci.dispatcher.system.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class CentrifugoApi {
    private final Jedis jedis;

    public CentrifugoApi(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    public void send(Object message, String channel) {
        Map sendData = ImmutableMap.of(
                "data", ImmutableList.of(ImmutableMap.of(
                        "method", "publish",
                        "params", ImmutableMap.of(
                                "channel", channel,
                                "data", message))));

        Gson gson = new Gson();
        String sendMessage = gson.toJson(sendData);

        jedis.rpush("centrifugo.api", sendMessage);
    }
}
