package game.connection;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.*;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Enumeration;

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
            //region try simple method
            InetAddress localHost = Inet4Address.getLocalHost();
            networkInterface = NetworkInterface.getByInetAddress(localHost);
            groupAddress = InetAddress.getByName(multicastIP);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                if (address.getAddress() instanceof Inet4Address) {
                    localAddress = address.getAddress();
                    break;
                }
            }
            //endregion
            //region try more complicated method that is less likely to return *anything*
            OuterBreak:
            for (NetworkInterface interface_ : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                //region get rid of loopback
                try {
                    if (interface_.isLoopback())
                        continue;
                } catch (SocketException e) {
                    continue;
                }
                //endregion
                //region get rid of interfaces that aren't running
                try {
                    if (!interface_.isUp())
                        continue;
                } catch (SocketException e) {
                    continue;
                }
                //endregion
                //region iterate over the addresses associated with the interface
                Enumeration<InetAddress> addresses = interface_.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    // look only for ipv4 addresses
                    if (address instanceof Inet6Address)
                        continue;

                    // use a timeout big enough for your needs
                    try {
                        if (!address.isReachable(100))
                            continue;
                    } catch (IOException e) {
                        continue;
                    }

                    /*
                    try to connect to a socket at the address
                    try (SocketChannel socket = new SocketChannel()) {
                        // again, use a big enough timeout
                        socket.socket().setSoTimeout(3000);

                        // bind the socket to your local interface
                        socket.bind(new InetSocketAddress(address, 8080));

                        // try to connect to *somewhere*
                        socket.connect(new InetSocketAddress("google.com", 80));
                    } catch (IOException ex) {
                        continue;
                    }

                    System.out.format("ni: %s, ia: %s\n", interface_, address);
                    */
                    // stops at the first ~*working*~ solution
                    networkInterface = interface_;
                    localAddress = address;
                    break OuterBreak;
                }
                //endregion
            }
            //endregion
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        if (localAddress == null) {
            throw new RuntimeException("Failed to find an IPV4 address");//handle this, don't throw
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
