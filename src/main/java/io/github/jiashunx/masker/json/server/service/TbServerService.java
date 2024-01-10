package io.github.jiashunx.masker.json.server.service;

import io.github.jiashunx.masker.json.server.model.TbServer;
import io.github.jiashunx.sdk.sqlite3.mapping.SQLite3JdbcTemplate;
import io.github.jiashunx.sdk.sqlite3.mapping.service.SQLite3Service;

/**
 * @author jiashunx
 */
public class TbServerService extends SQLite3Service<TbServer, String> {

    public TbServerService(SQLite3JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, false);
    }

    @Override
    protected Class<TbServer> getEntityClass() {
        return TbServer.class;
    }
}
