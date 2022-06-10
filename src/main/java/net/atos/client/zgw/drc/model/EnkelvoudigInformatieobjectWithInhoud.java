/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import java.util.Base64;

import javax.json.bind.annotation.JsonbTransient;

/**
 * Representation of EnkelvoudigInformatieobject for POST request and response only
 */
public class EnkelvoudigInformatieobjectWithInhoud extends AbstractEnkelvoudigInformatieobject {

    /**
     * Binaire inhoud, in base64 ge-encodeerd.
     */
    private String inhoud;

    public String getInhoud() {
        return inhoud;
    }

    public void setInhoud(final String inhoud) {
        this.inhoud = inhoud;
    }

    @JsonbTransient
    public void setInhoud(final byte[] inhoud) {
        this.inhoud = Base64.getEncoder().encodeToString(inhoud);
    }
}
