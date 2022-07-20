/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static net.atos.client.zgw.drc.model.InformatieobjectStatus.DEFINITIEF;
import static net.atos.zac.configuratie.ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.flowable.task.api.TaskInfo;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.opa.model.RuleResponse;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Rol;
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
import net.atos.zac.policy.input.EnkelvoudigInformatieobjectData;
import net.atos.zac.policy.input.EnkelvoudigInformatieobjectInput;
import net.atos.zac.policy.input.TaakData;
import net.atos.zac.policy.input.TaakInput;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakData;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.policy.output.EnkelvoudigInformatieobjectActies;
import net.atos.zac.policy.output.TaakActies;
import net.atos.zac.policy.output.TakenActies;
import net.atos.zac.policy.output.ZaakActies;
import net.atos.zac.policy.output.ZakenActies;
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
    private DRCClientService drcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private TaskService taskService;

    @Inject
    private EnkelvoudigInformatieObjectLockService lockService;

    @Inject
    private CaseVariablesService caseVariablesService;

    public AppActies readAppActies() {
        return evaluationClient.readAppActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype, final Statustype statustype, final RolMedewerker behandelaar,
            final Rol<?> initiator) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.opgeschort = zaak.isOpgeschort();
        zaakData.zaaktype = zaaktype.getOmschrijving();
        zaakData.heropend = statustype != null ? STATUSTYPE_OMSCHRIJVING_HEROPEND.equals(statustype.getOmschrijving()) : false;
        zaakData.behandelaar = behandelaar != null ? behandelaar.getBetrokkeneIdentificatie().getIdentificatie() : null;
        zaakData.initiator = initiator != null;
        return evaluationClient.readZaakActies(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData))).getResult();
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype, final Statustype statustype, final RolMedewerker behandelaar) {
        return readZaakActies(zaak, zaaktype, statustype, behandelaar, zgwApiService.findInitiatorForZaak(zaak));
    }

    public ZaakActies readZaakActies(final Zaak zaak, final Zaaktype zaaktype, final Statustype statustype) {
        return readZaakActies(zaak, zaaktype, statustype, zgwApiService.findBehandelaarForZaak(zaak));
    }

    public ZaakActies readZaakActies(final Zaak zaak) {
        final Statustype statustype = zaak.getStatus() != null ?
                ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype()) : null;
        return readZaakActies(zaak, ztcClientService.readZaaktype(zaak.getZaaktype()), statustype);
    }

    public ZaakActies readZaakActies(final UUID zaakUUID) {
        return readZaakActies(zrcClientService.readZaak(zaakUUID));
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final EnkelvoudigInformatieObjectLock lock, final Zaak zaak) {
        final EnkelvoudigInformatieobjectData enkelvoudigInformatieobjectData = new EnkelvoudigInformatieobjectData();
        enkelvoudigInformatieobjectData.definitief = enkelvoudigInformatieobject.getStatus() == DEFINITIEF;
        enkelvoudigInformatieobjectData.vergrendeld = enkelvoudigInformatieobject.getLocked();
        enkelvoudigInformatieobjectData.vergrendeldDoor = lock != null ? lock.getUserId() : null;
        if (zaak != null) {
            enkelvoudigInformatieobjectData.zaakOpen = zaak.isOpen();
            enkelvoudigInformatieobjectData.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        }
        return evaluationClient.readEnkelvoudigInformatieobjectActies(
                new RuleQuery<>(new EnkelvoudigInformatieobjectInput(loggedInUserInstance.get(), enkelvoudigInformatieobjectData))).getResult();
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final UUID enkelvoudigInformatieobjectUUID, final UUID zaakUUID) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
        final Zaak zaak = zaakUUID != null ? zrcClientService.readZaak(zaakUUID) : null;
        return readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieobject, zaak);
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final Zaak zaak) {
        return readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieobject, lockService.findLock(enkelvoudigInformatieobject.getUUID()), zaak);
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final UUID zaakUUID) {
        return readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieobject, lockService.findLock(enkelvoudigInformatieobject.getUUID()),
                                                     zrcClientService.readZaak(zaakUUID));
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return readEnkelvoudigInformatieobjectActies(enkelvoudigInformatieobject, lockService.findLock(enkelvoudigInformatieobject.getUUID()), null);
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final UUID enkelvoudigInformatieobjectUUID) {
        return readEnkelvoudigInformatieobjectActies(drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID));
    }

    public TaakActies readTaakActies(final TaskInfo taskInfo, final TaakStatus taakStatus, final String zaaktypeOmschrijving) {
        final TaakData taakData = new TaakData();
        taakData.behandelaar = taskInfo.getAssignee();
        taakData.afgerond = taakStatus == TaakStatus.AFGEROND;
        taakData.zaaktype = zaaktypeOmschrijving;
        return evaluationClient.readTaakActies(new RuleQuery<>(new TaakInput(loggedInUserInstance.get(), taakData))).getResult();
    }

    public TaakActies readTaakActies(final TaskInfo taskInfo) {
        return readTaakActies(taskInfo, taskService.getTaakStatus(taskInfo), caseVariablesService.readZaaktypeOmschrijving(taskInfo.getScopeId()));
    }

    public TaakActies readTaakActies(final String taskId) {
        return readTaakActies(taskService.readTask(taskId));
    }

    public ZakenActies readZakenActies() {
        return evaluationClient.readZakenActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
    }

    public TakenActies readTakenActies() {
        return evaluationClient.readTakenActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
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

    public void valideerZaakAfsluiten(final Zaak zaak) {
        assertActie(readZaakActies(zaak).getAfsluiten());
        if (zaak.getDeelzaken().stream().map(zrcClientService::readZaak).filter(Zaak::isOpen).findAny().isPresent()) {
            throw new FoutmeldingException("Zaak kan niet worden afgesloten want er zijn nog openstaande deelzaken.");
        }
    }

    private Set<String> readZaaktypen() {
        final RuleQuery<UserInput> query = new RuleQuery<>(new UserInput(loggedInUserInstance.get()));
        final RuleResponse<List<List<String>>> response = evaluationClient.readZaaktypen(query);
        return response.getResult().stream().flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }

}
