/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import javax.json.bind.annotation.JsonbProperty;

public class DocumentData {

    public boolean definitief;

    public boolean vergrendeld;

    @JsonbProperty("vergrendeld_door")
    public String vergrendeldDoor;

    public String zaaktype;

    @JsonbProperty("zaak_open")
    public Boolean zaakOpen;

    public boolean ondertekend;
}
