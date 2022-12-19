/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.klant;

public abstract class RESTKlant {

    public String emailadres;

    public String telefoonnummer;

    public abstract IdentificatieType getIdentificatieType();

    public abstract String getIdentificatie();

    public abstract String getNaam();

}
