package org.femtoframework.cube.spec;

import org.femtoframework.coin.exception.SpecSyntaxException;
import org.femtoframework.coin.spec.*;
import org.femtoframework.coin.spec.element.*;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.cube.ext.CubeAppServer;
import org.femtoframework.cube.ext.CubeTcpEndpoint;

import java.util.Map;

public class CubeKindSpec implements KindSpec {
    /**
     * The version this ResourceFactory supports
     *
     * @return Version
     */
    @Override
    public String getVersion() {
        return CubeConstants.SPEC_VERSION;
    }

    /**
     * The Kind Class it uses
     *
     * @return The kind Class should be an Enum and Kind implementation
     */
    @Override
    public Class<? extends Kind> getKindClass() {
        return CubeKind.class;
    }

    /**
     * Convert Kind to right spec
     *
     * @param map Map like data
     * @return right spec object
     */
    public <S extends MapSpec> S toSpec(Map map) {
        String kind = ModelElement.getString(map, SpecConstants._KIND, null);
        BeanElement beanElement = new BeanElement(map);
        if (CubeConstants.SYSTEM.equals(kind)) {
            beanElement.setNamespace(CubeConstants.CUBE);
            beanElement.setName(CubeConstants.SYSTEM);
            beanElement.setTypeClass(SystemSpec.class);
        }
        else if (CubeConstants.APP_SERVER.equals(kind)) {
            beanElement.setNamespace(CubeConstants.CUBE);
            beanElement.setName(CubeConstants.APP_SERVER);
            beanElement.setTypeClass(CubeAppServer.class);
        }
        else if (CubeConstants.TCP_ENDPOINT.equals(kind)) {
            beanElement.setNamespace(CubeConstants.CUBE);
            beanElement.setName(CubeConstants.TCP_ENDPOINT);
            beanElement.setTypeClass(CubeTcpEndpoint.class);
            beanElement.setBelongsTo("cube:app_server#addEndpoint");
        }
        else {
            throw new SpecSyntaxException("No such kind:" + kind + " in version(" + getVersion() + ") ");
        }
        return (S)beanElement;
    }
}
