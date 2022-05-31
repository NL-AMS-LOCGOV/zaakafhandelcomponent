package net.atos.zac.shared.model;

public class Sorting {
    private final String field;

    private final SorteerRichting direction;

    public Sorting(final String field) {
        this.field = field;
        this.direction = SorteerRichting.DESCENDING;
    }

    public Sorting(final String field, final SorteerRichting direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public SorteerRichting getDirection() {
        return direction;
    }
}
