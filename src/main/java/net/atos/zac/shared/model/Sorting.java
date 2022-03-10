package net.atos.zac.shared.model;

public class Sorting {
    private final String field;

    private final SortDirection direction;

    public Sorting(final String field) {
        this.field = field;
        this.direction = SortDirection.DESCENDING;
    }

    public Sorting(final String field, final SortDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public SortDirection getDirection() {
        return direction;
    }
}
