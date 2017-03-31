package simpleci.worker.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public final class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static boolean waitForPort(String host, int port, int numberOfAttemps, int attemptDelay ) {
        for(int attempt = 1; attempt <= numberOfAttemps; attempt++) {
            logger.info(String.format("Connecting to %s:%d, attempt %d", host, port, attempt));
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), 500);
                return true;
            } catch (IOException e) {
                try {
                    Thread.sleep(attemptDelay);
                } catch (InterruptedException e1) {

                }
            }
        }
        return false;
    }

    public static String execReadToString(String execCommand) throws IOException {
        Process proc = Runtime.getRuntime().exec(execCommand);
        try (InputStream stream = proc.getInputStream()) {
            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                return s.hasNext() ? trimLineBreaks(s.next()) : "";
            }
        }
    }

    public static String trimLineBreaks(String line) {
        return line.replaceAll("\n", "");
    }

    public static long timeDiffMilliseconds(Date from, Date to) {
        return from.getTime() - to.getTime();
    }
}
