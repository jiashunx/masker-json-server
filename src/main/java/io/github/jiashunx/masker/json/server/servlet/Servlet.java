package io.github.jiashunx.masker.json.server.servlet;

import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.rest.framework.MRestRequest;
import io.github.jiashunx.masker.rest.framework.MRestResponse;
import io.github.jiashunx.masker.rest.framework.servlet.AbstractRestServlet;
import io.github.jiashunx.masker.rest.framework.servlet.mapping.PostMapping;
import io.github.jiashunx.sdk.sqlite3.mapping.SQLite3JdbcTemplate;

import java.util.Objects;

/**
 * @author jiashunx
 */
public class Servlet extends AbstractRestServlet {

    private final SQLite3JdbcTemplate jdbcTemplate;
    private final TbServerService tbServerService;
    private final TbRestService tbRestService;

    public Servlet(SQLite3JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.tbServerService = new TbServerService(this.jdbcTemplate);
        this.tbRestService = new TbRestService(this.jdbcTemplate);
    }

    private volatile boolean initialized = false;

    public synchronized Servlet initServlet() {
        if (!initialized) {
            // TODO 启动所有server+rest
            initialized = true;
        }
        return this;
    }

    @PostMapping(url = "/server/create")
    public void createServer(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/server/update")
    public void updateServer(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/server/delete")
    public void deleteServer(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/server/queryList")
    public void queryServers(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/server/queryOne")
    public void queryServer(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/rest/create")
    public void createRest(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/rest/update")
    public void updateRest(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/rest/delete")
    public void deleteRest(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/rest/queryList")
    public void queryRests(MRestRequest request, MRestResponse response) {

    }

    @PostMapping(url = "/rest/queryOne")
    public void queryRest(MRestRequest request, MRestResponse response) {

    }

    public SQLite3JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public TbServerService getTbServerService() {
        return tbServerService;
    }

    public TbRestService getTbRestService() {
        return tbRestService;
    }
}
