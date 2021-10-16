/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Lijst van kenmerken. Merk op dat refereren naar gerelateerde objecten
 * beter kan via `ZaakObject`.
 */
public class ZaakKenmerk {

    public static final int KENMERK_MAX_LENGTH = 40;

    public static final int BRON_MAX_LENGTH = 40;

    /**
     * Identificeert uniek de zaak in een andere administratie.
     * <p>
     * maxLength: {@link ZaakKenmerk#KENMERK_MAX_LENGTH}
     */
    private final String kenmerk;

    /**
     * De aanduiding van de administratie waar het kenmerk op slaat.
     * <p>
     * maxLength: {@link ZaakKenmerk#BRON_MAX_LENGTH}
     */
    private final String bron;

    /**
     * Constructor with required attributes for POST and PUT requests and GET response
     */
    @JsonbCreator
    public ZaakKenmerk(@JsonbProperty("kenmerk") final String kenmerk,
            @JsonbProperty("bron") final String bron) {
        this.kenmerk = kenmerk;
        this.bron = bron;
    }

    public String getKenmerk() {
        return kenmerk;
    }

    public String getBron() {
        return bron;
    }
}
