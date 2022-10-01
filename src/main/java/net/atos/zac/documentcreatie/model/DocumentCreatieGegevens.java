/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import java.util.UUID;

import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.client.zgw.zrc.model.Zaak;

public class DocumentCreatieGegevens {

    private final Zaak zaak;

    private final UUID informatieobjecttype;

    private String taskId;

    private String titel;

    private InformatieobjectStatus informatieobjectStatus = InformatieobjectStatus.TER_VASTSTELLING;

    public DocumentCreatieGegevens(final Zaak zaak, final UUID informatieobjecttype, final String taskId) {
        this.zaak = zaak;
        this.taskId = taskId;
        this.informatieobjecttype = informatieobjecttype;
    }

    public Zaak getZaak() {
        return zaak;
    }

    public UUID getInformatieobjecttype() {
        return informatieobjecttype;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public InformatieobjectStatus getInformatieobjectStatus() {
        return informatieobjectStatus;
    }

    public String getTaskId() {
        return taskId;
    }
}
