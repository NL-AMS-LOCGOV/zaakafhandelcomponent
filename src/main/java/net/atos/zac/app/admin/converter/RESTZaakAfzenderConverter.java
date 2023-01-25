/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.atos.zac.app.admin.model.RESTZaakAfzender;
import net.atos.zac.zaaksturing.model.ZaakAfzender;

public class RESTZaakAfzenderConverter {

    public List<RESTZaakAfzender> convertZaakAfzenders(final Set<ZaakAfzender> zaakAfzender) {
        final List<RESTZaakAfzender> restZaakAfzenders = zaakAfzender.stream()
                .map(this::convertZaakAfzender)
                .collect(Collectors.toList());
        for (final ZaakAfzender.Speciaal speciaal : ZaakAfzender.Speciaal.values()) {
            if (zaakAfzender.stream()
                    .noneMatch(afzender -> afzender.is(speciaal))) {
                restZaakAfzenders.add(new RESTZaakAfzender(speciaal));
            }
        }
        return restZaakAfzenders;
    }

    public List<ZaakAfzender> convertRESTZaakAfzenders(final List<RESTZaakAfzender> restZaakAfzender) {
        return restZaakAfzender.stream()
                .filter(afzender -> !afzender.speciaal || afzender.defaultMail)
                .map(this::convertRESTZaakAfzender)
                .toList();
    }

    private RESTZaakAfzender convertZaakAfzender(final ZaakAfzender zaakAfzender) {
        final RESTZaakAfzender restZaakAfzender = new RESTZaakAfzender();
        restZaakAfzender.id = zaakAfzender.getId();
        restZaakAfzender.mail = zaakAfzender.getMail();
        restZaakAfzender.defaultMail = zaakAfzender.isDefault();
        restZaakAfzender.speciaal = Arrays.stream(ZaakAfzender.Speciaal.values()).anyMatch(zaakAfzender::is);
        return restZaakAfzender;
    }

    private ZaakAfzender convertRESTZaakAfzender(final RESTZaakAfzender restZaakAfzender) {
        final ZaakAfzender zaakAfzender = new ZaakAfzender();
        zaakAfzender.setId(restZaakAfzender.id);
        zaakAfzender.setMail(restZaakAfzender.mail);
        zaakAfzender.setDefault(restZaakAfzender.defaultMail);
        return zaakAfzender;
    }
}
