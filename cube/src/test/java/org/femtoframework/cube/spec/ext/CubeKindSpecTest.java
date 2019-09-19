package org.femtoframework.cube.spec.ext;

import org.femtoframework.coin.*;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.cube.spec.ConnectionSpec;
import org.femtoframework.cube.spec.ServerSpec;
import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

public class CubeKindSpecTest {

    @Test
    public void toSpec() throws Exception {
        File tempFile = NutletUtil.getResourceAsFile("cube.yaml");

        CoinModule coinModule = CoinUtil.newModule();
        coinModule.getController().create(tempFile);

        Namespace namespace = coinModule.getNamespaceFactory().get(CubeConstants.CUBE);
        ComponentFactory componentFactory = namespace.getComponentFactory();
        Component component = componentFactory.get(CubeConstants.NAME_SYSTEM);
        assertNotNull(component);

        SystemSpec systemSpec = component.getBean(SystemSpec.class);
        List<ServerSpec> servrers = systemSpec.getServers();
        assertEquals(2, servrers.size());

        ServerSpec front = servrers.get(0);
        List<ConnectionSpec> specs = front.getConnectionSpecs();
        assertEquals(1, specs.size());
        ConnectionSpec spec = specs.get(0);
        URI uri =  spec.getUri();
        assertEquals(uri.getPath(), "/backend");
        assertEquals("*", spec.getHost());
        assertEquals("backend", spec.getServerType());
        assertEquals("gmpp", uri.getScheme());
    }
}