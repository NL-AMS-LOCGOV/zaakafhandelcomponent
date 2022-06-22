/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.index;

public record HerindexeerInfo(int toevoegen, int herindexeren, int verwijderen) {

    /**
     * @return aantal nog niet in de Solr-index
     */
    public int getToevoegen() {
        return toevoegen;
    }

    /**
     * @return aantal reeds in Solr-index
     */
    public int getHerindexeren() {
        return herindexeren;
    }

    /**
     * @return In de Solr-index, maar niet meer in de bron
     */
    public int getVerwijderen() {
        return verwijderen;
    }
}
