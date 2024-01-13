package io.github.jiashunx.masker.json.server.context;

import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * @author jiashunx
 */
public class ServerContext {

    private final TbServerService tbServerService;

    public ServerContext(TbServerService tbServerService) {
        this.tbServerService = Objects.requireNonNull(tbServerService);
    }

    public RestResult create(TbServer serverObj) {
        TbServer samePortEntity = tbServerService.getJdbcTemplate().queryForObj("select * from tb_server where server_port=?", statement -> {
            statement.setInt(1, serverObj.getServerPort());
        }, TbServer.class);
        if (samePortEntity != null) {
            return RestResult.failWithMessage(String.format("端口[%d]冲突，请修改后提交", serverObj.getServerPort()));
        }
        serverObj.setServerId(StringUtils.randomUUID());
        serverObj.setCreateTime(new Date());
        serverObj.setLastModifyTime(serverObj.getCreateTime());
        tbServerService.insertWithNoCache(serverObj);
        // 启动Server
        return RestResult.ok();
    }

    public RestResult update(TbServer serverObj) {
        TbServer entity = tbServerService.findWithNoCache(serverObj.getServerId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Server实例ID[%s]找不到对应记录", serverObj.getServerId()));
        }
        serverObj.setCreateTime(entity.getCreateTime());
        serverObj.setLastModifyTime(new Date());
        // 若端口有更新，则查找新端口是否存在
        if (entity.getServerPort() != serverObj.getServerPort()) {
            TbServer samePortEntity = tbServerService.getJdbcTemplate().queryForObj("select * from tb_server where server_port=?", statement -> {
                statement.setInt(1, serverObj.getServerPort());
            }, TbServer.class);
            if (samePortEntity != null) {
                return RestResult.failWithMessage(String.format("端口[%d]冲突，请修改后提交", serverObj.getServerPort()));
            }
        }
        tbServerService.updateWithNoCache(serverObj);
        // 重启Server
        return RestResult.ok();
    }

    public RestResult delete(TbServer serverObj) {
        TbServer entity = tbServerService.findWithNoCache(serverObj.getServerId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Server实例ID[%s]找不到对应记录", serverObj.getServerId()));
        }
        tbServerService.deleteById(serverObj.getServerId());
        // 停止Server
        return RestResult.ok();
    }

    public RestResult queryList(PageQueryVo pageQueryVo) {
        return RestResult.ok(tbServerService.selectWithPage(pageQueryVo.getPageIndex(), pageQueryVo.getPageSize(), sql -> {
            sql.append(" order by last_modify_time asc ");
        }, statement -> {}));
    }

    public RestResult queryAll() {
        return RestResult.ok(tbServerService.selectFields(Collections.singletonList("server_id, server_name"), sql -> { sql.append(" order by server_name asc "); }, statement -> {}));
    }

    public TbServerService getTbServerService() {
        return tbServerService;
    }
}
