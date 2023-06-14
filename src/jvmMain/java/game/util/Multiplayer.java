package game.util;

import game.Config;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class Multiplayer {
    public static InetAddress localIp;
    public static NetworkInterface networkInterface;

    static {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            networkInterface = NetworkInterface.getByInetAddress(localHost);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                if (address.getAddress() instanceof Inet4Address) {
                    localIp = address.getAddress();
                    break;
                }
            }
            if (localIp == null) {
                throw new RuntimeException("Failed to find an IPV4 address");//handle this, don't throw
            }
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
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
    public static final int UDPPort = Integer.parseInt(System.getProperty("udpport", "54777"));
    public static final String multicastIP = System.getProperty("host", "224.42.42.42");
}
