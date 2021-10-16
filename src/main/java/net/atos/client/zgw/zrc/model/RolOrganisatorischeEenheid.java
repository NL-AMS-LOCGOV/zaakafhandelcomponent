/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.Objects;

public class RolOrganisatorischeEenheid extends Rol<OrganisatorischeEenheid> {

    public RolOrganisatorischeEenheid() {
    }

    public RolOrganisatorischeEenheid(final URI zaak, final URI roltype, final String roltoelichting, final OrganisatorischeEenheid betrokkeneIdentificatie) {
        super(zaak, roltype, BetrokkeneType.ORGANISATORISCHE_EENHEID, betrokkeneIdentificatie, roltoelichting);
    }

    @Override
    protected boolean equalBetrokkeneIdentificatie(final OrganisatorischeEenheid identificatie) {
        final OrganisatorischeEenheid betrokkeneIdentificatie = getBetrokkeneIdentificatie();
        if (betrokkeneIdentificatie == identificatie) {
            return true;
        }
        if (identificatie == null) {
            return false;
        }
        return Objects.equals(betrokkeneIdentificatie.getIdentificatie(), identificatie.getIdentificatie());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getIdentificatie());
    }
}
