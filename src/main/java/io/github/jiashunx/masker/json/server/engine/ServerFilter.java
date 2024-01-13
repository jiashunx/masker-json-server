package io.github.jiashunx.masker.json.server.engine;

import io.github.jiashunx.masker.json.server.context.RestContext;
import io.github.jiashunx.masker.json.server.context.ServerContext;
import io.github.jiashunx.masker.rest.framework.MRestRequest;
import io.github.jiashunx.masker.rest.framework.MRestResponse;
import io.github.jiashunx.masker.rest.framework.filter.MRestFilter;
import io.github.jiashunx.masker.rest.framework.filter.MRestFilterChain;

/**
 * @author jiashunx
 */
public class ServerFilter implements MRestFilter {

    public ServerFilter(ServerContext serverContext, RestContext restContext) {

    }

    @Override
    public void doFilter(MRestRequest request, MRestResponse response, MRestFilterChain filterChain) {
        filterChain.doFilter(request, response);
    }

}
