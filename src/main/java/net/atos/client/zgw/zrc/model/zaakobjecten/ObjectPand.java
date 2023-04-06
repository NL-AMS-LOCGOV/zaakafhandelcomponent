/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

/**
 * ObjectPand
 */
public class ObjectPand extends ObjectBAGObject {

    /**
     * Constructor for JSONB deserialization
     */
    public ObjectPand() {
    }

    /**
     * Constructor with required attributes
     */
    public ObjectPand(final String identificatie) {
        super(identificatie);
    }

}
