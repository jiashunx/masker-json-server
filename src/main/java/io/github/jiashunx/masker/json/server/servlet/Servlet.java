package io.github.jiashunx.masker.json.server.servlet;

import io.github.jiashunx.masker.json.server.context.RestContext;
import io.github.jiashunx.masker.json.server.context.ServerContext;
import io.github.jiashunx.masker.json.server.engine.ServerEngine;
import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.rest.framework.MRestRequest;
import io.github.jiashunx.masker.rest.framework.MRestResponse;
import io.github.jiashunx.masker.rest.framework.servlet.AbstractRestServlet;
import io.github.jiashunx.masker.rest.framework.servlet.mapping.PostMapping;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author jiashunx
 */
public class Servlet extends AbstractRestServlet {

    private static final Logger logger = LoggerFactory.getLogger(Servlet.class);

    private final ServerContext serverContext;
    private final RestContext restContext;
    private final ServerEngine serverEngine;

    public Servlet(TbServerService tbServerService, TbRestService tbRestService) {
        this.serverContext = new ServerContext(tbServerService);
        this.restContext = new RestContext(tbRestService);
        this.serverEngine = new ServerEngine(this.serverContext, this.restContext);
        this.serverContext.setServerEngine(this.serverEngine);
    }

    private volatile boolean initialized = false;

    public synchronized void initServlet() {
        if (!initialized) {
            serverEngine.startServers();
            initialized = true;
        }
    }

    public static RestResult execute(Supplier<RestResult> supplier) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            logger.error("请求处理异常", throwable);
            return RestResult.failWithMessage(StringUtils.getExceptionStackTrace(throwable));
        }
    }

    @PostMapping(url = "/server/create")
    public void createServer(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> serverContext.create(request.parseBodyToObj(TbServer.class))));
    }

    @PostMapping(url = "/server/update")
    public void updateServer(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> serverContext.update(request.parseBodyToObj(TbServer.class))));
    }

    @PostMapping(url = "/server/delete")
    public void deleteServer(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> serverContext.delete(request.parseBodyToObj(TbServer.class))));
    }

    @PostMapping(url = "/server/queryList")
    public void queryServers(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> serverContext.queryList(request.parseBodyToObj(PageQueryVo.class))));
    }

    @PostMapping(url = "/server/queryAll")
    public void queryServersAll(MRestRequest request, MRestResponse response) {
        response.write(execute(serverContext::queryAll));
    }

    @PostMapping(url = "/rest/create")
    public void createRest(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> restContext.create(request.parseBodyToObj(TbRest.class))));
    }

    @PostMapping(url = "/rest/update")
    public void updateRest(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> restContext.update(request.parseBodyToObj(TbRest.class))));
    }

    @PostMapping(url = "/rest/delete")
    public void deleteRest(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> restContext.delete(request.parseBodyToObj(TbRest.class))));
    }

    @PostMapping(url = "/rest/queryList")
    public void queryRests(MRestRequest request, MRestResponse response) {
        response.write(execute(() -> restContext.queryList(request.parseBodyToObj(PageQueryVo.class))));
    }

}
