/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static net.atos.client.zgw.drc.model.InformatieobjectStatus.DEFINITIEF;
import static net.atos.zac.configuratie.ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.task.api.TaskInfo;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.opa.model.RuleResponse;
import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
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
import net.atos.zac.shared.exception.FoutmeldingException;

@ApplicationScoped
public class PolicyService {

    private static final String ALLE_ZAAKTYPEN = "-ALLE-ZAAKTYPEN-";

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @RestClient
    private OPAEvaluationClient evaluationClient;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private BRCClientService brcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private TaskService taskService;

    @Inject
    private EnkelvoudigInformatieObjectLockService lockService;

    @Inject
    private CaseVariablesService caseVariablesService;

    public OverigActies readOverigActies() {
        return evaluationClient.readOverigActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
    }

    public ZaakActies readZaakActies(final Zaak zaak) {
        final Statustype statustype = zaak.getStatus() != null ?
                ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype()) : null;
        return readZaakActies(zaak, ztcClientService.readZaaktype(zaak.getZaaktype()), statustype);
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Statustype statustype) {
        return readZaakActies(zaak, ztcClientService.readZaaktype(zaak.getZaaktype()), statustype);
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype, final Statustype statustype) {
        return readZaakActies(zaak, zaaktype, statustype, zgwApiService.findBehandelaarForZaak(zaak), brcClientService.findBesluit(zaak));
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype, final Statustype statustype, final RolMedewerker behandelaar) {
        return readZaakActies(zaak, zaaktype, statustype, behandelaar, brcClientService.findBesluit(zaak));
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype, final Statustype statustype, final RolMedewerker behandelaar,
            final Besluit besluit) {
        final ZaakData zaakData = new ZaakData();
        final Boolean ontvangstbevestingVerstuurd = caseVariablesService.findOntvangstbevestigingVerstuurd(zaak.getUuid());
        zaakData.open = zaak.isOpen();
        zaakData.opgeschort = zaak.isOpgeschort();
        zaakData.zaaktype = zaaktype.getOmschrijving();
        zaakData.heeftBesluittypen = CollectionUtils.isNotEmpty(zaaktype.getBesluittypen());
        zaakData.heropend = statustype != null && STATUSTYPE_OMSCHRIJVING_HEROPEND.equals(statustype.getOmschrijving());
        zaakData.besluit = besluit != null;
        zaakData.behandelaar = behandelaar != null ? behandelaar.getBetrokkeneIdentificatie().getIdentificatie() : null;
        zaakData.ontvangstbevestigingVerstuurd = ontvangstbevestingVerstuurd != null ? ontvangstbevestingVerstuurd : false;
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
        return readTaakActies(taskInfo, taskService.getTaakStatus(taskInfo), caseVariablesService.readZaaktypeOmschrijving(taskInfo.getScopeId()));
    }

    public TaakActies readTaakActies(final TaskInfo taskInfo, final TaakStatus taakStatus, final String zaaktypeOmschrijving) {
        final TaakData taakData = new TaakData();
        taakData.behandelaar = taskInfo.getAssignee();
        taakData.afgerond = taakStatus == TaakStatus.AFGEROND;
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

    public static void assertActie(final boolean actie) {
        if (!actie) {
            throw new PolicyException();
        }
    }

    public void valideerAlleDeelzakenGesloten(final Zaak zaak) {
        if (zaak.getDeelzaken().stream().map(zrcClientService::readZaak).anyMatch(Zaak::isOpen)) {
            throw new FoutmeldingException("Zaak kan niet worden afgesloten want er zijn nog openstaande deelzaken.");
        }
    }

    private Set<String> readZaaktypen() {
        final RuleQuery<UserInput> query = new RuleQuery<>(new UserInput(loggedInUserInstance.get()));
        final RuleResponse<List<Set<String>>> response = evaluationClient.readZaaktypen(query);
        return response.getResult().get(0);
    }
}
