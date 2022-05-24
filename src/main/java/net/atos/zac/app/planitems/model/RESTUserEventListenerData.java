/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.UUID;

public class RESTUserEventListenerData {

    public UUID zaakUuid;

    public String planItemInstanceId;

    public UserEventListenerActie actie;

    public Boolean zaakOntvankelijk;

    public String resultaatToelichting;

    public UUID resultaattypeUuid;
}
