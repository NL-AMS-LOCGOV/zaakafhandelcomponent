/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.signalering.model.SignaleringSubject.INFORMATIEOBJECT;
import static net.atos.zac.signalering.model.SignaleringSubject.TAAK;
import static net.atos.zac.signalering.model.SignaleringSubject.ZAAK;
import static net.atos.zac.signalering.model.SignaleringTarget.GROUP;
import static net.atos.zac.signalering.model.SignaleringTarget.USER;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;

public class SignaleringVerzondenZoekParameters {
    private final SignaleringTarget targettype;

    private final String target;

    private Set<SignaleringType.Type> types;

    private SignaleringSubject subjecttype;

    private String subject;

    private SignaleringSubjectField subjectfield;

    public SignaleringVerzondenZoekParameters(final SignaleringTarget targettype, final String target) {
        this.targettype = targettype;
        this.target = target;
    }

    public SignaleringVerzondenZoekParameters(final Group target) {
        this(GROUP, target.getId());
    }

    public SignaleringVerzondenZoekParameters(final User target) {
        this(USER, target.getId());
    }

    public SignaleringTarget getTargettype() {
        return targettype;
    }

    public String getTarget() {
        return target;
    }

    public Set<SignaleringType.Type> getTypes() {
        return types == null ? Collections.emptySet() : Collections.unmodifiableSet(types);
    }

    public SignaleringVerzondenZoekParameters types(final SignaleringType.Type... types) {
        this.types = EnumSet.copyOf(Arrays.asList(types));
        return this;
    }

    public SignaleringVerzondenZoekParameters types(final SignaleringType.Type type) {
        this.types = EnumSet.of(type);
        return this;
    }

    public SignaleringSubject getSubjecttype() {
        return subjecttype;
    }

    public String getSubject() {
        return subject;
    }

    public SignaleringVerzondenZoekParameters subjecttype(final SignaleringSubject subjecttype) {
        this.subjecttype = subjecttype;
        return this;
    }

    public SignaleringVerzondenZoekParameters subject(final Zaak subject) {
        return subjectZaak(subject.getUuid());
    }

    public SignaleringVerzondenZoekParameters subject(final TaskInfo subject) {
        return subjectTaak(subject.getId());
    }

    public SignaleringVerzondenZoekParameters subject(final EnkelvoudigInformatieobject subject) {
        return subjectInformatieobject(URIUtil.parseUUIDFromResourceURI(subject.getUrl()));
    }

    public SignaleringVerzondenZoekParameters subjectZaak(final UUID zaakId) {
        this.subject = zaakId.toString();
        return subjecttype(ZAAK);
    }

    public SignaleringVerzondenZoekParameters subjectTaak(final String taakId) {
        this.subject = taakId;
        return subjecttype(TAAK);
    }

    public SignaleringVerzondenZoekParameters subjectInformatieobject(final UUID informatieobjectId) {
        this.subject = informatieobjectId.toString();
        return subjecttype(INFORMATIEOBJECT);
    }

    public SignaleringSubjectField getSubjectfield() {
        return subjectfield;
    }

    public SignaleringVerzondenZoekParameters subjectfield(final SignaleringSubjectField subjectfield) {
        this.subjectfield = subjectfield;
        return this;
    }
}
