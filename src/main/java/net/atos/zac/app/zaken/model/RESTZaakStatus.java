/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.ZonedDateTime;

public class RESTZaakStatus {

    public ZonedDateTime toekenningsdatumTijd; /* datum van toekenning van de status op de zaak */

    public String toelichting; /* toelichting waarom de zaak in deze status staat */

    public String naam; /* naam van de status */

    public String naamGeneriek; /* generieke (interne?) naam van de status */

    public String statusToelichting; /* omschrijving van de status */

    public boolean eindStatus;

    public boolean informeren; /* indicatie die aangeeft of na het zetten van een status de initiator moet worden geïnformeerd over de statusovergang. */

    public String informerenStatusTekst; /* de tekst die wordt gebruikt om de initiator te informeren over het bereiken van een status */
}
