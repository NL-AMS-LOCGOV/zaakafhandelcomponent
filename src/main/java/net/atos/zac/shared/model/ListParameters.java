package net.atos.zac.shared.model;

public class ListParameters {
    private Sorting sorting;

    private Paging paging;

    public ListParameters() {}

    public Sorting getSorting() {
        return sorting;
    }

    public void setSorting(final Sorting sorting) {
        this.sorting = sorting;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(final Paging paging) {
        this.paging = paging;
    }
}
