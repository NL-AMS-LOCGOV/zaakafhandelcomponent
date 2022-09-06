package net.atos.zac.zoeken.converter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.flowable.TaskVariablesService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zoeken.model.TaakZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class TaakZoekObjectConverter extends AbstractZoekObjectConverter<TaakZoekObject> {

    @Inject
    private IdentityService identityService;

    @Inject
    private TaskService taskService;

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private TaskVariablesService taskVariablesService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Override
    public TaakZoekObject convert(final String taskID) {
        final TaskInfo taskInfo = taskService.readTask(taskID);
        final TaakZoekObject taakZoekObject = new TaakZoekObject();

        taakZoekObject.setNaam(taskInfo.getName());
        taakZoekObject.setId(taskInfo.getId());
        taakZoekObject.setType(ZoekObjectType.TAAK);
        taakZoekObject.setCreatiedatum(taskInfo.getCreateTime());
        taakZoekObject.setToekenningsdatum(taskInfo.getClaimTime());
        taakZoekObject.setStreefdatum(taskInfo.getDueDate());
        taakZoekObject.setToelichting(taskInfo.getDescription());

        if (taskInfo.getAssignee() != null) {
            final User user = identityService.readUser(taskInfo.getAssignee());
            taakZoekObject.setBehandelaarNaam(user.getFullName());
            taakZoekObject.setBehandelaarGebruikersnaam(user.getId());
        }
        taakZoekObject.setStatus(taskService.getTaakStatus(taskInfo));
        final String groupID = extractGroupId(taskInfo.getIdentityLinks());
        if (groupID != null) {
            final Group group = identityService.readGroup(groupID);
            taakZoekObject.setGroepID(group.getId());
            taakZoekObject.setGroepNaam(group.getName());
        }

        final Zaaktype zaaktype = ztcClientService.readZaaktype(caseVariablesService.readZaaktypeUUID(taskInfo.getScopeId()));
        taakZoekObject.setZaaktypeIdentificatie(zaaktype.getIdentificatie());
        taakZoekObject.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
        taakZoekObject.setZaaktypeUuid(UriUtil.uuidFromURI(zaaktype.getUrl()).toString());

        taakZoekObject.setZaakUUID(caseVariablesService.readZaakUUID(taskInfo.getScopeId()).toString());
        taakZoekObject.setZaakIdentificatie(caseVariablesService.readZaakIdentificatie(taskInfo.getScopeId()));

        final Zaak zaak = zrcClientService.readZaak(UUID.fromString(taakZoekObject.getZaakUUID()));
        taakZoekObject.setZaakOmschrijving(zaak.getOmschrijving());
        taakZoekObject.setToelichting(zaak.getToelichting());

        final HashMap<String, String> taakdata = taskVariablesService.findTaakdata(taskInfo.getId());
        if (MapUtils.isNotEmpty(taakdata)) {
            taakZoekObject.setTaakData(taakdata.entrySet().stream().map((es) -> "%s|%s".formatted(es.getKey(), es.getValue())).toList());
        }

        final HashMap<String, String> taakinformatie = taskVariablesService.findTaakinformatie(taskInfo.getId());
        if (MapUtils.isNotEmpty(taakinformatie)) {
            taakZoekObject.setTaakInformatie(taakinformatie.entrySet().stream().map((es) -> "%s|%s".formatted(es.getKey(), es.getValue())).toList());
        }

        return taakZoekObject;
    }

    @Override
    public boolean supports(final ZoekObjectType objectType) {
        return objectType == ZoekObjectType.TAAK;
    }

    private String extractGroupId(final List<? extends IdentityLinkInfo> identityLinks) {
        return identityLinks.stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .findAny()
                .map(IdentityLinkInfo::getGroupId)
                .orElse(null);
    }
}
