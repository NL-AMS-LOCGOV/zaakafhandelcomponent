/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.zrc.model.Zaak;

public class DocumentCreatieGegevens {

    private final Zaak zaak;

    private String taskId;

    private InformatieobjectStatus informatieobjectStatus = InformatieobjectStatus.TER_VASTSTELLING;

    public DocumentCreatieGegevens(final Zaak zaak, final String taskId) {
        this.zaak = zaak;
        this.taskId = taskId;
    }

    public Zaak getZaak() {
        return zaak;
    }

    public InformatieobjectStatus getInformatieobjectStatus() {
        return informatieobjectStatus;
    }

    public String getTaskId() {
        return taskId;
    }
}
