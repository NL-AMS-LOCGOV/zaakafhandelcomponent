/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.model;

import java.time.LocalDate;

import net.atos.zac.app.shared.RESTListParameters;

public class RESTOntkoppeldDocumentListParameters extends RESTListParameters {
    public String titel;

    public String identificatie;

    public LocalDate creatiedatum;

    public String ontkoppeldDoor;

    public String zaakId;

    public LocalDate ontkoppeldOp;
}
