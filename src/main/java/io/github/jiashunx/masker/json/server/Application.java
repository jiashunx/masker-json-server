package io.github.jiashunx.masker.json.server;

import io.github.jiashunx.masker.rest.framework.MRestServer;
import io.github.jiashunx.masker.rest.framework.filter.MRestFilter;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author jiashunx
 */
public class Application {

    public static void main(String[] args) {
        new MRestServer(18080, "json-server")
                .bossThreadNum(1)
                .workerThreadNum(2)
                .context()
                .filter(new MRestFilter[] {})
                .filter("/*", (request, response, filterChain) -> {
                    response.write(HttpResponseStatus.NOT_FOUND);
                })
                .getRestServer()
                .start();
    }

}
