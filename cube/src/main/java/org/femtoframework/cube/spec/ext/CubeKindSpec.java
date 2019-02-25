package org.femtoframework.cube.spec.ext;

import org.femtoframework.coin.exception.SpecSyntaxException;
import org.femtoframework.coin.spec.*;
import org.femtoframework.coin.spec.element.*;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.cube.spec.CubeKind;

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
            beanElement.setTypeClass(SystemDef.class);
        }
        else if (CubeConstants.HOST.equals(kind)) {
            beanElement.setTypeClass(HostDef.class);
        }
        else if (CubeConstants.BACKEND.equals(kind)) {
            beanElement.setTypeClass(ServerDef.class);
        }
        else if (CubeConstants.CONNECTION.equals(kind)) {
            beanElement.setTypeClass(ConnectionDef.class);
        }
        else {
            throw new SpecSyntaxException("No such kind:" + kind + " in version(" + getVersion() + ") ");
        }
        return (S)beanElement;
    }
}
