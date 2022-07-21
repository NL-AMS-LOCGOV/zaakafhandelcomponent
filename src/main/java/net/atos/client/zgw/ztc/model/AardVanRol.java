/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(AardVanRol.Adapter.class)
public enum AardVanRol implements AbstractEnum<AardVanRol> {

    ADVISEUR("adviseur"),
    BEHANDELAAR("behandelaar"),
    BELANGHEBBENDE("belanghebbende"),
    BESLISSER("beslisser"),
    INITIATOR("initiator"),
    KLANTCONTACTER("klantcontacter"),
    ZAAKCOORDINATOR("zaakcoordinator"),
    MEDE_INITIATOR("mede_initiator");

    private static Set<AardVanRol> betrokkenen;

    private final String value;

    static {
        final EnumSet<AardVanRol> betrokkenen = EnumSet.allOf(AardVanRol.class);
        betrokkenen.remove(INITIATOR);
        betrokkenen.remove(BEHANDELAAR);
        AardVanRol.betrokkenen = Collections.unmodifiableSet(betrokkenen);
    }

    AardVanRol(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static AardVanRol fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    public static Set<AardVanRol> getBetrokkenen() {
        return betrokkenen;
    }

    static class Adapter extends AbstractEnum.Adapter<AardVanRol> {
        @Override
        protected AardVanRol[] getEnums() {
            return values();
        }
    }
}
