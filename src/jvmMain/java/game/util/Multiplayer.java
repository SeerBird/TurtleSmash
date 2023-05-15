package game.util;

import game.Config;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;

public class Multiplayer {
    public static InetAddress broadcastIP;
    public static String localIp;

    static {
        try {
            broadcastIP = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            localIp = socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }
    }
    public static SslContext buildSslContext() throws CertificateException, SSLException {
        if (!SSL) {
            return null;
        }
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        return SslContextBuilder
                .forServer(ssc.certificate(), ssc.privateKey())
                .build();
    }
    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final String localhost = System.getProperty("host", "localhost");
    public static final int TCPPort = Integer.parseInt(System.getProperty("tcpport", String.valueOf(Config.TCPPort)));
    public static final int UDPPort = Integer.parseInt(System.getProperty("udpport", "54777"));
}
