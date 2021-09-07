package org.nutz.boot.starter.swagger3;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/18
 */
public interface NutzReaderListener {
    /**
     * Called before the OpenAPI definition gets populated from scanned classes. Use this method to
     * pre-process the OpenAPI definition before it gets populated.
     *
     * @param reader  the reader used to read annotations and build the openAPI definition
     * @param openAPI the initial OpenAPI definition
     */

    void beforeScan(NutzReader reader, OpenAPI openAPI);

    /**
     * Called after a OpenAPI definition has been populated from scanned classes. Use this method to
     * post-process OpenAPI definitions.
     *
     * @param reader  the reader used to read annotations and build the OpenAPI definition
     * @param openAPI the configured OpenAPI definition
     */

    void afterScan(NutzReader reader, OpenAPI openAPI);
}
