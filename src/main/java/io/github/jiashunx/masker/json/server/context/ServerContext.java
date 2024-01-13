package io.github.jiashunx.masker.json.server.context;

import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;

import java.util.Objects;

/**
 * @author jiashunx
 */
public class ServerContext {

    private final TbServerService tbServerService;
    private final TbRestService tbRestService;

    public ServerContext(TbServerService tbServerService, TbRestService tbRestService) {
        this.tbServerService = Objects.requireNonNull(tbServerService);
        this.tbRestService = Objects.requireNonNull(tbRestService);
    }

    public RestResult create(TbServer serverObj) {
        return null;
    }

    public RestResult update(TbServer serverObj) {
        return null;
    }

    public RestResult delete(TbServer serverObj) {
        return null;
    }

    public RestResult queryList(PageQueryVo pageQueryVo) {
        return null;
    }

    public RestResult queryOne(TbServer serverObj) {
        return null;
    }

}
