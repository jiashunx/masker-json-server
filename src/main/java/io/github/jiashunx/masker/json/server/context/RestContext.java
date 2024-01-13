package io.github.jiashunx.masker.json.server.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.model.outvo.PageQueryOutVo;
import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author jiashunx
 */
public class RestContext {

    private final TbRestService tbRestService;

    public RestContext(TbRestService tbRestService) {
        this.tbRestService = Objects.requireNonNull(tbRestService);
    }

    public RestResult create(TbRest restObj) {
        TbRest sameRest = tbRestService.getJdbcTemplate().queryForObj("select * from tb_rest where server_id=? and rest_url=?", statement -> {
            statement.setString(1, restObj.getServerId());
            statement.setString(2, restObj.getRestUrl());
        }, TbRest.class);
        if (sameRest != null) {
            return RestResult.failWithMessage(String.format("Server[%s]URL[%s]冲突，请修改后提交", restObj.getServerId(), restObj.getRestUrl()));
        }
        restObj.setServerId(StringUtils.randomUUID());
        restObj.setRestUrl(restObj.getRestUrl().trim());
        try {
            new ObjectMapper().readTree(restObj.getRestBody());
        } catch (Throwable throwable) {
            return RestResult.failWithMessage(String.format("Server[%s]URL[%s]响应报文不是JSON格式，请修改后提交", restObj.getServerId(), restObj.getRestUrl()));
        }
        restObj.setCreateTime(new Date());
        restObj.setLastModifyTime(restObj.getCreateTime());
        return RestResult.ok(tbRestService.insertWithNoCache(restObj));
    }

    public RestResult update(TbRest restObj) {
        TbRest entity = tbRestService.findWithNoCache(restObj.getRestId());
        if (entity == null) {
            return RestResult.failWithMessage(String.format("根据Rest接口实例ID[%s]找不到对应记录", restObj.getRestId()));
        }
        restObj.setRestUrl(restObj.getRestUrl());
        try {
            new ObjectMapper().readTree(restObj.getRestBody());
        } catch (Throwable throwable) {
            return RestResult.failWithMessage(String.format("Server[%s]URL[%s]响应报文不是JSON格式，请修改后提交", restObj.getServerId(), restObj.getRestUrl()));
        }
        restObj.setCreateTime(entity.getCreateTime());
        restObj.setLastModifyTime(new Date());
        // 若Server接口有更新，则查找新Server接口是否存在
        if (!entity.getServerId().equals(restObj.getServerId()) || !entity.getRestUrl().equals(restObj.getRestUrl())) {
            TbRest sameRest = tbRestService.getJdbcTemplate().queryForObj("select * from tb_rest where server_id=? and rest_url=?", statement -> {
                statement.setString(1, restObj.getServerId());
                statement.setString(2, restObj.getRestUrl());
            }, TbRest.class);
            if (sameRest != null) {
                return RestResult.failWithMessage(String.format("Server[%s]URL[%s]冲突，请修改后提交", restObj.getServerId(), restObj.getRestUrl()));
            }
        }
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
        int total = tbRestService.getJdbcTemplate().queryForInt("select count(1) from tb_server");
        return RestResult.ok(new PageQueryOutVo<>().setTotal(total).setRecords(tbRestService.selectWithPage(pageQueryVo.getPageIndex(), pageQueryVo.getPageSize(), sql -> {
            sql.append(" order by last_modify_time asc ");
        }, statement -> {})));
    }

    public TbRestService getTbRestService() {
        return tbRestService;
    }
}
