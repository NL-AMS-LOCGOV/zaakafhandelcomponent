/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.signaleringen.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.zac.app.signaleringen.model.RESTSignaleringInstellingen;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringInstellingen;

public class RESTSignaleringInstellingenConverter {

    @Inject
    private SignaleringenService signaleringenService;

    public RESTSignaleringInstellingen convert(final SignaleringInstellingen instellingen) {
        final RESTSignaleringInstellingen restInstellingen = new RESTSignaleringInstellingen();
        restInstellingen.id = instellingen.getId();
        restInstellingen.type = instellingen.getType().getType();
        restInstellingen.subjecttype = instellingen.getType().getSubjecttype();
        if (instellingen.getType().getType().isDashboard()) {
            restInstellingen.dashboard = instellingen.isDashboard();
        }
        if (instellingen.getType().getType().isMail()) {
            restInstellingen.mail = instellingen.isMail();
        }
        return restInstellingen;
    }

    public List<RESTSignaleringInstellingen> convert(final Collection<SignaleringInstellingen> instellingen) {
        return instellingen.stream().map(this::convert).collect(Collectors.toList());
    }

    public SignaleringInstellingen convert(final RESTSignaleringInstellingen restInstellingen, final Group group) {
        final SignaleringInstellingen instellingen = signaleringenService.readInstellingen(restInstellingen.type, group);
        instellingen.setDashboard(restInstellingen.dashboard);
        instellingen.setMail(restInstellingen.mail);
        return instellingen;
    }

    public SignaleringInstellingen convert(final RESTSignaleringInstellingen restInstellingen, final User user) {
        final SignaleringInstellingen instellingen = signaleringenService.readInstellingen(restInstellingen.type, user);
        instellingen.setDashboard(instellingen.getType().getType().isDashboard() && restInstellingen.dashboard);
        instellingen.setMail(instellingen.getType().getType().isMail() && restInstellingen.mail);
        return instellingen;
    }
}
