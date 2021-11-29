/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.Objects;

public class RolVestiging extends Rol<Vestiging> {

    public RolVestiging() {
    }

    public RolVestiging(final URI zaak, final URI roltype, final String roltoelichting, final Vestiging betrokkeneIdentificatie) {
        super(zaak, roltype, BetrokkeneType.VESTIGING, betrokkeneIdentificatie, roltoelichting);
    }

    @Override
    protected boolean equalBetrokkeneIdentificatie(final Vestiging identificatie) {
        final Vestiging betrokkeneIdentificatie = getBetrokkeneIdentificatie();
        if (betrokkeneIdentificatie == identificatie) {
            return true;
        }
        if (identificatie == null) {
            return false;
        }
        return Objects.equals(betrokkeneIdentificatie.getVestigingsNummer(), identificatie.getVestigingsNummer());
    }

    @Override
    public String getNaam() {
        final Vestiging ves = getBetrokkeneIdentificatie();
        return ves == null ? null : ves.getVestigingsNummer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getVestigingsNummer());
    }

}
