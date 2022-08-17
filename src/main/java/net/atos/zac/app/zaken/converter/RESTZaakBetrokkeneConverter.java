/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.util.List;
import java.util.stream.Stream;

import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolNietNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolVestiging;
import net.atos.zac.app.zaken.model.RESTZaakBetrokkene;

public class RESTZaakBetrokkeneConverter {

    public RESTZaakBetrokkene convert(final Rol<?> rol) {
        final RESTZaakBetrokkene betrokkene = new RESTZaakBetrokkene();
        betrokkene.rolid = rol.getUuid().toString();
        betrokkene.roltype = rol.getOmschrijving();
        betrokkene.roltoelichting = rol.getRoltoelichting();
        betrokkene.type = rol.getBetrokkeneType().name();
        switch (rol.getBetrokkeneType()) {
            case NATUURLIJK_PERSOON -> {
                final RolNatuurlijkPersoon rolNatuurlijkPersoon = (RolNatuurlijkPersoon) rol;
                betrokkene.identificatie = rolNatuurlijkPersoon.getBetrokkeneIdentificatie().getInpBsn();
            }
            case NIET_NATUURLIJK_PERSOON -> {
                final RolNietNatuurlijkPersoon rolNietNatuurlijkPersoon = (RolNietNatuurlijkPersoon) rol;
                betrokkene.identificatie = rolNietNatuurlijkPersoon.getBetrokkeneIdentificatie().getInnNnpId();
            }
            case VESTIGING -> {
                final RolVestiging rolVestiging = (RolVestiging) rol;
                betrokkene.identificatie = rolVestiging.getBetrokkeneIdentificatie().getVestigingsNummer();
            }
            case ORGANISATORISCHE_EENHEID -> {
                final RolOrganisatorischeEenheid rolOrganisatorischeEenheid = (RolOrganisatorischeEenheid) rol;
                betrokkene.identificatie = rolOrganisatorischeEenheid.getBetrokkeneIdentificatie().getNaam();
            }
            case MEDEWERKER -> {
                final RolMedewerker rolMedewerker = (RolMedewerker) rol;
                betrokkene.identificatie = rolMedewerker.getBetrokkeneIdentificatie().getIdentificatie();
            }
        }
        return betrokkene;
    }

    public List<RESTZaakBetrokkene> convert(final Stream<Rol<?>> rollen) {
        return rollen
                .map(this::convert)
                .toList();
    }
}
