package io.github.jiashunx.masker.json.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.jiashunx.sdk.sqlite3.metadata.annotation.SQLite3Column;
import io.github.jiashunx.sdk.sqlite3.metadata.annotation.SQLite3Id;
import io.github.jiashunx.sdk.sqlite3.metadata.annotation.SQLite3Table;

import java.util.Date;

/**
 * @author jiashunx
 */
@SQLite3Table(tableName = "tb_rest")
public class TbRest {

    /**
     * Rest接口实例ID
     */
    @SQLite3Id
    @SQLite3Column(columnName = "rest_id")
    private String restId;

    /**
     * Rest接口名称
     */
    @SQLite3Column(columnName = "rest_name")
    private String restName;

    /**
     * Server实例ID
     */
    @SQLite3Column(columnName = "server_id")
    private String serverId;

    /**
     * Rest接口URL
     */
    @SQLite3Column(columnName = "rest_url")
    private String restUrl;

    /**
     * Rest接口响应JSON
     */
    @SQLite3Column(columnName = "rest_body")
    private String restBody;

    /**
     * aviator表达式
     */
    @SQLite3Column(columnName = "expression")
    private String expression;

    /**
     * Rest接口代理是否开启（0-否，1-是），默认0-否
     */
    @SQLite3Column(columnName = "proxy_enabled")
    private String proxyEnabled = "0";

    /**
     * Rest接口代理Server
     */
    @SQLite3Column(columnName = "proxy_server")
    private String proxyServer;

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

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getRestBody() {
        return restBody;
    }

    public void setRestBody(String restBody) {
        this.restBody = restBody;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Deprecated
    public String getProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(String proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public boolean isProxyEnabled() {
        return "1".equals(this.proxyEnabled);
    }

    /*public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled ? "1" : "0";
    }*/

    public String getProxyServer() {
        return proxyServer;
    }

    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
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
