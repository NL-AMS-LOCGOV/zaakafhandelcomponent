/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

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
public class EnkelvoudigInformatieObjectOndertekenService {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private DRCClientService drcClientService;

    public void ondertekenEnkelvoudigInformatieObject(final UUID enkelvoudigInformatieObjectUUID) {
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
