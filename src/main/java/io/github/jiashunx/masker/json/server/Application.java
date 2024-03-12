package io.github.jiashunx.masker.json.server;

import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.json.server.servlet.Servlet;
import io.github.jiashunx.masker.json.server.service.ArgumentService;
import io.github.jiashunx.masker.rest.framework.MRestServer;
import io.github.jiashunx.sdk.sqlite3.mapping.SQLite3JdbcTemplate;
import io.github.jiashunx.sdk.sqlite3.mapping.util.SQLite3SQLHelper;
import io.github.jiashunx.sdk.sqlite3.metadata.xml.SQLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiashunx
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        // fix: JDK11 HttpClient restricted header name: “xxx“ 异常，JDK11无法绕过，JDK17,JDK21可绕过
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "connection,content-length,expect,host,upgrade");
        long startTimeMillis = System.currentTimeMillis();
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
        long endTimeMillis = System.currentTimeMillis();
        logger.info("Server started on port(s) {} (http)", restServer.getListenPort());
        logger.info("Started Application in {} seconds", (endTimeMillis - startTimeMillis) / 1000D);
    }

}
