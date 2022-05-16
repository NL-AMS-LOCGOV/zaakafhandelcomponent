/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.index;

public class HerindexeerInfo {
    int toevoegen; // Nog niet in de Solr-index

    int herindexeren; // Reeds in Solr-index

    int verwijderen; // In de Solr-index, maar niet meer in Open-Zaak

    public HerindexeerInfo() {
    }

    public int getToevoegen() {
        return toevoegen;
    }

    public void setToevoegen(final int toevoegen) {
        this.toevoegen = toevoegen;
    }

    public int getHerindexeren() {
        return herindexeren;
    }

    public void setHerindexeren(final int herindexeren) {
        this.herindexeren = herindexeren;
    }

    public int getVerwijderen() {
        return verwijderen;
    }

    public void setVerwijderen(final int verwijderen) {
        this.verwijderen = verwijderen;
    }
}
