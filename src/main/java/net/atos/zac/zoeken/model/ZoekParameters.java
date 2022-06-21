/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class ZoekParameters {

    private int rows;

    private int start;

    private final ZoekObjectType type;

    private EnumMap<ZoekVeld, String> zoeken = new EnumMap<>(ZoekVeld.class);

    private EnumMap<DatumVeld, DatumRange> datums = new EnumMap<>(DatumVeld.class);

    private final EnumMap<FilterVeld, String> filters = new EnumMap<>(FilterVeld.class);

    private final HashMap<String, String> filterQueries = new HashMap<>();

    private Sortering sortering = new Sortering(SorteerVeld.IDENTIFICATIE, SorteerRichting.DESCENDING);

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

    public EnumMap<DatumVeld, DatumRange> getDatums() {
        return datums;
    }

    public void setDatums(final EnumMap<DatumVeld, DatumRange> datums) {
        this.datums = datums;
    }

    public void addDatum(final DatumVeld zoekVeld, final DatumRange range) {
        this.datums.put(zoekVeld, range);
    }


    public EnumSet<FilterVeld> getBeschikbareFilters() {
        return switch (type) {
            case ZAAK -> FilterVeld.ZAAK_FACETTEN;
            case TAAK -> FilterVeld.TAAK_FACETTEN;
            case INFORMATIE_OBJECT -> FilterVeld.INFORMATIE_OBJECT_FACETTEN;
        };
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

    public void addFilterQuery(final String veld, final String waarde) {
        this.filterQueries.put(veld, waarde);
    }

    public HashMap<String, String> getFilterQueries() {
        return filterQueries;
    }

    public Sortering getSortering() {
        return sortering;
    }

    public void setSortering(final SorteerVeld veld, SorteerRichting richting) {
        this.sortering = new Sortering(veld, richting);
    }

    public ZoekObjectType getType() {
        return type;
    }
}
