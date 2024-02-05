package io.github.jiashunx.masker.json.server;

import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.json.server.servlet.Servlet;
import io.github.jiashunx.masker.json.server.service.ArgumentService;
import io.github.jiashunx.masker.rest.framework.MRestServer;
import io.github.jiashunx.sdk.sqlite3.mapping.SQLite3JdbcTemplate;
import io.github.jiashunx.sdk.sqlite3.mapping.util.SQLite3SQLHelper;
import io.github.jiashunx.sdk.sqlite3.metadata.xml.SQLPackage;

/**
 * @author jiashunx
 */
public class Application {

    public static void main(String[] args) {
        // 解析参数
        ArgumentService argumentService = new ArgumentService(args);
        // 初始化SQLite3数据库
        SQLite3JdbcTemplate jdbcTemplate = new SQLite3JdbcTemplate(argumentService.getWorkspace() + "/json-server.db");
        // 解析SQLite3数据库表结构
        SQLPackage sqlPackage = SQLite3SQLHelper.loadSQLPackageFromClasspath("ddl-sqlite3.xml");
        // 初始化SQLite3数据库表结构
        jdbcTemplate.initSQLPackage(sqlPackage);
        // 初始化服务实例
        TbServerService tbServerService = new TbServerService(jdbcTemplate);
        TbRestService tbRestService = new TbRestService(jdbcTemplate);
        // 创建Server实例
        MRestServer restServer = new MRestServer(argumentService.getListenPort(), "json-server");
        // 创建路由处理Servlet
        Servlet servlet = new Servlet(argumentService, tbServerService, tbRestService);
        // 启动web服务
        restServer.bossThreadNum(1)
                .workerThreadNum(2)
                .callbackAfterStartup(servlet::initServlet)
                .context()
                .addDefaultClasspathResource()
                .servlet(servlet)
                .getRestServer()
                .start();
    }

}
