package net.atos.zac.shared.model;


import java.util.List;


public class Resultaat<TYPE> {

    final List<TYPE> items;

    final long count;

    public Resultaat(final List<TYPE> items, final long count) {
        this.items = items;
        this.count = count;
    }

    public List<TYPE> getItems() {
        return items;
    }

    public long getCount() {
        return count;
    }
}
