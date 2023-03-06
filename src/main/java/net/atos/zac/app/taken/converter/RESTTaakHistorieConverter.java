/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static javax.json.bind.annotation.JsonbDateFormat.TIME_IN_MILLIS;
import static net.atos.zac.flowable.TakenService.USER_TASK_ASSIGNEE_CHANGED_CUSTOM;
import static net.atos.zac.flowable.TakenService.USER_TASK_DESCRIPTION_CHANGED;
import static net.atos.zac.flowable.TakenService.USER_TASK_GROUP_CHANGED;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
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
import net.atos.zac.flowable.TakenService;
import net.atos.zac.identity.IdentityService;

public class RESTTaakHistorieConverter {

    private static final String CREATED_ATTRIBUUT_LABEL = "aangemaakt";

    private static final String COMPLETED_ATTRIBUUT_LABEL = "afgerond";

    private static final String GROEP_ATTRIBUUT_LABEL = "groep";

    private static final String BEHANDELAAR_ATTRIBUUT_LABEL = "behandelaar";

    private static final String TOELICHTING_ATTRIBUUT_LABEL = "toelichting";

    private static final String AANGEMAAKT_DOOR_ATTRIBUUT_LABEL = "aangemaaktDoor";

    private static final String FATALEDATUM_ATTRIBUUT_LABEL = "fataledatum";

    @Inject
    private IdentityService identityService;

    public List<RESTTaakHistorieRegel> convert(final List<HistoricTaskLogEntry> historicTaskLogEntries) {
        return historicTaskLogEntries.stream()
                .map(this::convert)
                .filter(Objects::nonNull)
                .toList();
    }

    private RESTTaakHistorieRegel convert(final HistoricTaskLogEntry historicTaskLogEntry) {
        final RESTTaakHistorieRegel restTaakHistorieRegel = switch (historicTaskLogEntry.getType()) {
            case USER_TASK_DESCRIPTION_CHANGED ->
                    convertValueChangeData(TOELICHTING_ATTRIBUUT_LABEL, historicTaskLogEntry.getData());
            case USER_TASK_ASSIGNEE_CHANGED_CUSTOM ->
                    convertValueChangeData(BEHANDELAAR_ATTRIBUUT_LABEL, historicTaskLogEntry.getData());
            case USER_TASK_GROUP_CHANGED ->
                    convertValueChangeData(GROEP_ATTRIBUUT_LABEL, historicTaskLogEntry.getData());
            default -> convertData(HistoricTaskLogEntryType.valueOf(historicTaskLogEntry.getType()),
                                   historicTaskLogEntry.getData());
        };
        if (restTaakHistorieRegel != null) {
            restTaakHistorieRegel.datumTijd = convertToZonedDateTime(historicTaskLogEntry.getTimeStamp());
        }
        return restTaakHistorieRegel;
    }

    private RESTTaakHistorieRegel convertData(final HistoricTaskLogEntryType type, final String data) {
        return switch (type) {
            case USER_TASK_CREATED -> new RESTTaakHistorieRegel(CREATED_ATTRIBUUT_LABEL);
            case USER_TASK_COMPLETED -> new RESTTaakHistorieRegel(COMPLETED_ATTRIBUUT_LABEL);
            case USER_TASK_OWNER_CHANGED -> convertOwnerChanged(data);
            case USER_TASK_DUEDATE_CHANGED -> convertDuedateChanged(data);
            default -> null;
        };
    }

    private RESTTaakHistorieRegel convertValueChangeData(final String attribuutLabel, final String data) {
        final TakenService.ValueChangeData valueChangeData = JSONB.fromJson(data, TakenService.ValueChangeData.class);
        return new RESTTaakHistorieRegel(attribuutLabel, valueChangeData.oldValue, valueChangeData.newValue,
                                         valueChangeData.explanation);
    }

    public static class AssigneeChangedData {
        public String newAssigneeId;

        public String previousAssigneeId;
    }

    private RESTTaakHistorieRegel convertOwnerChanged(final String data) {
        final AssigneeChangedData assigneeChangedData = JSONB.fromJson(data, AssigneeChangedData.class);
        return new RESTTaakHistorieRegel(AANGEMAAKT_DOOR_ATTRIBUUT_LABEL,
                                         getMedewerkerFullName(assigneeChangedData.previousAssigneeId),
                                         getMedewerkerFullName(assigneeChangedData.newAssigneeId), null);
    }

    private String getMedewerkerFullName(final String medewerkerId) {
        return medewerkerId == null ? null : identityService.readUser(medewerkerId).getFullName();
    }

    public static class DuedateChangedData {
        @JsonbDateFormat(TIME_IN_MILLIS)
        public Date newDueDate;

        @JsonbDateFormat(TIME_IN_MILLIS)
        public Date previousDueDate;
    }

    private RESTTaakHistorieRegel convertDuedateChanged(final String data) {
        final DuedateChangedData duedateChangedData = JSONB.fromJson(data, DuedateChangedData.class);
        return new RESTTaakHistorieRegel(FATALEDATUM_ATTRIBUUT_LABEL,
                                         convertToLocalDate(duedateChangedData.previousDueDate),
                                         convertToLocalDate(duedateChangedData.newDueDate), null);
    }
}
