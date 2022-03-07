package net.atos.zac.shared.model;

public class Paging {

    private final int page;

    private final int maxResults;

    public Paging(final int page, final int count) {
        this.page = page;
        this.maxResults = count;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public int getFirstResult() {
        return page * maxResults;
    }
}
