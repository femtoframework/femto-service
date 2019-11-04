package org.femtoframework.service.apsis.spec;

import org.femtoframework.coin.CoinConstants;
import org.femtoframework.coin.exception.SpecSyntaxException;
import org.femtoframework.coin.spec.Kind;
import org.femtoframework.coin.spec.KindSpec;
import org.femtoframework.coin.spec.MapSpec;
import org.femtoframework.coin.spec.element.ComponentElement;
import org.femtoframework.service.apsis.ApsisConstants;
import org.femtoframework.service.apsis.event.EventServer;
import org.femtoframework.service.apsis.ext.SimpleApsisServer;
import org.femtoframework.service.apsis.gmpp.GmppConnector;
import org.femtoframework.service.apsis.k8s.K8sConnector;
import org.femtoframework.service.apsis.rmi.RmiServer;

import java.util.Map;

import static org.femtoframework.service.apsis.ApsisConstants.APSIS_V_7;

public class ApsisKindSpec implements KindSpec {
    /**
     * The version this ResourceFactory supports
     *
     * @return Version
     */
    @Override
    public String getApiVersion() {
        return APSIS_V_7;
    }

    /**
     * The Kind Class it uses
     *
     * @return The kind Class should be an Enum and Kind implementation
     */
    @Override
    public Class<? extends Kind> getKindClass() {
        return ApsisKind.class;
    }

    /**
     * Convert Kind to right spec
     *
     * @param map Map like data
     * @return right spec object
     */
    @Override
    public <S extends MapSpec> S toSpec(Map map) {
        String kind = MapSpec.getString(map, CoinConstants.KIND, null);
        ComponentElement compElement = new ComponentElement(map);
        if (ApsisConstants.KIND_APSIS_SERVER.equals(kind)) {
            compElement.setNamespace(ApsisConstants.NAMESPACE_APSIS);
            compElement.setName(ApsisConstants.NAME_APSIS_SERVER);
            compElement.setTypeClass(SimpleApsisServer.class);
        }
        else if (ApsisConstants.KIND_RMI_SERVER.equals(kind)) {
            compElement.setNamespace(ApsisConstants.NAMESPACE_APSIS);
            compElement.setName(ApsisConstants.NAME_RMI_SERVER);
            compElement.setTypeClass(RmiServer.class);
            compElement.setBelongsTo("apsis:apsis_server#addServer");
        }
        else if (ApsisConstants.KIND_EVENT_SERVER.equals(kind)) {
            compElement.setNamespace(ApsisConstants.NAMESPACE_APSIS);
            compElement.setName(ApsisConstants.NAME_EVENT_SERVER);
            compElement.setTypeClass(EventServer.class);
            compElement.setBelongsTo("apsis:apsis_server#addServer");
        }
        else if (ApsisConstants.KIND_GMPP_CONNECTOR.equals(kind)) {
            compElement.setNamespace(ApsisConstants.NAMESPACE_APSIS);
            compElement.setName(ApsisConstants.NAME_GMPP_CONNECTOR);
            compElement.setTypeClass(GmppConnector.class);
            compElement.setBelongsTo("apsis:apsis_server#addConnector");
        }
        else if (ApsisConstants.KIND_K8S_CONNECTOR.equals(kind)) {
            compElement.setNamespace(ApsisConstants.NAMESPACE_APSIS);
            compElement.setName(ApsisConstants.NAME_K8S_CONNECTOR);
            compElement.setTypeClass(K8sConnector.class);
            compElement.setBelongsTo("apsis:apsis_server#addConnector");
        }
        else {
            throw new SpecSyntaxException("No such kind:" + kind + " in version(" + getApiVersion() + ") ");
        }
        return (S)compElement;
    }
}
