package simpleci.shared.utils;

import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import simpleci.shared.utils.ConnectionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class TestServicesUtils {
    private final static Logger logger = LoggerFactory.getLogger(TestServicesUtils.class);

    public static boolean testRedis(String host, int port) {
        final int tryCount = 10;
        for (int i = 1; i <= tryCount; i++) {
            logger.info(String.format("redis: connecting to %s:%s, try %d of %d",   host, port, i, tryCount));
            try {
                Jedis connection = new Jedis(host, port);
                connection.connect();
                connection.close();
                logger.info("Connection to redis established successfully");
                return true;
            } catch (JedisConnectionException e) {
                logger.info("Failed to connect: " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error("", e);
                }
            }
        }
        logger.info(String.format("Failed connect to redis on %s:%d", host, port));
        return false;
    }

    public static boolean testDatabase(String host, int port, String name, String user, String password) {
        final int tryCount = 10;
        for (int i = 1; i <= tryCount; i++) {
            try {
                logger.info(String.format("database: connecting to %s:%d, try %d of %d", host, port, i, tryCount));
                java.sql.Connection connection = ConnectionUtils.createDataSource(host, port, name, user, password).getConnection();
                connection.close();
                logger.info("Connection to database established successfully");
                return true;
            } catch (SQLException e) {
                logger.info("Failed to connect");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error("", e);
                }
            }
        }
        logger.info(String.format("Failed connect to database on %s:%d", host, port));
        return false;
    }

    public static boolean testRabbitmq(String host, int port, String user, String password) {
        final int tryCount = 10;
         for (int i = 1; i <= tryCount; i++) {
            try {
                logger.info(String.format("rabbitmq: connecting to %s:%s, try %d of %d", host, port, i, tryCount));
                Connection connection = ConnectionUtils.createRabbitmqConnection(host, port, user, password);
                connection.close();
                logger.info("Connection to rabbitmq established successfully");
                return true;
            } catch (Throwable  e) {
                logger.info("Failed to connect: " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error("", e);
                }
            }
        }
        logger.info(String.format("Failed connect to rabbitmq on %s:%d", host, port));
        return false;
    }


}
