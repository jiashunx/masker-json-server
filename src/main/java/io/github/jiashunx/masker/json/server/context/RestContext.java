package io.github.jiashunx.masker.json.server.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.model.outvo.PageQueryOutVo;
import io.github.jiashunx.masker.json.server.model.outvo.TbRestOutVo;
import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;
import io.github.jiashunx.masker.rest.framework.util.MRestUtils;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author jiashunx
 */
public class RestContext {

    private final TbServerService tbServerService;
    private final TbRestService tbRestService;

    public RestContext(TbServerService tbServerService, TbRestService tbRestService) {
        this.tbServerService = Objects.requireNonNull(tbServerService);
        this.tbRestService = Objects.requireNonNull(tbRestService);
    }

    public RestResult create(TbRest restObj) {
        TbServer serverEntity = tbServerService.findWithNoCache(restObj.getServerId());
        if (serverEntity == null) {
            return RestResult.failWithMessage(String.format("根据Server实例ID[%s]找不到对应记录", restObj.getServerId()));
        }
        restObj.setRestId(StringUtils.randomUUID());
        restObj.setRestUrl(MRestUtils.formatPath(restObj.getRestUrl()));
        try {
            new ObjectMapper().readTree(restObj.getRestBody());
        } catch (Throwable throwable) {
            return RestResult.failWithMessage(String.format("Server[%s]URL[%s]响应报文不是标准格式JSON，请修改后提交", restObj.getServerId(), restObj.getRestUrl()));
        }
        restObj.setCreateTime(new Date());
        restObj.setLastModifyTime(restObj.getCreateTime());
        return RestResult.ok(tbRestService.insertWithNoCache(restObj));
    }

    public RestResult update(TbRest restObj) {
        TbServer serverEntity = tbServerService.findWithNoCache(restObj.getServerId());
        if (serverEntity == null) {
            return RestResult.failWithMessage(String.format("根据Server实例ID[%s]找不到对应记录", restObj.getServerId()));
        }
        TbRest entity = tbRestService.findWithNoCache(restObj.getRestId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Rest接口实例ID[%s]找不到对应记录", restObj.getRestId()));
        }
        restObj.setRestUrl(MRestUtils.formatPath(restObj.getRestUrl()));
        try {
            new ObjectMapper().readTree(restObj.getRestBody());
        } catch (Throwable throwable) {
            return RestResult.failWithMessage(String.format("Server[%s]URL[%s]响应报文不是标准格式JSON，请修改后提交", restObj.getServerId(), restObj.getRestUrl()));
        }
        restObj.setCreateTime(entity.getCreateTime());
        restObj.setLastModifyTime(new Date());
        tbRestService.updateWithNoCache(restObj);
        return RestResult.ok();
    }

    public RestResult delete(TbRest restObj) {
        TbRest entity = tbRestService.findWithNoCache(restObj.getRestId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Rest接口实例ID[%s]找不到对应记录", restObj.getRestId()));
        }
        tbRestService.deleteById(restObj.getRestId());
        return RestResult.ok();
    }

    public RestResult queryList(PageQueryVo pageQueryVo) {
        int total = tbRestService.getJdbcTemplate().queryForInt("select count(1) from tb_rest");
        return RestResult.ok(new PageQueryOutVo<>().setTotal(total).setRecords(tbRestService.getJdbcTemplate().queryForList(
               "select a.rest_id,a.rest_name,a.server_id,a.rest_url,a.rest_body,a.expression,a.proxy_enabled,a.proxy_server,a.create_time,a.last_modify_time,b.server_port,b.server_context " +
                    "from tb_rest a " +
                    "left join tb_server b on a.server_id = b.server_id " +
                    "order by a.last_modify_time desc " +
                    "limit " + (pageQueryVo.getPageIndex() - 1) * pageQueryVo.getPageSize() + ", " + pageQueryVo.getPageSize()
                , statement -> {}
                , TbRestOutVo.class)));
    }

    public TbServerService getTbServerService() {
        return tbServerService;
    }

    public TbRestService getTbRestService() {
        return tbRestService;
    }
}
