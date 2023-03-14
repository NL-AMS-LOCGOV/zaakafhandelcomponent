/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.util.UUID;

public class RESTTaakToekennenGegevens {

    public String taakId;

    public UUID zaakUuid;

    public String groepId;

    public String behandelaarId;

    // ToDo zaakafhandelcomponent-2131
    public String reden = "Test taak toekennen reden";
}
