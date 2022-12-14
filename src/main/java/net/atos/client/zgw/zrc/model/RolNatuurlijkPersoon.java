/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.ztc.model.Roltype;

public class RolNatuurlijkPersoon extends Rol<NatuurlijkPersoon> {

    public RolNatuurlijkPersoon() {
    }

    public RolNatuurlijkPersoon(final URI zaak, final Roltype roltype, final String roltoelichting, final NatuurlijkPersoon betrokkeneIdentificatie) {
        super(zaak, roltype, BetrokkeneType.NATUURLIJK_PERSOON, betrokkeneIdentificatie, roltoelichting);
    }

    @Override
    protected boolean equalBetrokkeneIdentificatie(final NatuurlijkPersoon identificatie) {
        final NatuurlijkPersoon betrokkeneIdentificatie = getBetrokkeneIdentificatie();
        if (betrokkeneIdentificatie == identificatie) {
            return true;
        }
        if (identificatie == null) {
            return false;
        }
        // In volgorde van voorkeur (als er 1 matcht wordt de rest overgeslagen)
        if (betrokkeneIdentificatie.getAnpIdentificatie() != null || identificatie.getAnpIdentificatie() != null) {
            return Objects.equals(betrokkeneIdentificatie.getAnpIdentificatie(), identificatie.getAnpIdentificatie());
        }
        if (betrokkeneIdentificatie.getInpA_nummer() != null || identificatie.getInpA_nummer() != null) {
            return Objects.equals(betrokkeneIdentificatie.getInpA_nummer(), identificatie.getInpA_nummer());
        }
        if (betrokkeneIdentificatie.getInpBsn() != null || identificatie.getInpBsn() != null) {
            return Objects.equals(betrokkeneIdentificatie.getInpBsn(), identificatie.getInpBsn());
        }
        return true;
    }

    @Override
    public String getNaam() {
        if (getBetrokkeneIdentificatie() == null) {
            return null;
        }
        return StringUtils.isNotEmpty(getBetrokkeneIdentificatie().getVoorvoegselGeslachtsnaam())
                ? getBetrokkeneIdentificatie().getVoorvoegselGeslachtsnaam()
                : getIdentificatienummer();
    }

    @Override
    public String getIdentificatienummer() {
        if (getBetrokkeneIdentificatie() == null) {
            return null;
        }
        return getBetrokkeneIdentificatie().getInpBsn();
    }

    @Override
    public int hashCode() {
        if (getBetrokkeneIdentificatie().getAnpIdentificatie() != null) {
            return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getAnpIdentificatie());
        }
        if (getBetrokkeneIdentificatie().getInpA_nummer() != null) {
            return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getInpA_nummer());
        }
        if (getBetrokkeneIdentificatie().getInpBsn() != null) {
            return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getInpBsn());
        }
        return Objects.hash(getRoltype(), getBetrokkeneType(), null);
    }
}
