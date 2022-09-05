/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import javax.json.bind.annotation.JsonbProperty;

public class ZaakData {

    public boolean open;

    public boolean heropend;

    public boolean opgeschort;

    public String behandelaar;

    public String zaaktype;

    public boolean ontvangstbevestigingVerstuurd;

    public boolean besluit;

    @JsonbProperty("heeft_besluittypen")
    public boolean heeftBesluittypen;
}
