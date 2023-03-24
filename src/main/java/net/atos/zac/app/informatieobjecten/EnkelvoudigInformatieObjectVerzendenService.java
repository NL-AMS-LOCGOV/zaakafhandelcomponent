/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.enkelvoudiginformatieobject.EnkelvoudigInformatieObjectLockService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.shared.exception.FoutmeldingException;

@ApplicationScoped
@Transactional
public class EnkelvoudigInformatieObjectVerzendenService {

    private static final String PREFIX_VERZENDEN_TOELICHTING = "Per post";

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    private EnkelvoudigInformatieObjectLockService enkelvoudigInformatieObjectLockService;

    @Inject
    private DRCClientService drcClientService;

    public void verzendenEnkelvoudigInformatieObject(final EnkelvoudigInformatieobject informatieobject,
            final LocalDate verzenddatum, final String toelichting) {
        final var tempLock = !informatieobject.getLocked();
        try {
            final var informatieobjectUpdate = new EnkelvoudigInformatieobjectWithInhoudAndLock();
            informatieobjectUpdate.setLock(getLock(informatieobject));
            informatieobjectUpdate.setVerzenddatum(verzenddatum);
            drcClientService.updateEnkelvoudigInformatieobject(informatieobject.getUUID(),
                                                               isNotEmpty(toelichting) ? "%s: %s".formatted(
                                                                       PREFIX_VERZENDEN_TOELICHTING, toelichting) :
                                                                       PREFIX_VERZENDEN_TOELICHTING,
                                                               informatieobjectUpdate);
        } finally {
            if (tempLock) {
                enkelvoudigInformatieObjectLockService.deleteLock(informatieobject.getUUID());
            }
        }
    }

    private String getLock(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        if (enkelvoudigInformatieobject.getLocked()) {
            return enkelvoudigInformatieObjectLockService.findLock(enkelvoudigInformatieobject.getUUID())
                    .map(EnkelvoudigInformatieObjectLock::getLock)
                    .orElseThrow(() -> new FoutmeldingException(
                            "Document kan niet worden aangepast omdat het is gelocked met onbekende lock."));
        } else {
            return enkelvoudigInformatieObjectLockService.createLock(enkelvoudigInformatieobject.getUUID(),
                                                                     loggedInUserInstance.get().getId()).getLock();
        }
    }
}
