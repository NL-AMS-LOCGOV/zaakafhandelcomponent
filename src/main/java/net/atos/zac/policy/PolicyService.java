/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static net.atos.client.zgw.drc.model.InformatieobjectStatus.DEFINITIEF;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.task.api.TaskInfo;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.policy.exception.PolicyException;
import net.atos.zac.policy.input.DocumentData;
import net.atos.zac.policy.input.DocumentInput;
import net.atos.zac.policy.input.NoInput;
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
import net.atos.zac.util.UriUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zoeken.model.DocumentIndicatie;
import net.atos.zac.zoeken.model.zoekobject.DocumentZoekObject;
import net.atos.zac.zoeken.model.zoekobject.TaakZoekObject;
import net.atos.zac.zoeken.model.zoekobject.ZaakZoekObject;

@ApplicationScoped
public class PolicyService {

    private static final String ALLE_DOMEINEN = "domein_elk_zaaktype";

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @RestClient
    private OPAEvaluationClient evaluationClient;

    @Inject
    private EnkelvoudigInformatieObjectLockService lockService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    public OverigeRechten readOverigeRechten() {
        return evaluationClient.readOverigeRechten(new RuleQuery<>(new UserInput(loggedInUserInstance.get())))
                .getResult();
    }

    private ZaakafhandelParameters readZaakafhandelParameters(final Zaak zaak) {
        return zaakafhandelParameterService.readZaakafhandelParameters(
                UriUtil.uuidFromURI(zaak.getZaaktype()));
    }

    public ZaakRechten readZaakRechten(final Zaak zaak) {
        return readZaakRechten(zaak, readZaakafhandelParameters(zaak));
    }

    public ZaakRechten readZaakRechten(final Zaak zaak, final ZaakafhandelParameters zaakafhandelParameters) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.domein = zaakafhandelParameters.getDomein();
        return evaluationClient.readZaakRechten(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData)))
                .getResult();
    }

    public ZaakRechten readZaakRechten(final ZaakZoekObject zaakZoekObject) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = !zaakZoekObject.isAfgehandeld();
        zaakData.domein = zaakZoekObject.getZaaktypeDomein();
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
            documentData.domein = readZaakafhandelParameters(zaak).getDomein();
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
        documentData.domein = enkelvoudigInformatieobject.getZaaktypeDomein();
        return evaluationClient.readDocumentRechten(
                new RuleQuery<>(new DocumentInput(loggedInUserInstance.get(), documentData))).getResult();
    }

    public TaakRechten readTaakRechten(final TaskInfo taskInfo) {
        return readTaakRechten(taakVariabelenService.readZaaktypeDomein(taskInfo));
    }

    public TaakRechten readTaakRechten(final String zaaktypeDomein) {
        final TaakData taakData = new TaakData();
        taakData.domein = zaaktypeDomein;
        return evaluationClient.readTaakRechten(new RuleQuery<>(new TaakInput(loggedInUserInstance.get(), taakData)))
                .getResult();
    }

    public TaakRechten readTaakRechten(final TaakZoekObject taakZoekObject) {
        final TaakData taakData = new TaakData();
        taakData.domein = taakZoekObject.getZaaktypeDomein();
        return evaluationClient.readTaakRechten(new RuleQuery<>(new TaakInput(loggedInUserInstance.get(), taakData)))
                .getResult();
    }

    public WerklijstRechten readWerklijstRechten() {
        return evaluationClient.readWerklijstRechten(new RuleQuery<>(new UserInput(loggedInUserInstance.get())))
                .getResult();
    }

    /**
     * Get the set of allowed domeinen.
     * Returns null if all domeinen are allowed.
     *
     * @return Set of allowed domeinen which may be empty. Or null indicating that all domeinen are allowed.
     */
    public Set<String> getAllowedDomeinenForUser() {
        final Set<String> userDomeinen = listDomeinenForUser();
        return userDomeinen.contains(ALLE_DOMEINEN) ? null : userDomeinen;
    }

    public boolean isDomeinAllowedForUser(final String domein) {
        final Set<String> userDomeinen = listDomeinenForUser();
        return userDomeinen.contains(ALLE_DOMEINEN) || userDomeinen.contains(domein);
    }

    private Set<String> listDomeinenForUser() {
        final Set<String> userDomeinen = new HashSet<>(loggedInUserInstance.get().getRoles());
        evaluationClient.readRollen(NoInput.QUERY).getResult()
                .forEach(userDomeinen::remove);
        return Collections.unmodifiableSet(userDomeinen);
    }

    public static void assertPolicy(final boolean policy) {
        if (!policy) {
            throw new PolicyException();
        }
    }
}
