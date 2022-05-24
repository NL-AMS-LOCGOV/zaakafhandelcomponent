/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class ZoekParameters {

    private int rows;

    private int start;

    private final ZoekObjectType type;

    private EnumMap<ZoekVeld, String> zoeken = new EnumMap<>(ZoekVeld.class);

    private final EnumMap<FilterVeld, String> filters = new EnumMap<>(FilterVeld.class);

    private Sortering sorteren = new Sortering(SorteerVeld.IDENTIFICATIE, SorteerRichting.DESCENDING);

    public ZoekParameters(final ZoekObjectType type) {
        this.type = type;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(final int rows) {
        this.rows = rows;
    }

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public Map<ZoekVeld, String> getZoeken() {
        return zoeken;
    }

    public void setZoeken(final EnumMap<ZoekVeld, String> zoeken) {
        this.zoeken = zoeken;
    }

    public void addZoekVeld(final ZoekVeld zoekVeld, final String zoekTekst) {
        this.zoeken.put(zoekVeld, zoekTekst);
    }

    public EnumSet<FilterVeld> getBeschikbareFilters() {
        switch (type) {
            case ZAAK -> {
                return EnumSet.of(FilterVeld.ZAAK_ZAAKTYPE, FilterVeld.ZAAK_STATUS, FilterVeld.ZAAK_BEHANDELAAR, FilterVeld.ZAAK_GROEP);
            }
            case INFORMATIE_OBJECT -> {
                return EnumSet.noneOf(FilterVeld.class);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public EnumMap<FilterVeld, String> getFilters() {
        return filters;
    }

    public void setFilters(final EnumMap<FilterVeld, String> filters) {
        this.filters.clear();
        this.filters.putAll(filters);
    }

    public void addFilter(FilterVeld veld, String waarde) {
        this.filters.put(veld, waarde);
    }

    public Sortering getSorteren() {
        return sorteren;
    }

    public void setSortering(final SorteerVeld veld, SorteerRichting richting) {
        this.sorteren = new Sortering(veld, richting);
    }
}
