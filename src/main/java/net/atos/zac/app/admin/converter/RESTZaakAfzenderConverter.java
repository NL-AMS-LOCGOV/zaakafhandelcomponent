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
                    .map(ZaakAfzender::getMail)
                    .noneMatch(speciaal::is)) {
                restZaakAfzenders.add(new RESTZaakAfzender(speciaal));
            }
        }
        return restZaakAfzenders;
    }

    public List<ZaakAfzender> convertRESTZaakAfzenders(final List<RESTZaakAfzender> restZaakAfzender) {
        return restZaakAfzender.stream()
                .peek(afzender -> {
                    if (afzender.mail.equals(afzender.replyTo)) {
                        afzender.replyTo = null;
                    }
                })
                .filter(afzender -> !afzender.speciaal || afzender.defaultMail || afzender.replyTo != null)
                .map(this::convertRESTZaakAfzender)
                .toList();
    }

    public RESTZaakAfzender convertZaakAfzender(final ZaakAfzender zaakAfzender) {
        final RESTZaakAfzender restZaakAfzender = new RESTZaakAfzender();
        restZaakAfzender.id = zaakAfzender.getId();
        restZaakAfzender.defaultMail = zaakAfzender.isDefault();
        restZaakAfzender.mail = zaakAfzender.getMail();
        restZaakAfzender.replyTo = zaakAfzender.getReplyTo();
        restZaakAfzender.speciaal = Arrays.stream(ZaakAfzender.Speciaal.values())
                .anyMatch(speciaal -> speciaal.is(restZaakAfzender.mail));
        return restZaakAfzender;
    }

    public ZaakAfzender convertRESTZaakAfzender(final RESTZaakAfzender restZaakAfzender) {
        final ZaakAfzender zaakAfzender = new ZaakAfzender();
        zaakAfzender.setId(restZaakAfzender.id);
        zaakAfzender.setDefault(restZaakAfzender.defaultMail);
        zaakAfzender.setMail(restZaakAfzender.mail);
        zaakAfzender.setReplyTo(restZaakAfzender.replyTo);
        return zaakAfzender;
    }
}
