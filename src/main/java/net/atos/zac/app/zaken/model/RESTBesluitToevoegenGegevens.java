/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

public class RESTBesluitToevoegenGegevens {

    public UUID zaakUuid;

    public UUID resultaattypeUuid;

    public String toelichting;

    public LocalDate ingangsdatum;

    public LocalDate vervaldatum;

    public URI besluittypeURL;
}
