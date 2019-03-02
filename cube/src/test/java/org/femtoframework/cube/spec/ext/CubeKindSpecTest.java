package org.femtoframework.cube.spec.ext;

import org.femtoframework.coin.*;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class CubeKindSpecTest {

    @Test
    public void toSpec() throws Exception {
        File tempFile = NutletUtil.getResourceAsFile("cube.yaml");

        CoinModule coinModule = CoinUtil.newModule();
        coinModule.getController().create(tempFile);

        Namespace namespace = coinModule.getNamespaceFactory().get(CubeConstants.CUBE);
        ComponentFactory componentFactory = namespace.getComponentFactory();
        Component component = componentFactory.get(CubeConstants.SYSTEM);
        assertNotNull(component);

    }
}