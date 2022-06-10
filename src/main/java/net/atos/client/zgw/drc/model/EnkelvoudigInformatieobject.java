/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import java.net.URI;

/**
 * Representation of EnkelvoudigInformatieobject for GET response only
 */
public class EnkelvoudigInformatieobject extends AbstractEnkelvoudigInformatieobject {

    /**
     * Download URL van de binaire inhoud.
     */
    private URI inhoud;

    public URI getInhoud() {
        return inhoud;
    }

    public void setInhoud(final URI inhoud) {
        this.inhoud = inhoud;
    }
}
