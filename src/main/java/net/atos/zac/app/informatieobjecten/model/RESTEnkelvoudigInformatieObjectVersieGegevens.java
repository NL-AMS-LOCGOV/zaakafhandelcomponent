/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;
import java.util.UUID;

import net.atos.zac.app.configuratie.model.RESTTaal;

/**
 *
 */
public class RESTEnkelvoudigInformatieObjectVersieGegevens {

    public UUID uuid;

    public UUID zaakUuid;

    public String titel;

    public String vertrouwelijkheidaanduiding;

    public String auteur;

    public String status;

    public RESTTaal taal;

    public String bestandsnaam;

    public String beschrijving;

    public LocalDate verzenddatum;

    public LocalDate ontvangstdatum;

    public String toelichting;
}
