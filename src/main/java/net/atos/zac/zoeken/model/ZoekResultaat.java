package net.atos.zac.zoeken.model;

import java.util.EnumMap;
import java.util.List;

import net.atos.zac.shared.model.Resultaat;

public class ZoekResultaat<TYPE> extends Resultaat<TYPE> {

    private final EnumMap<FilterVeld, List<FilterResultaat>> filters = new EnumMap<>(FilterVeld.class);

    public ZoekResultaat(final List<TYPE> items, final long count) {
        super(items, count);
    }

    public EnumMap<FilterVeld, List<FilterResultaat>> getFilters() {
        return filters;
    }

    public void addFilter(final FilterVeld facetVeld, final List<FilterResultaat> waardes) {
        filters.put(facetVeld, waardes);
    }
}
