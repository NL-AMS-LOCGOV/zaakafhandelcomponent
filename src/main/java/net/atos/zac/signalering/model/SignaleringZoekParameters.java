/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.signalering.model.SignaleringSubject.INFORMATIEOBJECT;
import static net.atos.zac.signalering.model.SignaleringSubject.TAAK;
import static net.atos.zac.signalering.model.SignaleringSubject.ZAAK;
import static net.atos.zac.signalering.model.SignaleringTarget.GROEP;
import static net.atos.zac.signalering.model.SignaleringTarget.MEDEWERKER;

import org.flowable.idm.api.Group;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.authentication.Medewerker;

public class SignaleringZoekParameters {
    private SignaleringType.Type type;

    private SignaleringTarget targettype;

    private String target;

    private SignaleringSubject subjecttype;

    private String subject;

    public SignaleringType.Type getType() {
        return type;
    }

    public SignaleringZoekParameters type(final SignaleringType.Type type) {
        this.type = type;
        return this;
    }

    public SignaleringTarget getTargettype() {
        return targettype;
    }

    public String getTarget() {
        return target;
    }

    public SignaleringZoekParameters target(final Group target) {
        this.targettype = GROEP;
        this.target = target.getId();
        return this;
    }

    public SignaleringZoekParameters target(final Medewerker target) {
        this.targettype = MEDEWERKER;
        this.target = target.getGebruikersnaam();
        return this;
    }

    public SignaleringSubject getSubjecttype() {
        return subjecttype;
    }

    public String getSubject() {
        return subject;
    }

    public SignaleringZoekParameters subject(final Zaak subject) {
        this.subjecttype = ZAAK;
        this.subject = subject.getUuid().toString();
        return this;
    }

    public SignaleringZoekParameters subject(final TaskInfo subject) {
        this.subjecttype = TAAK;
        this.subject = subject.getId();
        return this;
    }

    public SignaleringZoekParameters subject(final EnkelvoudigInformatieobject subject) {
        this.subjecttype = INFORMATIEOBJECT;
        this.subject = URIUtil.parseUUIDFromResourceURI(subject.getUrl()).toString();
        return this;
    }
}
