/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.identity.model;

import java.util.List;

public class RESTIngelogdeMedewerker {

    private String gebruikersnaam;

    private String naam;

    private List<RESTGroep> groepen;

    public List<RESTGroep> getGroepen() {
        return groepen;
    }

    public void setGroepen(final List<RESTGroep> groepen) {
        this.groepen = groepen;
    }

    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(final String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }
}
