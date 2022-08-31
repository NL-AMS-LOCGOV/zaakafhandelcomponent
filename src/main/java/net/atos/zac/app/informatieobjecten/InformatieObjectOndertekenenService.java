/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

import com.fasterxml.uuid.impl.UUIDUtil;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.drc.model.Ondertekening;
import net.atos.client.zgw.drc.model.OndertekeningSoort;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;

import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@ApplicationScoped
@Transactional
public class InformatieObjectOndertekenenService {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private DRCClientService drcClientService;

    public void ondertekenen(final List<UUID> enkelvoudigInformatieObjectUUIDs) {
        enkelvoudigInformatieObjectUUIDs.forEach(this::ondertekenen);
    }

    public void ondertekenen(final UUID enkelvoudigInformatieObjectUUID) {
        boolean tempLock = false;
        try {
            final EnkelvoudigInformatieobjectWithInhoudAndLock enkelvoudigInformatieobjectWithInhoudAndLock =
                    new EnkelvoudigInformatieobjectWithInhoudAndLock();
            final Ondertekening ondertekening = new Ondertekening(OndertekeningSoort.DIGITAAL, LocalDate.now());
            enkelvoudigInformatieobjectWithInhoudAndLock.setOndertekening(ondertekening);
            enkelvoudigInformatieobjectWithInhoudAndLock.setStatus(InformatieobjectStatus.DEFINITIEF);

            EnkelvoudigInformatieObjectLock enkelvoudigInformatieObjectLock =
                    enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieObjectUUID);
            if (enkelvoudigInformatieObjectLock == null) {
                tempLock = true;
                enkelvoudigInformatieObjectLock =
                        enkelvoudigInformatieObjectLockService.createLock(enkelvoudigInformatieObjectUUID,
                                                                          loggedInUserInstance.get().getId());
            }

            enkelvoudigInformatieobjectWithInhoudAndLock.setLock(enkelvoudigInformatieObjectLock.getLock());
            drcClientService.updateEnkelvoudigInformatieobject(enkelvoudigInformatieObjectUUID,
                                                               "Door ondertekenen",
                                                               enkelvoudigInformatieobjectWithInhoudAndLock);
        } finally {
            if (tempLock) {
                enkelvoudigInformatieObjectLockService.deleteLock(enkelvoudigInformatieObjectUUID);
            }
        }
    }

}
