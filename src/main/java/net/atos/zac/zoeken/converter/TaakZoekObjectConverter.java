package net.atos.zac.zoeken.converter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.flowable.FlowableService;
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
    private FlowableService flowableService;

    @Inject
    private ZTCClientService ztcClientService;


    public TaakZoekObjectConverter() {
    }

    @Override
    public TaakZoekObject convert(final UUID taskID) {
        final TaskInfo task = flowableService.readTask(taskID.toString());
        final TaakZoekObject zoekObject = new TaakZoekObject();

        zoekObject.setNaam(task.getName());
        zoekObject.setUuid(task.getId());
        zoekObject.setIdentificatie(task.getId());
        zoekObject.setType(ZoekObjectType.TAAK.toString());
        zoekObject.setCreatiedatum(task.getCreateTime());
        zoekObject.setToekenningsdatum(task.getClaimTime());
        zoekObject.setStreefdatum(task.getDueDate());
        zoekObject.setToelichting(task.getDescription());

        if (task.getAssignee() != null) {
            zoekObject.setStatus(TaakStatus.TOEGEKEND.toString());
            final User user = identityService.readUser(task.getAssignee());
            zoekObject.setBehandelaarNaam(user.getFullName());
            zoekObject.setBehandelaarGebruikersnaam(user.getId());
        } else {
            zoekObject.setStatus(TaakStatus.NIET_TOEGEKEND.toString());
        }

        final String groupID = extractGroupId(task.getIdentityLinks());
        if (groupID != null) {
            final Group group = identityService.readGroup(groupID);
            zoekObject.setGroepID(group.getId());
            zoekObject.setGroepNaam(group.getName());
        }

        final Zaaktype zaaktype = ztcClientService.readZaaktype(flowableService.readZaaktypeUUID(task.getScopeId()));
        zoekObject.setZaaktypeIdentificatie(zaaktype.getIdentificatie());
        zoekObject.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
        zoekObject.setZaaktypeUuid(UriUtil.uuidFromURI(zaaktype.getUrl()).toString());

        zoekObject.setZaakUUID(flowableService.readZaakUUID(task.getScopeId()).toString());
        zoekObject.setZaakID(flowableService.readZaakIdentificatieOpenCase(task.getScopeId()));

        final HashMap<String, String> taakdata = flowableService.findTaakdata(task.getId());
        if (MapUtils.isNotEmpty(taakdata)) {
            zoekObject.setTaakData(taakdata.entrySet().stream().map((es) -> "%s|%s".formatted(es.getKey(), es.getValue())).toList());
        }

        final HashMap<String, String> taakinformatie = flowableService.findTaakinformatie(task.getId());
        if (MapUtils.isNotEmpty(taakinformatie)) {
            zoekObject.setTaakInformatie(taakinformatie.entrySet().stream().map((es) -> "%s|%s".formatted(es.getKey(), es.getValue())).toList());
        }

        return zoekObject;
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
