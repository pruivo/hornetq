package org.hornetq.api.core.client.loadbalance;

import org.hornetq.api.core.Pair;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.utils.ConfigurationHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ConnectionLoadBalancingPolicy#select(org.hornetq.api.core.Pair[])} returns the value set, otherwise it falls back to the round robin policy
 *
 * @author Pedro Ruivo
 * @since 2.2
 */
public class CloudtmLoadBalancingPolicy extends RoundRobinConnectionLoadBalancingPolicy {

    private static final ThreadLocal<Host> HOST = new ThreadLocal<Host>();
    private static final int ANY_PORT = -1;

    public static void setHostName(String hostname) throws UnknownHostException {
        HOST.set(new Host(hostname, ANY_PORT));
    }

    public static void setHostNameAndPort(String hostname, String port) throws UnknownHostException {
        HOST.set(new Host(hostname, Integer.valueOf(port)));
    }

    @Override
    public int select(Pair<TransportConfiguration, TransportConfiguration>[] elements) {
        Host desiredHost = HOST.get();
        if (desiredHost == null) {
            return super.select(elements);
        }
        try {
            for (int i = 0; i < elements.length; ++i) {
                if (isThisHost(elements[i].getA(), desiredHost)) {
                    return i;
                }
            }
        } catch (UnknownHostException e) {
            //swallow and shut up...
        }
        return super.select(elements);
    }

    @Override
    public int select(TransportConfiguration[] elements) {
        Host desiredHost = HOST.get();
        if (desiredHost == null) {
            return super.select(elements);
        }
        try {
            for (int i = 0; i < elements.length; ++i) {
                if (isThisHost(elements[i], desiredHost)) {
                    return i;
                }
            }
        } catch (UnknownHostException e) {
            //swallow and shut up...
        }
        return super.select(elements);
    }

    private List<Host> convertTransportConfiguration(TransportConfiguration transportConfiguration) throws UnknownHostException {
        String host = ConfigurationHelper.getStringProperty(TransportConstants.HOST_PROP_NAME,
                null,
                transportConfiguration.getParams());
        int port = ConfigurationHelper.getIntProperty(TransportConstants.PORT_PROP_NAME,
                ANY_PORT,
                transportConfiguration.getParams());

        String[] hosts = TransportConfiguration.splitHosts(host);
        ArrayList<Host> list = new ArrayList<Host>(hosts.length);
        for (String h : hosts) {
            list.add(new Host(h, port));
        }
        return list;
    }

    private boolean isThisHost(TransportConfiguration transportConfiguration, Host desiredHost) throws UnknownHostException {
        List<Host> hostList = convertTransportConfiguration(transportConfiguration);
        for (Host toTest : hostList) {
            boolean portMatch = toTest.port == ANY_PORT || desiredHost.port == ANY_PORT || toTest.port == desiredHost.port;
            if (!portMatch) {
                continue;
            }
            for (InetAddress address1 : toTest.host) {
                for (InetAddress address2 : desiredHost.host) {
                    if (address1.equals(address2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static class Host {
        private final InetAddress[] host;
        private final int port;

        private Host(String host, int port) throws UnknownHostException {
            this.host = InetAddress.getAllByName(host);
            this.port = port;
        }
    }
}
