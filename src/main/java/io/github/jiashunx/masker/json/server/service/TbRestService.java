package io.github.jiashunx.masker.json.server.service;

import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.sdk.sqlite3.mapping.SQLite3JdbcTemplate;
import io.github.jiashunx.sdk.sqlite3.mapping.service.SQLite3Service;

import java.util.Collections;
import java.util.List;

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

    public List<TbRest> queryByServerId(String serverId) {
        return selectFields(Collections.singletonList("rest_url"), sql -> {
            sql.append(" where server_id=? ");
        }, statement -> {
            statement.setString(1, serverId);
        });
    }

    public List<TbRest> queryByServerIdAndRestUrl(String serverId, String restUrl) {
        return getJdbcTemplate().queryForList("select * from tb_rest where server_id=? and rest_url=?", statement -> {
            statement.setString(1, serverId);
            statement.setString(2, restUrl);
        }, TbRest.class);
    }
}
