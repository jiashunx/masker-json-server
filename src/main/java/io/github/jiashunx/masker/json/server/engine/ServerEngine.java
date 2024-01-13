package io.github.jiashunx.masker.json.server.engine;

import io.github.jiashunx.masker.json.server.context.RestContext;
import io.github.jiashunx.masker.json.server.context.ServerContext;
import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.masker.json.server.type.ServerStatus;
import io.github.jiashunx.masker.rest.framework.MRestServer;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author jiashunx
 */
public class ServerEngine {

    private static final Logger logger = LoggerFactory.getLogger(ServerEngine.class);

    private final ServerContext serverContext;
    private final RestContext restContext;
    private final Map<String, MRestServer> serverMap = new HashMap<>();

    public ServerEngine(ServerContext serverContext, RestContext restContext) {
        this.serverContext = Objects.requireNonNull(serverContext);
        this.restContext = Objects.requireNonNull(restContext);
    }

    @SuppressWarnings("unchecked")
    public synchronized void startServers() {
        try {
            RestResult restResult = serverContext.queryAll();
            if (!restResult.isSuccess()) {
                logger.error("查询Server列表失败, 错误信息: {}", restResult.getMessage());
                return;
            }
            List<TbServer> serverList = (List<TbServer>) restResult.getData();
            int total = serverList.size();
            int succCount = 0;
            for (TbServer server: serverList) {
                if (startServer(server.getServerId())) {
                    succCount++;
                }
            }
            logger.info("启动Server实例处理完成, Server实例总数: {}, 启动成功Server实例总数: {}", total, succCount);
        } catch (Throwable throwable) {
            logger.error("启动Server实例处理异常", throwable);
        }
    }

    public synchronized boolean startServer(String serverId) {
        try {
            TbServer server = serverContext.getTbServerService().findWithNoCache(serverId);
            if (server == null) {
                logger.error("启动Server实例失败: 根据 serverId={} 找不到对应Server实例", serverId);
                return false;
            }
            int serverPort = server.getServerPort();
            String serverName = server.getServerName();
            if (serverMap.containsKey(serverId)) {
                logger.warn("Server实例已启动（略过当前启动处理）: serverId={}, serverPort={}, serverName={}", serverId, serverPort, serverName);
                return true;
            }
            String serverStatus = ServerStatus.START_SUCCESS.getCode();
            String startupErrLog = "";
            try {
                MRestServer restServer = new MRestServer(serverPort, serverName)
                        .bossThreadNum(1)
                        .workerThreadNum(2)
                        .context(server.getServerContext())
                        .filter("/*", new ServerRestFilter(serverId, restContext))
                        .getRestServer()
                        .start();
                serverMap.put(serverId, restServer);
                logger.info("启动Server实例处理成功: serverId={}, serverPort={}, serverName={}", serverId, serverPort, serverName);
            } catch (Throwable throwable) {
                logger.error("启动Server实例处理失败: serverId={}, serverPort={}, serverName={}", serverId, serverPort, serverName, throwable);
                serverStatus = ServerStatus.START_FAILED.getCode();
                startupErrLog = StringUtils.getExceptionStackTrace(throwable);
            } finally {
                updateServerStatus(serverId, serverStatus, startupErrLog);
            }
            return serverStatus.equals(ServerStatus.START_SUCCESS.getCode());
        } catch (Throwable throwable) {
            logger.error("启动Server实例处理异常: serverId={}", serverId, throwable);
        }
        return false;
    }

    public synchronized boolean stopServer(String serverId) {
        try {
            if (!serverMap.containsKey(serverId)) {
                logger.warn("Server实例已停止（略过当前停止处理）: serverId={}", serverId);
                return true;
            }
            try {
                serverMap.get(serverId).shutdown();
                serverMap.remove(serverId);
                logger.info("停止Server实例处理成功: serverId={}", serverId);
                updateServerStatus(serverId, ServerStatus.NOT_START.getCode(), "");
                return true;
            } catch (Throwable throwable) {
                logger.error("停止Server实例处理失败: serverId={}", serverId, throwable);
            }
        } catch (Throwable throwable) {
            logger.error("停止Server实例处理异常: serverId={}", serverId, throwable);
        }
        return false;
    }

    public synchronized boolean restartServer(String serverId) {
        stopServer(serverId);
        return startServer(serverId);
    }

    private void updateServerStatus(String serverId, String serverStatus, String startupErrLog) {
        try {
            TbServer server = serverContext.getTbServerService().findWithNoCache(serverId);
            if (server == null) {
                return;
            }
            // 更新启动状态至数据库
            serverContext.getTbServerService().getJdbcTemplate().executeUpdate(
                    "update tb_server set server_status=?, startup_err_log=? where server_id=?"
                    , statement -> {
                        statement.setString(1, serverStatus);
                        statement.setString(2, startupErrLog);
                        statement.setString(3, serverId);
                    });
        } catch (Throwable throwable) {
            logger.error("更新Server实例状态处理异常: serverId={}, serverStatus={}", serverId, serverStatus, throwable);
        }
    }

}
