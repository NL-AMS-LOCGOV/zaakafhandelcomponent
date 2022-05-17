/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import net.atos.zac.app.configuratie.model.RESTTaal;

import java.time.LocalDate;

/**
 *
 */
public class RESTEnkelvoudigInformatieObjectVersieGegevens {

    public String uuid;

    public String zaakUuid;

    public String titel;

    public String vertrouwelijkheidaanduiding;

    public String auteur;

    public String status;

    public RESTTaal taal;

    public String bestandsnaam;

    public String beschrijving;

    public LocalDate verzenddatum;

    public String toelichting;
}
