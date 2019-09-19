package org.femtoframework.cube.spec;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.parameters.ParametersMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class ConnectionSpec {

    private URI uri;

    private Parameters<Object> parameters;

    public ConnectionSpec() {
    }

    public ConnectionSpec(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
        parse();
    }

    public String getHost() {
        String host = uri != null ? uri.getHost() : null;
        return host == null ? "*" : host;
    }

    public String getServerType() {
        String path = uri != null ? uri.getPath() : null;
        if (path != null) {
            if (path.startsWith("/")) {
                return path.substring(1);
            }
            else {
                return path;
            }
        }
        return null;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    protected void parse() throws URISyntaxException {
        if (parameters == null) {
            Parameters<Object> parameters = new ParametersMap<>();
            String queryString = uri.getQuery();
            for (String pair : queryString.split("&")) {
                int eq = pair.indexOf("=");
                if (eq < 0) {
                    // key=value
                    try {
                        String key = URLDecoder.decode(pair, "utf8");
                        parameters.put(key, Boolean.TRUE);
                    }
                    catch(Exception ex) {
                        throw new URISyntaxException(pair, "URL decoding exception:" + ex.getMessage());
                    }
                } else {
                    // key=value
                    try {
                        String key = URLDecoder.decode(pair.substring(0, eq), "utf8");
                        String value = URLDecoder.decode(pair.substring(eq + 1), "utf8");
                        parameters.put(key, value);
                    }
                    catch(Exception ex) {
                        throw new URISyntaxException(pair, "URL decoding exception:" + ex.getMessage());
                    }
                }
            }
            this.parameters = parameters;
        }
    }

    public Parameters<Object> getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
