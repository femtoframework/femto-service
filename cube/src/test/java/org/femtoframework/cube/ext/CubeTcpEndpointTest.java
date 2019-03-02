package org.femtoframework.cube.ext;

import org.femtoframework.coin.*;
import org.femtoframework.cube.AppServer;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.cube.Endpoint;
import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class CubeTcpEndpointTest {

    @Test
    public void setExecutor() throws IOException {

        File tempFile = NutletUtil.getResourceAsFile("META-INF/spec/component.yaml");

        CoinModule coinModule = CoinUtil.newModule();
        coinModule.getController().create(tempFile);

        Namespace namespace = coinModule.getNamespaceFactory().get(CubeConstants.CUBE);
        ComponentFactory componentFactory = namespace.getComponentFactory();
        Component component = componentFactory.get(CubeConstants.APP_SERVER);
        assertNotNull(component);

        AppServer appServer = (AppServer)component.getBean();
        Endpoint endpoint = appServer.getEndpoint(CubeConstants.TCP_ENDPOINT);
        assertNotNull(endpoint);
    }
}