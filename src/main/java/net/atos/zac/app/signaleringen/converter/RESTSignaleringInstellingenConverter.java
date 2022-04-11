/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.signaleringen.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.idm.api.Group;

import net.atos.zac.app.signaleringen.model.RESTSignaleringInstellingen;
import net.atos.zac.authentication.Medewerker;
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
        restInstellingen.dashboard = instellingen.isDashboard();
        restInstellingen.mail = instellingen.isMail();
        return restInstellingen;
    }

    public List<RESTSignaleringInstellingen> convert(final Collection<SignaleringInstellingen> instellingen) {
        return instellingen.stream().map(this::convert).collect(Collectors.toList());
    }

    public SignaleringInstellingen convert(final RESTSignaleringInstellingen restInstellingen, final Group groep) {
        final SignaleringInstellingen instellingen = signaleringenService.readInstellingen(restInstellingen.type, groep);
        instellingen.setDashboard(restInstellingen.dashboard);
        instellingen.setMail(restInstellingen.mail);
        return instellingen;
    }

    public SignaleringInstellingen convert(final RESTSignaleringInstellingen restInstellingen, final Medewerker medewerker) {
        final SignaleringInstellingen instellingen = signaleringenService.readInstellingen(restInstellingen.type, medewerker);
        instellingen.setDashboard(restInstellingen.dashboard);
        instellingen.setMail(restInstellingen.mail);
        return instellingen;
    }
}
