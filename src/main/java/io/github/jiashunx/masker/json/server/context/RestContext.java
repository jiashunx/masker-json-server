package io.github.jiashunx.masker.json.server.context;

import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.masker.json.server.model.invo.PageQueryVo;
import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.json.server.service.TbServerService;

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
        return null;
    }

    public RestResult update(TbRest restObj) {
        return null;
    }

    public RestResult delete(TbRest restObj) {
        return null;
    }

    public RestResult queryList(PageQueryVo pageQueryVo) {
        return null;
    }

    public RestResult queryOne(TbRest restObj) {
        return null;
    }

}
