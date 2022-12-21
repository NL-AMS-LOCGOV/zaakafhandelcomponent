/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.ztc.model.Roltype;

public class RolMedewerker extends Rol<Medewerker> {

    public RolMedewerker() {
    }

    public RolMedewerker(final URI zaak, final Roltype roltype, final String roltoelichting, final Medewerker betrokkeneIdentificatie) {
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
        if (getBetrokkeneIdentificatie() == null) {
            return null;
        }
        final Medewerker medewerker = getBetrokkeneIdentificatie();
        if (isNotBlank(medewerker.getAchternaam())) {
            final StringBuilder naam = new StringBuilder();
            if (isNotBlank(medewerker.getVoorletters())) {
                naam.append(medewerker.getVoorletters());
                naam.append(StringUtils.SPACE);
            }
            if (isNotBlank(medewerker.getVoorvoegselAchternaam())) {
                naam.append(medewerker.getVoorvoegselAchternaam());
                naam.append(StringUtils.SPACE);
            }
            naam.append(medewerker.getAchternaam());
            return naam.toString();
        } else {
            return medewerker.getIdentificatie();
        }
    }

    @Override
    public String getIdentificatienummer() {
        if (getBetrokkeneIdentificatie() == null) {
            return null;
        }
        return getBetrokkeneIdentificatie().getIdentificatie();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoltype(), getBetrokkeneType(), getBetrokkeneIdentificatie().getIdentificatie());
    }
}
