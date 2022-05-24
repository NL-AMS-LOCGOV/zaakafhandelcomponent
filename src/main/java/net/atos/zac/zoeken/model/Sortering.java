package net.atos.zac.zoeken.model;

import net.atos.zac.shared.model.SortDirection;

public class Sortering {
    private final SorteerVeld sorteerVeld;

    private final SortDirection richting;

    public Sortering(final SorteerVeld sorteerVeld, final SortDirection richting) {
        this.sorteerVeld = sorteerVeld;
        this.richting = richting;
    }

    public SorteerVeld getSorteerVeld() {
        return sorteerVeld;
    }

    public SortDirection getRichting() {
        return richting;
    }
}
