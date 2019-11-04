package org.femtoframework.service.k8s;

import org.femtoframework.cube.spec.ConnectionSpec;
import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.service.apsis.cube.CubeConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * K8s Service
 */
public class K8sServiceConnector extends CubeConnector {

    public static final int DEFAULT_PORT = 9168;

    private static Logger log = LoggerFactory.getLogger(K8sServiceConnector.class);

    protected void connect(SystemSpec systemSpec, ConnectionSpec conn) {
        //Service Type
        URI uri = conn.getUri();
        String serviceType = uri.getPath();
        int port = uri.getPort();
        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        // In K8s, statefulset uses "serviceType"
        try {
            InetAddress[] addresses = InetAddress.getAllByName(serviceType);
            for(InetAddress add: addresses) {
                connect(uri.getScheme(), add.getHostAddress(), port, conn);
            }
        } catch (UnknownHostException e) {
            log.warn("Service:" + serviceType + " not found", e);
        }
    }
}
