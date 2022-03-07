/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail.model;

import javax.json.bind.annotation.JsonbProperty;

public class Ontvanger {

    @JsonbProperty("Email")
    private String ontvanger;

    public Ontvanger(final String ontvanger) {
        this.ontvanger = ontvanger;
    }

    public String getOntvanger() {
        return ontvanger;
    }

    public void setOntvanger(final String ontvanger) {
        this.ontvanger = ontvanger;
    }
}
