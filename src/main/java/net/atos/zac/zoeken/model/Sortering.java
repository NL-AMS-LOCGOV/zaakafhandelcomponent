package net.atos.zac.zoeken.model;

import net.atos.zac.shared.model.SorteerRichting;

public class Sortering {
    private final SorteerVeld sorteerVeld;

    private final SorteerRichting richting;

    public Sortering(final SorteerVeld sorteerVeld, final SorteerRichting richting) {
        this.sorteerVeld = sorteerVeld;
        this.richting = richting;
    }

    public SorteerVeld getSorteerVeld() {
        return sorteerVeld;
    }

    public SorteerRichting getRichting() {
        return richting;
    }
}
