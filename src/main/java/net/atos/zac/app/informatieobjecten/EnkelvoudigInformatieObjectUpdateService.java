package net.atos.zac.app.informatieobjecten;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import java.time.LocalDate;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.drc.model.Ondertekening;
import net.atos.client.zgw.drc.model.OndertekeningSoort;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;

@ApplicationScoped
@Transactional
public class EnkelvoudigInformatieObjectUpdateService {

    private static final String VERZEND_TOELICHTING_PREFIX = "Per post";

    private static final String ONDERTEKENEN_TOELICHTING = "Door ondertekenen";

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public void verzendEnkelvoudigInformatieObject(final UUID uuid, final LocalDate verzenddatum, final String toelichting) {
        final var update = new EnkelvoudigInformatieobjectWithInhoudAndLock();
        update.setVerzenddatum(verzenddatum);
        updateEnkelvoudigInformatieObject(uuid, update, isNotEmpty(toelichting) ? "%s: %s".formatted(
                VERZEND_TOELICHTING_PREFIX, toelichting) :
                VERZEND_TOELICHTING_PREFIX);
    }

    public void ondertekenEnkelvoudigInformatieObject(final UUID uuid) {
        final var update = new EnkelvoudigInformatieobjectWithInhoudAndLock();
        final Ondertekening ondertekening = new Ondertekening(OndertekeningSoort.DIGITAAL, LocalDate.now());
        update.setOndertekening(ondertekening);
        update.setStatus(InformatieobjectStatus.DEFINITIEF);
        updateEnkelvoudigInformatieObject(uuid, update, ONDERTEKENEN_TOELICHTING);
    }

    public EnkelvoudigInformatieobjectWithInhoudAndLock updateEnkelvoudigInformatieObject(final UUID uuid,
            final EnkelvoudigInformatieobjectWithInhoudAndLock update, final String toelichting) {
        EnkelvoudigInformatieObjectLock tempLock = null;
        try {
            final var existingLock = enkelvoudigInformatieObjectLockService.findLock(uuid);
            if (existingLock.isPresent()) {
                update.setLock(existingLock.get().getLock());
            } else {
                tempLock = enkelvoudigInformatieObjectLockService.createLock(uuid, loggedInUserInstance.get().getId());
                update.setLock(tempLock.getLock());
            }
            return drcClientService.updateEnkelvoudigInformatieobject(uuid, update, toelichting);
        } finally {
            if (tempLock != null) {
                enkelvoudigInformatieObjectLockService.deleteLock(uuid);
            }
        }
    }
}
