/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class RolMedewerker extends Rol<Medewerker> {

    public RolMedewerker() {
    }

    public RolMedewerker(final URI zaak, final URI roltype, final String roltoelichting, final Medewerker betrokkeneIdentificatie) {
        super(zaak, roltype, BetrokkeneType.MEDEWERKER, betrokkeneIdentificatie, roltoelichting);
    }

    @Override
    protected boolean equalBetrokkeneIdentificatie(final Medewerker identificatie) {
        final Medewerker betrokkeneIdentificatie = getBetrokkeneIdentificatie();
        if (betrokkeneIdentificatie == identificatie) {
            return true;
        }
        if (identificatie == null) {
            return false;
        }
        return Objects.equals(betrokkeneIdentificatie.getIdentificatie(), identificatie.getIdentificatie());
    }

    public String getNaam() {
        final Medewerker medewerker = getBetrokkeneIdentificatie();
        if (medewerker == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(medewerker.getVoorletters());
        sb.append(StringUtils.SPACE);
        if (StringUtils.isNotEmpty(medewerker.getVoorvoegselAchternaam())) {
            sb.append(medewerker.getVoorvoegselAchternaam());
            sb.append(StringUtils.SPACE);
        }
        sb.append(medewerker.getAchternaam());
        return sb.toString();
    }

    @Override
    public String getIdentificatienummer() {
        return getBetrokkeneIdentificatie().getIdentificatie();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getIdentificatie());
    }
}
