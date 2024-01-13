package io.github.jiashunx.masker.json.server.model.outvo;

import java.util.List;

/**
 * @author jiashunx
 */
public class PageQueryOutVo<T> {

    private int total;

    private List<T> records;

    public int getTotal() {
        return total;
    }

    public PageQueryOutVo setTotal(int total) {
        this.total = total;
        return this;
    }

    public List<T> getRecords() {
        return records;
    }

    public PageQueryOutVo setRecords(List<T> records) {
        this.records = records;
        return this;
    }
}
