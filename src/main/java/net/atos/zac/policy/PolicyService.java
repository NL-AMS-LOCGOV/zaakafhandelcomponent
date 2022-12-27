/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static net.atos.client.zgw.drc.model.InformatieobjectStatus.DEFINITIEF;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.task.api.TaskInfo;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.opa.model.RuleResponse;
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.policy.exception.PolicyException;
import net.atos.zac.policy.input.DocumentData;
import net.atos.zac.policy.input.DocumentInput;
import net.atos.zac.policy.input.TaakData;
import net.atos.zac.policy.input.TaakInput;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakData;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.DocumentRechten;
import net.atos.zac.policy.output.OverigeRechten;
import net.atos.zac.policy.output.TaakRechten;
import net.atos.zac.policy.output.WerklijstRechten;
import net.atos.zac.policy.output.ZaakRechten;
import net.atos.zac.zoeken.model.DocumentIndicatie;
import net.atos.zac.zoeken.model.zoekobject.DocumentZoekObject;
import net.atos.zac.zoeken.model.zoekobject.TaakZoekObject;
import net.atos.zac.zoeken.model.zoekobject.ZaakZoekObject;

@ApplicationScoped
public class PolicyService {

    private static final String ALLE_ZAAKTYPEN = "-ALLE-ZAAKTYPEN-";

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @RestClient
    private OPAEvaluationClient evaluationClient;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private EnkelvoudigInformatieObjectLockService lockService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    public OverigeRechten readOverigeRechten() {
        return evaluationClient.readOverigeRechten(new RuleQuery<>(new UserInput(loggedInUserInstance.get())))
                .getResult();
    }

    public ZaakRechten readZaakRechten(final Zaak zaak) {
        return readZaakRechten(zaak, ztcClientService.readZaaktype(zaak.getZaaktype()));
    }

    public ZaakRechten readZaakRechten(final Zaak zaak, final Zaaktype zaaktype) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.zaaktype = zaaktype.getOmschrijving();
        return evaluationClient.readZaakRechten(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData)))
                .getResult();
    }

    public ZaakRechten readZaakRechten(final ZaakZoekObject zaakZoekObject) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = !zaakZoekObject.isAfgehandeld();
        zaakData.zaaktype = zaakZoekObject.getZaaktypeOmschrijving();
        return evaluationClient.readZaakRechten(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData)))
                .getResult();
    }

    public DocumentRechten readDocumentRechten(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return readDocumentRechten(enkelvoudigInformatieobject, null);
    }

    public DocumentRechten readDocumentRechten(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final Zaak zaak) {
        return readDocumentRechten(enkelvoudigInformatieobject,
                                   lockService.findLock(enkelvoudigInformatieobject.getUUID()).orElse(null), zaak);
    }

    public DocumentRechten readDocumentRechten(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final EnkelvoudigInformatieObjectLock lock,
            final Zaak zaak) {
        final DocumentData documentData = new DocumentData();
        documentData.definitief = enkelvoudigInformatieobject.getStatus() == DEFINITIEF;
        documentData.vergrendeld = enkelvoudigInformatieobject.getLocked();
        documentData.vergrendeldDoor = lock != null ? lock.getUserId() : null;
        if (zaak != null) {
            documentData.zaakOpen = zaak.isOpen();
            documentData.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        }
        return evaluationClient.readDocumentRechten(
                new RuleQuery<>(new DocumentInput(loggedInUserInstance.get(), documentData))).getResult();
    }

    public DocumentRechten readDocumentRechten(final DocumentZoekObject enkelvoudigInformatieobject) {
        final DocumentData documentData = new DocumentData();
        documentData.definitief = DEFINITIEF.equals(enkelvoudigInformatieobject.getStatus());
        documentData.vergrendeld = enkelvoudigInformatieobject.isIndicatie(DocumentIndicatie.VERGRENDELD);
        documentData.vergrendeldDoor = enkelvoudigInformatieobject.getVergrendeldDoorGebruikersnaam();
        documentData.zaakOpen = !enkelvoudigInformatieobject.isZaakAfgehandeld();
        documentData.zaaktype = enkelvoudigInformatieobject.getZaaktypeOmschrijving();
        return evaluationClient.readDocumentRechten(
                new RuleQuery<>(new DocumentInput(loggedInUserInstance.get(), documentData))).getResult();
    }

    public TaakRechten readTaakRechten(final TaskInfo taskInfo) {
        return readTaakRechten(taakVariabelenService.readZaaktypeOmschrijving(taskInfo));
    }

    public TaakRechten readTaakRechten(final String zaaktypeOmschrijving) {
        final TaakData taakData = new TaakData();
        taakData.zaaktype = zaaktypeOmschrijving;
        return evaluationClient.readTaakRechten(new RuleQuery<>(new TaakInput(loggedInUserInstance.get(), taakData)))
                .getResult();
    }

    public TaakRechten readTaakRechten(final TaakZoekObject taakZoekObject) {
        final TaakData taakData = new TaakData();
        taakData.zaaktype = taakZoekObject.getZaaktypeOmschrijving();
        return evaluationClient.readTaakRechten(new RuleQuery<>(new TaakInput(loggedInUserInstance.get(), taakData)))
                .getResult();
    }

    public WerklijstRechten readWerklijstRechten() {
        return evaluationClient.readWerklijstRechten(new RuleQuery<>(new UserInput(loggedInUserInstance.get())))
                .getResult();
    }

    /**
     * Get the set of allowed zaaktypen.
     * Returns null if all zaaktypen are allowed.
     *
     * @return Set of allowed zaaktypen which may be empty. Or null indicating that all zaaktypen are allowed.
     */
    public Set<String> getAllowedZaaktypen() {
        final Set<String> zaaktypen = readZaaktypen();
        return zaaktypen.contains(ALLE_ZAAKTYPEN) ? null : zaaktypen;
    }

    public List<Zaaktype> filterAllowedZaaktypen(final List<Zaaktype> alleZaaktypen) {
        final Set<String> zaaktypenAllowed = readZaaktypen();
        if (zaaktypenAllowed.contains(ALLE_ZAAKTYPEN)) {
            return alleZaaktypen;
        } else {
            return alleZaaktypen.stream().filter(zaaktype -> zaaktypenAllowed.contains(zaaktype.getOmschrijving()))
                    .toList();
        }
    }

    public boolean isZaaktypeAllowed(final String zaaktype) {
        final Set<String> zaaktypenAllowed = readZaaktypen();
        return zaaktypenAllowed.contains(ALLE_ZAAKTYPEN) || zaaktypenAllowed.contains(zaaktype);
    }

    public static void assertPolicy(final boolean policy) {
        if (!policy) {
            throw new PolicyException();
        }
    }

    private Set<String> readZaaktypen() {
        final RuleQuery<UserInput> query = new RuleQuery<>(new UserInput(loggedInUserInstance.get()));
        final RuleResponse<List<Set<String>>> response = evaluationClient.readZaaktypen(query);
        return response.getResult().get(0);
    }
}
