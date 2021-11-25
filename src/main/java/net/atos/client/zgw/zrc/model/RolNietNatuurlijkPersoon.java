/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.Objects;

public class RolNietNatuurlijkPersoon extends Rol<NietNatuurlijkPersoon> {

    public RolNietNatuurlijkPersoon() {
    }

    public RolNietNatuurlijkPersoon(final URI zaak, final URI roltype, final String roltoelichting, final NietNatuurlijkPersoon betrokkeneIdentificatie) {
        super(zaak, roltype, BetrokkeneType.NIET_NATUURLIJK_PERSOON, betrokkeneIdentificatie, roltoelichting);
    }

    @Override
    protected boolean equalBetrokkeneIdentificatie(final NietNatuurlijkPersoon identificatie) {
        final NietNatuurlijkPersoon betrokkeneIdentificatie = getBetrokkeneIdentificatie();
        if (betrokkeneIdentificatie == identificatie) {
            return true;
        }
        if (identificatie == null) {
            return false;
        }
        // In volgorde van voorkeur (als er 1 matcht wordt de rest overgeslagen)
        if (betrokkeneIdentificatie.getAnnIdentificatie() != null || identificatie.getAnnIdentificatie() != null) {
            return Objects.equals(betrokkeneIdentificatie.getAnnIdentificatie(), identificatie.getAnnIdentificatie());
        }
        if (betrokkeneIdentificatie.getInnNnpId() != null || identificatie.getInnNnpId() != null) {
            return Objects.equals(betrokkeneIdentificatie.getInnNnpId(), identificatie.getInnNnpId());
        }
        return true;
    }

    @Override
    public String getNaam() {
        final NietNatuurlijkPersoon nnp = getBetrokkeneIdentificatie();
        if (nnp == null) {
            return "-";
        }
        return nnp.getStatutaireNaam();
    }

    @Override
    public int hashCode() {
        if (getBetrokkeneIdentificatie().getAnnIdentificatie() != null) {
            return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getAnnIdentificatie());
        }
        if (getBetrokkeneIdentificatie().getInnNnpId() != null) {
            return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getInnNnpId());
        }
        return Objects.hash(getRoltype(), getBetrokkeneType(), null);
    }
}
