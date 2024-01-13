package io.github.jiashunx.masker.json.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.jiashunx.sdk.sqlite3.metadata.annotation.SQLite3Column;
import io.github.jiashunx.sdk.sqlite3.metadata.annotation.SQLite3Id;
import io.github.jiashunx.sdk.sqlite3.metadata.annotation.SQLite3Table;

import java.util.Date;

/**
 * @author jiashunx
 */
@SQLite3Table(tableName = "tb_server")
public class TbServer {

    /**
     * Server实例ID
     */
    @SQLite3Id
    @SQLite3Column(columnName = "server_id")
    private String serverId;

    /**
     * Server名称
     */
    @SQLite3Column(columnName = "server_name")
    private String serverName;

    /**
     * Server服务端口
     */
    @SQLite3Column(columnName = "server_port")
    private int serverPort;

    /**
     * Server的context-path, 默认: "/"
     */
    @SQLite3Column(columnName = "server_context")
    private String serverContext;

    /**
     * Server当前状态: 0-未启动, 1-启动失败, 2-启动成功
     */
    @SQLite3Column(columnName = "server_status")
    private String serverStatus;

    /**
     * Server启动错误日志
     */
    @SQLite3Column(columnName = "startup_err_log")
    private String startupErrLog;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLite3Column(columnName = "create_time")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLite3Column(columnName = "last_modify_time")
    private Date lastModifyTime;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerContext() {
        return serverContext;
    }

    public void setServerContext(String serverContext) {
        this.serverContext = serverContext;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getStartupErrLog() {
        return startupErrLog;
    }

    public void setStartupErrLog(String startupErrLog) {
        this.startupErrLog = startupErrLog;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}
