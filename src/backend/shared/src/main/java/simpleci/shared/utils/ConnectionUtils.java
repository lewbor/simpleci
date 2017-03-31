package simpleci.shared.utils;

import com.rabbitmq.client.Connection;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicy;
import net.jodah.lyra.util.Duration;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtils {

    public static DataSource createDataSource(String host, int port, String name, String user, String password) {
        BasicDataSource bds = new BasicDataSource();

        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, name));
        bds.setUsername(user);
        bds.setPassword(password);
        bds.setInitialSize(5);

        return bds;
    }

    public static Connection createRabbitmqConnection(String host, int port, String user, String password) throws IOException, TimeoutException {
        Config config = new Config()
                .withRecoveryPolicy(new RecoveryPolicy()
                        .withBackoff(Duration.seconds(1), Duration.seconds(30))
                        .withMaxAttempts(20));
        ConnectionOptions options = new ConnectionOptions()
                .withHost(host)
                .withPort(port)
                .withUsername(user)
                .withPassword(password);

        return Connections.create(options, config);
    }
}
