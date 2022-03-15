/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.util.List;

public class Vestiging {

    /**
     * Een korte unieke aanduiding van de Vestiging.
     * - maxLength: 24
     */
    private String vestigingsNummer;

    /**
     * De naam van de vestiging waaronder gehandeld wordt.
     * - maxLength: 625
     */
    private List<String> handelsnaam;

    private VerblijfsAdres verblijfsadres;

    private SubVerblijfBuitenland subVerblijfBuitenland;

    public Vestiging() {
    }

    public Vestiging(final String vestigingsNummer) {
        this.vestigingsNummer = vestigingsNummer;
    }

    public String getVestigingsNummer() {
        return vestigingsNummer;
    }

    public void setVestigingsNummer(final String vestigingsNummer) {
        this.vestigingsNummer = vestigingsNummer;
    }

    public List<String> getHandelsnaam() {
        return handelsnaam;
    }

    public void setHandelsnaam(final List<String> handelsnaam) {
        this.handelsnaam = handelsnaam;
    }

    public VerblijfsAdres getVerblijfsadres() {
        return verblijfsadres;
    }

    public void setVerblijfsadres(final VerblijfsAdres verblijfsadres) {
        this.verblijfsadres = verblijfsadres;
    }

    public SubVerblijfBuitenland getSubVerblijfBuitenland() {
        return subVerblijfBuitenland;
    }

    public void setSubVerblijfBuitenland(final SubVerblijfBuitenland subVerblijfBuitenland) {
        this.subVerblijfBuitenland = subVerblijfBuitenland;
    }
}
