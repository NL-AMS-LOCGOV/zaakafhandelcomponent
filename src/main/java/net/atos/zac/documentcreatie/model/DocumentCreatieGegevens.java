/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import java.util.UUID;

import net.atos.client.zgw.drc.model.InformatieobjectStatus;

public class DocumentCreatieGegevens {

    private final UUID zaakUUID;

    private final UUID informatieobjecttype;

    private String taskId;

    private String titel;

    private InformatieobjectStatus informatieobjectStatus = InformatieobjectStatus.TER_VASTSTELLING;

    public DocumentCreatieGegevens(final UUID zaakUUID, final UUID informatieobjecttype) {
        this.zaakUUID = zaakUUID;
        this.informatieobjecttype = informatieobjecttype;
    }

    public DocumentCreatieGegevens(final UUID zaakUUID, final UUID informatieobjecttype, final String taskId) {
        this.zaakUUID = zaakUUID;
        this.taskId = taskId;
        this.informatieobjecttype = informatieobjecttype;
    }

    public UUID getZaakUUID() {
        return zaakUUID;
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

    public void setInformatieobjectStatus(final InformatieobjectStatus informatieobjectStatus) {
        this.informatieobjectStatus = informatieobjectStatus;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }
}
