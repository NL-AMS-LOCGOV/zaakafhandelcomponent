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
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.policy.exception.PolicyException;
import net.atos.zac.policy.input.DocumentData;
import net.atos.zac.policy.input.DocumentInput;
import net.atos.zac.policy.input.TaakData;
import net.atos.zac.policy.input.TaakInput;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakData;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.DocumentActies;
import net.atos.zac.policy.output.OverigActies;
import net.atos.zac.policy.output.TaakActies;
import net.atos.zac.policy.output.WerklijstActies;
import net.atos.zac.policy.output.ZaakActies;

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
    private CaseVariablesService caseVariablesService;

    public OverigActies readOverigActies() {
        return evaluationClient.readOverigActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
    }

    public ZaakActies readZaakActies(final Zaak zaak) {
        return readZaakActies(zaak, ztcClientService.readZaaktype(zaak.getZaaktype()));
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.zaaktype = zaaktype.getOmschrijving();
        return evaluationClient.readZaakActies(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData))).getResult();
    }

    public DocumentActies readDocumentActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return readDocumentActies(enkelvoudigInformatieobject, null);
    }

    public DocumentActies readDocumentActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject, final Zaak zaak) {
        return readDocumentActies(enkelvoudigInformatieobject, lockService.findLock(enkelvoudigInformatieobject.getUUID()), zaak);
    }

    public DocumentActies readDocumentActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject, final EnkelvoudigInformatieObjectLock lock,
            final Zaak zaak) {
        final DocumentData documentData = new DocumentData();
        documentData.definitief = enkelvoudigInformatieobject.getStatus() == DEFINITIEF;
        documentData.vergrendeld = enkelvoudigInformatieobject.getLocked();
        documentData.vergrendeldDoor = lock != null ? lock.getUserId() : null;
        if (zaak != null) {
            documentData.zaakOpen = zaak.isOpen();
            documentData.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        }
        documentData.ondertekend = enkelvoudigInformatieobject.getOndertekening().getDatum() != null;
        return evaluationClient.readDocumentActies(
                new RuleQuery<>(new DocumentInput(loggedInUserInstance.get(), documentData))).getResult();
    }

    public TaakActies readTaakActies(final TaskInfo taskInfo) {
        return readTaakActies(taskInfo, caseVariablesService.readZaaktypeOmschrijving(taskInfo.getScopeId()));
    }

    public TaakActies readTaakActies(final TaskInfo taskInfo, final String zaaktypeOmschrijving) {
        final TaakData taakData = new TaakData();
        taakData.behandelaar = taskInfo.getAssignee();
        taakData.zaaktype = zaaktypeOmschrijving;
        return evaluationClient.readTaakActies(new RuleQuery<>(new TaakInput(loggedInUserInstance.get(), taakData))).getResult();
    }

    public WerklijstActies readWerklijstActies() {
        return evaluationClient.readWerklijstActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
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
            return alleZaaktypen.stream().filter(zaaktype -> zaaktypenAllowed.contains(zaaktype.getOmschrijving())).toList();
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
