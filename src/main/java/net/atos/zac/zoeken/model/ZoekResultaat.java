package net.atos.zac.zoeken.model;

import java.util.EnumMap;
import java.util.List;

public class ZoekResultaat<TYPE> {

    final List<TYPE> items;

    final long count;

    final EnumMap<FilterVeld, List<String>> filters = new EnumMap<>(FilterVeld.class);

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

    public EnumMap<FilterVeld, List<String>> getFilters() {
        return filters;
    }

    public void addFilter(final FilterVeld facetVeld, final List<String> waardes) {
        filters.put(facetVeld, waardes);
    }
}
