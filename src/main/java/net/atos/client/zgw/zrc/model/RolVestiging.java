/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.ztc.model.Roltype;

public class RolVestiging extends Rol<Vestiging> {

    public RolVestiging() {
    }

    public RolVestiging(final URI zaak, final Roltype roltype, final String roltoelichting, final Vestiging betrokkeneIdentificatie) {
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
        if (getBetrokkeneIdentificatie() == null) {
            return null;
        }
        final String namen = getBetrokkeneIdentificatie().getHandelsnaam() != null
                ? String.join("; ", getBetrokkeneIdentificatie().getHandelsnaam())
                : null;
        return StringUtils.isNotEmpty(namen)
                ? namen
                : getIdentificatienummer();
    }

    @Override
    public String getIdentificatienummer() {
        if (getBetrokkeneIdentificatie() == null) {
            return null;
        }
        return getBetrokkeneIdentificatie().getVestigingsNummer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getVestigingsNummer());
    }

}
