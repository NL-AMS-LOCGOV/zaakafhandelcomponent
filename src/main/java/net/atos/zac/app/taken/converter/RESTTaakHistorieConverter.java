/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static javax.json.bind.annotation.JsonbDateFormat.TIME_IN_MILLIS;
import static net.atos.zac.flowable.TaskService.USER_TASK_DESCRIPTION_CHANGED;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;
import static net.atos.zac.util.JsonbUtil.JSONB;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.json.bind.annotation.JsonbDateFormat;

import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.flowable.task.api.history.HistoricTaskLogEntryType;

import net.atos.zac.app.taken.model.RESTTaakHistorieRegel;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.identity.IdentityService;

public class RESTTaakHistorieConverter {

    private static final String CREATED_ATTRIBUUT_LABEL = "aangemaakt";

    private static final String COMPLETED_ATTRIBUUT_LABEL = "afgerond";

    private static final String GROEP_ATTRIBUUT_LABEL = "groep";

    private static final String BEHANDELAAR_ATTRIBUUT_LABEL = "behandelaar";

    private static final String TOELICHTING_ATTRIBUUT_LABEL = "toelichting";

    private static final String AANGEMAAKT_DOOR_ATTRIBUUT_LABEL = "aangemaaktDoor";

    private static final String STREEFDATUM_ATTRIBUUT_LABEL = "streefdatum";

    @Inject
    private IdentityService identityService;

    public List<RESTTaakHistorieRegel> convert(final List<HistoricTaskLogEntry> historicTaskLogEntries) {
        return historicTaskLogEntries.stream()
                .map(this::convert)
                .filter(Objects::nonNull)
                .toList();
    }

    private RESTTaakHistorieRegel convert(final HistoricTaskLogEntry historicTaskLogEntry) {
        final RESTTaakHistorieRegel restTaakHistorieRegel;

        if (historicTaskLogEntry.getType().equals(USER_TASK_DESCRIPTION_CHANGED)) {
            restTaakHistorieRegel = convertDescriptionChanged(historicTaskLogEntry.getData());
        } else {
            restTaakHistorieRegel = switch (HistoricTaskLogEntryType.valueOf(historicTaskLogEntry.getType())) {
                case USER_TASK_CREATED -> convertCreated();
                case USER_TASK_COMPLETED -> convertCompleted();
                case USER_TASK_IDENTITY_LINK_ADDED -> convertIdentityLinkAdded(historicTaskLogEntry.getData());
                case USER_TASK_IDENTITY_LINK_REMOVED -> convertIdentityLinkRemoved(historicTaskLogEntry.getData());
                case USER_TASK_ASSIGNEE_CHANGED -> convertAssigneeChanged(historicTaskLogEntry.getData());
                case USER_TASK_OWNER_CHANGED -> convertOwnerChanged(historicTaskLogEntry.getData());
                case USER_TASK_DUEDATE_CHANGED -> convertDuedateChanged(historicTaskLogEntry.getData());
                default -> null;
            };
        }

        if (restTaakHistorieRegel != null) {
            restTaakHistorieRegel.datumTijd = convertToZonedDateTime(historicTaskLogEntry.getTimeStamp());
        }

        return restTaakHistorieRegel;
    }

    private RESTTaakHistorieRegel convertCreated() {
        return new RESTTaakHistorieRegel(CREATED_ATTRIBUUT_LABEL);
    }

    private RESTTaakHistorieRegel convertCompleted() {
        return new RESTTaakHistorieRegel(COMPLETED_ATTRIBUUT_LABEL);
    }

    private RESTTaakHistorieRegel convertDescriptionChanged(final String data) {
        final TaskService.TaskDescriptionChangedData descriptionChangedData = JSONB.fromJson(data, TaskService.TaskDescriptionChangedData.class);
        return new RESTTaakHistorieRegel(TOELICHTING_ATTRIBUUT_LABEL,
                                         descriptionChangedData.previousDescription(),
                                         descriptionChangedData.newDescription());
    }

    public static class IdentityLinkData {
        public String groupId;
    }

    private String getGroupName(final String groepId) {
        return groepId == null ? null : identityService.readGroup(groepId).getName();
    }

    private RESTTaakHistorieRegel convertIdentityLinkAdded(final String data) {
        final IdentityLinkData identityLinkData = JSONB.fromJson(data, IdentityLinkData.class);
        return new RESTTaakHistorieRegel(GROEP_ATTRIBUUT_LABEL,
                                         null,
                                         getGroupName(identityLinkData.groupId));
    }

    private RESTTaakHistorieRegel convertIdentityLinkRemoved(final String data) {
        final IdentityLinkData identityLinkData = JSONB.fromJson(data, IdentityLinkData.class);
        return new RESTTaakHistorieRegel(GROEP_ATTRIBUUT_LABEL,
                                         getGroupName(identityLinkData.groupId),
                                         null);
    }

    public static class AssigneeChangedData {
        public String newAssigneeId;

        public String previousAssigneeId;
    }

    private String getMedewerkerFullName(final String medewerkerId) {
        return medewerkerId == null ? null : identityService.readUser(medewerkerId).getFullName();
    }

    private RESTTaakHistorieRegel convertAssigneeChanged(final String data) {
        final AssigneeChangedData assigneeChangedData = JSONB.fromJson(data, AssigneeChangedData.class);
        return new RESTTaakHistorieRegel(BEHANDELAAR_ATTRIBUUT_LABEL,
                                         getMedewerkerFullName(assigneeChangedData.previousAssigneeId),
                                         getMedewerkerFullName(assigneeChangedData.newAssigneeId));
    }

    private RESTTaakHistorieRegel convertOwnerChanged(final String data) {
        final AssigneeChangedData assigneeChangedData = JSONB.fromJson(data, AssigneeChangedData.class);
        return new RESTTaakHistorieRegel(AANGEMAAKT_DOOR_ATTRIBUUT_LABEL,
                                         getMedewerkerFullName(assigneeChangedData.previousAssigneeId),
                                         getMedewerkerFullName(assigneeChangedData.newAssigneeId));
    }

    public static class DuedateChangedData {
        @JsonbDateFormat(TIME_IN_MILLIS)
        public Date newDueDate;

        @JsonbDateFormat(TIME_IN_MILLIS)
        public Date previousDueDate;
    }

    private RESTTaakHistorieRegel convertDuedateChanged(final String data) {
        final DuedateChangedData duedateChangedData = JSONB.fromJson(data, DuedateChangedData.class);
        return new RESTTaakHistorieRegel(STREEFDATUM_ATTRIBUUT_LABEL,
                                         convertToZonedDateTime(duedateChangedData.previousDueDate),
                                         convertToZonedDateTime(duedateChangedData.newDueDate));
    }
}
