/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RESTBesluitWijzigenGegevens {

    public UUID besluitUuid;

    public UUID resultaattypeUuid;

    public String toelichting;

    public LocalDate ingangsdatum;

    public LocalDate vervaldatum;

    public List<UUID> informatieobjecten;

    public String reden;

}
