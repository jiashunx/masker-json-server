package io.github.jiashunx.masker.json.server.service;

import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.sdk.sqlite3.mapping.SQLite3JdbcTemplate;
import io.github.jiashunx.sdk.sqlite3.mapping.service.SQLite3Service;

/**
 * @author jiashunx
 */
public class TbRestService extends SQLite3Service<TbRest, String> {

    public TbRestService(SQLite3JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, false);
    }

    @Override
    protected Class<TbRest> getEntityClass() {
        return TbRest.class;
    }
}
