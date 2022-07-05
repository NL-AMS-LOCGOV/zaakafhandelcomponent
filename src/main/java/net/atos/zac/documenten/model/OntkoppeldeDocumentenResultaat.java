package net.atos.zac.documenten.model;

import java.util.List;

import net.atos.zac.shared.model.Resultaat;

public class OntkoppeldeDocumentenResultaat extends Resultaat<OntkoppeldDocument> {

    List<String> ontkoppeldDoorFilter;

    public OntkoppeldeDocumentenResultaat(final List<OntkoppeldDocument> items, final long count, final List<String> ontkoppeldDoorFilter) {
        super(items, count);
        this.ontkoppeldDoorFilter = ontkoppeldDoorFilter;
    }

    public List<String> getOntkoppeldDoorFilter() {
        return ontkoppeldDoorFilter;
    }
}
