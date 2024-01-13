package io.github.jiashunx.masker.json.server.model.invo;

/**
 * @author jiashunx
 */
public class PageQueryVo {

    /**
     * 当前查询页索引（从1开始）
     */
    private int pageIndex = 1;

    /**
     * 当前页查询条数
     */
    private int pageSize = 10;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
