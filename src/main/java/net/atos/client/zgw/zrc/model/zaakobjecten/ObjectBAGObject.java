/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

/**
 * ObjectAdres
 */
public abstract class ObjectBAGObject {

    /**
     * De unieke identificatie van het OBJECT
     * - maxLength: 100
     * - required
     */
    private String identificatie;

    /**
     * Constructor for JSONB deserialization
     */
    public ObjectBAGObject() {
    }

    /**
     * Constructor with required attributes
     */
    public ObjectBAGObject(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }
}
