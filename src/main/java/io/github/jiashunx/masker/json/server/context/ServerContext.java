package io.github.jiashunx.masker.json.server.context;

import io.github.jiashunx.masker.json.server.engine.ServerEngine;
import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.model.outvo.PageQueryOutVo;
import io.github.jiashunx.masker.json.server.service.ArgumentService;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.json.server.type.ServerStatus;
import io.github.jiashunx.masker.rest.framework.util.MRestUtils;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * @author jiashunx
 */
public class ServerContext {

    private final ArgumentService argumentService;
    private final TbServerService tbServerService;
    private ServerEngine serverEngine;

    public ServerContext(ArgumentService argumentService, TbServerService tbServerService) {
        this.argumentService = Objects.requireNonNull(argumentService);
        this.tbServerService = Objects.requireNonNull(tbServerService);
    }

    public RestResult create(TbServer serverObj) {
        if (serverObj.getServerPort() == argumentService.getListenPort()) {
            return RestResult.failWithMessage(String.format("端口[%d]与当前服务端口冲突，请修改后提交", serverObj.getServerPort()));
        }
        TbServer samePortEntity = tbServerService.getJdbcTemplate().queryForObj("select * from tb_server where server_port=?", statement -> {
            statement.setInt(1, serverObj.getServerPort());
        }, TbServer.class);
        if (samePortEntity != null) {
            return RestResult.failWithMessage(String.format("端口[%d]与其他Server实例端口冲突，请修改后提交", serverObj.getServerPort()));
        }
        serverObj.setServerId(StringUtils.randomUUID());
        serverObj.setServerContext(MRestUtils.formatContextPath(serverObj.getServerContext()));
        serverObj.setServerStatus(ServerStatus.NOT_START.getCode());
        serverObj.setStartupErrLog("");
        serverObj.setCreateTime(new Date());
        serverObj.setLastModifyTime(serverObj.getCreateTime());
        tbServerService.insertWithNoCache(serverObj);
        // 启动Server
        serverEngine.startServer(serverObj.getServerId());
        return RestResult.ok();
    }

    public RestResult update(TbServer serverObj) {
        if (serverObj.getServerPort() == argumentService.getListenPort()) {
            return RestResult.failWithMessage(String.format("端口[%d]与当前服务端口冲突，请修改后提交", serverObj.getServerPort()));
        }
        TbServer entity = tbServerService.findWithNoCache(serverObj.getServerId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Server实例ID[%s]找不到对应记录", serverObj.getServerId()));
        }
        serverObj.setServerContext(MRestUtils.formatContextPath(serverObj.getServerContext()));
        serverObj.setServerStatus(entity.getServerStatus());
        serverObj.setStartupErrLog(entity.getStartupErrLog());
        serverObj.setCreateTime(entity.getCreateTime());
        serverObj.setLastModifyTime(new Date());
        // 若端口有更新，则查找新端口是否存在
        if (entity.getServerPort() != serverObj.getServerPort()) {
            TbServer samePortEntity = tbServerService.getJdbcTemplate().queryForObj("select * from tb_server where server_port=?", statement -> {
                statement.setInt(1, serverObj.getServerPort());
            }, TbServer.class);
            if (samePortEntity != null) {
                return RestResult.failWithMessage(String.format("端口[%d]与其他Server实例端口冲突，请修改后提交", serverObj.getServerPort()));
            }
        }
        tbServerService.updateWithNoCache(serverObj);
        // 重启Server
        serverEngine.restartServer(serverObj.getServerId());
        return RestResult.ok();
    }

    public RestResult delete(TbServer serverObj) {
        TbServer entity = tbServerService.findWithNoCache(serverObj.getServerId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Server实例ID[%s]找不到对应记录", serverObj.getServerId()));
        }
        tbServerService.deleteById(serverObj.getServerId());
        // 停止Server
        serverEngine.stopServer(serverObj.getServerId());
        return RestResult.ok();
    }

    public RestResult queryList(PageQueryVo pageQueryVo) {
        int total = tbServerService.getJdbcTemplate().queryForInt("select count(1) from tb_server");
        return RestResult.ok(new PageQueryOutVo<>().setTotal(total).setRecords(tbServerService.selectWithPage(pageQueryVo.getPageIndex(), pageQueryVo.getPageSize(), sql -> {
            sql.append(" order by last_modify_time desc ");
        }, statement -> {})));
    }

    public RestResult queryAll() {
        return RestResult.ok(tbServerService.selectFields(Collections.singletonList("server_id, server_name, server_port, server_context"), sql -> { sql.append(" order by server_name asc "); }, statement -> {}));
    }

    public TbServerService getTbServerService() {
        return tbServerService;
    }

    public ServerEngine getServerEngine() {
        return serverEngine;
    }

    public void setServerEngine(ServerEngine serverEngine) {
        this.serverEngine = serverEngine;
    }
}
