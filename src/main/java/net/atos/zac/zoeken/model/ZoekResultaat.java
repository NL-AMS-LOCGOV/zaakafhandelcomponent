package net.atos.zac.zoeken.model;

import java.util.Collection;
import java.util.List;

public class ZoekResultaat <TYPE> {

    final List<TYPE> items;

    final long count;

    public ZoekResultaat(final List<TYPE> items, final long count) {
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
