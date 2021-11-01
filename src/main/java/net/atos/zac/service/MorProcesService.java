/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.service;

import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.cdi.annotation.BusinessKey;

import net.atos.client.or.object.model.ORObject;
import net.atos.client.or.object.util.ObjectsInvocationBuilderFactory;
import net.atos.client.zgw.zrc.ZRCClient;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;
import net.atos.zac.flowable.bpmn.ProcessContext;

/**
 *
 */
@Named("mor")
public class MorProcesService {

    private static final Logger LOG = Logger.getLogger(MorProcesService.class.getName());

    private static final String CATEGORIE_ATTRIBUUT_NAAM = "categorie";

    private static final String CATEGORIE_TE_ANALYSEREN_WAARDE = "Hoofd 1";

    @Inject
    @BusinessKey
    private String zaakUUID;

    @Inject
    private ProcessContext context;

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    @Produces
    @Named
    public Boolean analyserenMelding() {
        // final ProcessContext processContext = (ProcessContext) object;
        LOG.info(String.format(">>> Trace: %s", context.getTrace()));
        context.setTrace(context.getTrace() + " analyserenMelding()");
        final Zaak zaak = zrcClient.zaakRead(UUID.fromString(zaakUUID));
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        zaakobjectListParameters.setZaak(zaak.getUrl());
        final Zaakobject zaakobject = zrcClient.zaakobjectList(zaakobjectListParameters).getSingleResult().get();
        final ORObject object = ObjectsInvocationBuilderFactory.create(zaakobject.getObject()).get(ORObject.class);
        final String categorie = (String) object.getRecord().getDataAsHashMap().get(CATEGORIE_ATTRIBUUT_NAAM);
        final Boolean result = StringUtils.equalsIgnoreCase(categorie, CATEGORIE_TE_ANALYSEREN_WAARDE);
        LOG.info(String.format("Categorie '%s' dus analyseren melding: %s", categorie, result));
        return result;
    }
}
