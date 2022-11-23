package net.atos.zac.flowable;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

@ApplicationScoped
@Transactional
public class CaseVariablesService {

    public static final String VAR_ZAAK_UUID = "zaakUUID";

    public static final String VAR_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_ZAAKTYPE_UUUID = "zaaktypeUUID";

    public static final String VAR_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    public static final String VAR_ZAAK_DATA = "zaakData";

    private static final String VAR_ONTVANGSTBEVESTIGING_VERSTUURD = "ontvangstbevestigingVerstuurd";

    private static final String VAR_DATUMTIJD_OPGESCHORT = "datumTijdOpgeschort";

    private static final String VAR_VERWACHTE_DAGEN_OPGESCHORT = "verwachteDagenOpgeschort";

    private static final String VAR_ONTVANKELIJK = "ontvankelijk";

    @Inject
    private CmmnRuntimeService cmmnRuntimeService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    public UUID readZaakUUID(final String caseInstanceId) {
        return (UUID) readCaseVariable(caseInstanceId, VAR_ZAAK_UUID);
    }

    public String readZaakIdentificatie(final String caseInstanceId) {
        return (String) readCaseVariable(caseInstanceId, VAR_ZAAK_IDENTIFICATIE);
    }

    public UUID readZaaktypeUUID(final String caseInstanceId) {
        return (UUID) readCaseVariable(caseInstanceId, VAR_ZAAKTYPE_UUUID);
    }

    public String readZaaktypeOmschrijving(final String caseInstanceId) {
        return (String) readCaseVariable(caseInstanceId, VAR_ZAAKTYPE_OMSCHRIJVING);
    }

    public Boolean findOntvangstbevestigingVerstuurd(final UUID zaakUUID) {
        return (Boolean) findCaseVariable(zaakUUID, VAR_ONTVANGSTBEVESTIGING_VERSTUURD);
    }

    public void setOntvangstbevestigingVerstuurd(final UUID zaakUUID, final Boolean ontvangstbevestigingVerstuurd) {
        setVariable(zaakUUID, VAR_ONTVANGSTBEVESTIGING_VERSTUURD, ontvangstbevestigingVerstuurd);
    }

    public void setOntvankelijk(final String caseInstanceId, final Boolean ontvankelijk) {
        setVariable(caseInstanceId, VAR_ONTVANKELIJK, ontvankelijk);
    }

    public ZonedDateTime findDatumtijdOpgeschort(final UUID zaakUUID) {
        return (ZonedDateTime) findCaseVariable(zaakUUID, VAR_DATUMTIJD_OPGESCHORT);
    }

    public void setDatumtijdOpgeschort(final UUID zaakUUID, final ZonedDateTime datumtijOpgeschort) {
        setVariable(zaakUUID, VAR_DATUMTIJD_OPGESCHORT, datumtijOpgeschort);
    }

    public void removeDatumtijdOpgeschort(final UUID zaakUUID) {
        removeVariable(zaakUUID, VAR_DATUMTIJD_OPGESCHORT);
    }

    public Integer findVerwachteDagenOpgeschort(final UUID zaakUUID) {
        return (Integer) findCaseVariable(zaakUUID, VAR_VERWACHTE_DAGEN_OPGESCHORT);
    }

    public void setVerwachteDagenOpgeschort(final UUID zaakUUID, final Integer verwachteDagenOpgeschort) {
        setVariable(zaakUUID, VAR_VERWACHTE_DAGEN_OPGESCHORT, verwachteDagenOpgeschort);
    }

    public void removeVerwachteDagenOpgeschort(final UUID zaakUUID) {
        removeVariable(zaakUUID, VAR_VERWACHTE_DAGEN_OPGESCHORT);
    }

    public Map<String, Object> readZaakdata(final UUID zaakUUID) {
        final Map<String, Object> zaakdata = (Map<String, Object>) findCaseVariable(zaakUUID, VAR_ZAAK_DATA);
        return zaakdata != null ? zaakdata : Collections.emptyMap();
    }

    public void setZaakdata(final UUID zaakUUID, final Map<String, Object> zaakdata) {
        setVariable(zaakUUID, VAR_ZAAK_DATA, zaakdata);
    }

    private Object readCaseVariable(final String caseInstanceId, final String variableName) {
        if (cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstanceId).singleResult() != null) {
            return readOpenCaseVariable(caseInstanceId, variableName);
        } else {
            return readClosedCaseVariable(caseInstanceId, variableName);
        }
    }

    private Object readOpenCaseVariable(final String caseInstanceId, final String variableName) {
        final Object value = cmmnRuntimeService.getVariable(caseInstanceId, variableName);
        if (value != null) {
            return value;
        } else {
            throw new RuntimeException(
                    String.format("No variable found with name '%s' for open case instance id '%s'", variableName,
                                  caseInstanceId));
        }
    }

    private Object readClosedCaseVariable(final String caseInstanceId, final String variableName) {
        final HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .variableName(variableName)
                .singleResult();
        final Object value = historicVariableInstance != null ? historicVariableInstance.getValue() : null;
        if (value != null) {
            return value;
        } else {
            throw new RuntimeException(
                    String.format("No variable found with name '%s' for closed case instance id '%s'", variableName,
                                  caseInstanceId));
        }
    }

    private Object findCaseVariable(final UUID zaakUUID, final String variableName) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .includeCaseVariables()
                .singleResult();

        if (caseInstance != null) {
            return caseInstance.getCaseVariables().get(variableName);
        } else {
            final HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery()
                    .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                    .includeCaseVariables()
                    .singleResult();

            return historicCaseInstance != null ? historicCaseInstance.getCaseVariables().get(variableName) : null;
        }
    }

    private void setVariable(final String caseInstanceId, final String variableName, final Object value) {
        cmmnRuntimeService.setVariable(caseInstanceId, variableName, value);
    }

    private void setVariable(final UUID zaakUUID, final String variableName, final Object value) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(CaseVariablesService.VAR_ZAAK_UUID, zaakUUID)
                .singleResult();

        if (caseInstance != null) {
            cmmnRuntimeService.setVariable(caseInstance.getId(), variableName, value);
        } else {
            throw new RuntimeException(
                    String.format("No case instance found for zaak with UUID: '%s'", zaakUUID.toString()));
        }
    }

    private void removeVariable(final UUID zaakUUID, final String variableName) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(CaseVariablesService.VAR_ZAAK_UUID, zaakUUID)
                .singleResult();

        if (caseInstance != null) {
            cmmnRuntimeService.removeVariable(caseInstance.getId(), variableName);
        }
    }
}
