package org.femtoframework.cube.spec;

import org.femtoframework.coin.CoinConstants;
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
    public String getApiVersion() {
        return CubeConstants.CUBE_V_7;
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
        String kind = MapSpec.getString(map, CoinConstants.KIND, null);
        ComponentElement compElement = new ComponentElement(map);
        if (CubeConstants.KIND_SYSTEM.equals(kind)) {
            compElement.setNamespace(CubeConstants.CUBE);
            compElement.setName(CubeConstants.NAME_SYSTEM);
            compElement.setTypeClass(SystemSpec.class);
        }
        else if (CubeConstants.KIND_APP_SERVER.equals(kind)) {
            compElement.setNamespace(CubeConstants.CUBE);
            compElement.setName(CubeConstants.NAME_APP_SERVER);
            compElement.setTypeClass(CubeAppServer.class);
        }
        else if (CubeConstants.KIND_TCP_ENDPOINT.equals(kind)) {
            compElement.setNamespace(CubeConstants.CUBE);
            compElement.setName(CubeConstants.NAME_TCP_ENDPOINT);
            compElement.setTypeClass(CubeTcpEndpoint.class);
            compElement.setBelongsTo("cube:app_server#addEndpoint");
        }
        else {
            throw new SpecSyntaxException("No such kind:" + kind + " in version(" + getApiVersion() + ") ");
        }
        return (S)compElement;
    }
}
