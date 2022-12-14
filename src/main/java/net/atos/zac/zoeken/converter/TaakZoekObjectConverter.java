package net.atos.zac.zoeken.converter;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.flowable.ZaakVariabelenService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.zoeken.model.index.ZoekObjectType;
import net.atos.zac.zoeken.model.zoekobject.TaakZoekObject;

public class TaakZoekObjectConverter extends AbstractZoekObjectConverter<TaakZoekObject> {

    @Inject
    private IdentityService identityService;

    @Inject
    private TakenService takenService;

    @Inject
    private ZaakVariabelenService zaakVariabelenService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Override
    public TaakZoekObject convert(final String taskID) {
        final TaskInfo taskInfo = takenService.readTask(taskID);
        final TaakZoekObject taakZoekObject = new TaakZoekObject();

        taakZoekObject.setNaam(taskInfo.getName());
        taakZoekObject.setId(taskInfo.getId());
        taakZoekObject.setType(ZoekObjectType.TAAK);
        taakZoekObject.setCreatiedatum(taskInfo.getCreateTime());
        taakZoekObject.setToekenningsdatum(taskInfo.getClaimTime());
        taakZoekObject.setFataledatum(taskInfo.getDueDate());
        taakZoekObject.setToelichting(taskInfo.getDescription());

        if (taskInfo.getAssignee() != null) {
            final User user = identityService.readUser(taskInfo.getAssignee());
            taakZoekObject.setBehandelaarNaam(user.getFullName());
            taakZoekObject.setBehandelaarGebruikersnaam(user.getId());
            taakZoekObject.setToegekend(true);
        }

        taakZoekObject.setStatus(takenService.getTaakStatus(taskInfo));
        final String groupID = extractGroupId(taskInfo.getIdentityLinks());
        if (groupID != null) {
            final Group group = identityService.readGroup(groupID);
            taakZoekObject.setGroepID(group.getId());
            taakZoekObject.setGroepNaam(group.getName());
        }

        final Zaaktype zaaktype = ztcClientService.readZaaktype(
                taakVariabelenService.readZaaktypeUUID(taskInfo));
        taakZoekObject.setZaaktypeIdentificatie(zaaktype.getIdentificatie());
        taakZoekObject.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
        taakZoekObject.setZaaktypeUuid(zaaktype.getUUID().toString());

        final UUID zaakUUID = taakVariabelenService.readZaakUUID(taskInfo);
        taakZoekObject.setZaakUUID(zaakUUID.toString());
        taakZoekObject.setZaakIdentificatie(taakVariabelenService.readZaakIdentificatie(taskInfo));

        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        taakZoekObject.setZaakOmschrijving(zaak.getOmschrijving());
        taakZoekObject.setZaakToelichting(zaak.getToelichting());

        taakZoekObject.setTaakData(
                taakVariabelenService.readTaakdata(taskInfo).entrySet().stream()
                        .map(data -> "%s|%s".formatted(data.getKey(), data.getValue()))
                        .toList());

        taakZoekObject.setTaakInformatie(
                taakVariabelenService.readTaakinformatie(taskInfo).entrySet().stream()
                        .map(informatie -> "%s|%s".formatted(informatie.getKey(), informatie.getValue()))
                        .toList());

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
