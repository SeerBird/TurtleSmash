package game.connection;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.net.*;
import java.security.cert.CertificateException;

public class Addresses {
    public static InetAddress localAddress;
    public static InetAddress groupAddress;
    public static NetworkInterface networkInterface;
    public static final boolean SSL = System.getProperty("ssl") != null; //idk what this is even.
    // /|\ leaving this in case I will want to actually deal with it, does nothing rn.
    private static final String multicastIP = "224.42.42.42";
    public static final int multicastPort = 5455;

    static {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            networkInterface = NetworkInterface.getByInetAddress(localHost);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                if (address.getAddress() instanceof Inet4Address) {
                    localAddress = address.getAddress();
                    break;
                }
            }
            if (localAddress == null) {
                throw new RuntimeException("Failed to find an IPV4 address");//handle this, don't throw
            }
            groupAddress = InetAddress.getByName(multicastIP);
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
}
