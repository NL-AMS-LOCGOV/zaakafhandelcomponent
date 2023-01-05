package net.atos.zac.flowable;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;

@ApplicationScoped
@Transactional
public class ZaakVariabelenService {

    public static final String VAR_ZAAK_UUID = "zaakUUID";

    public static final String VAR_ZAAK_IDENTIFICATIE = "zaakIdentificatie";

    public static final String VAR_ZAAKTYPE_UUUID = "zaaktypeUUID";

    public static final String VAR_ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    public static final String VAR_ZAAKTYPE_DOMEIN = "zaaktypeDomein";

    static final String VAR_ZAAK_DATA = "zaakData";

    private static final String VAR_ONTVANGSTBEVESTIGING_VERSTUURD = "ontvangstbevestigingVerstuurd";

    private static final String VAR_DATUMTIJD_OPGESCHORT = "datumTijdOpgeschort";

    private static final String VAR_VERWACHTE_DAGEN_OPGESCHORT = "verwachteDagenOpgeschort";

    // Wordt gebruikt binnen het CMMN model
    private static final String VAR_ONTVANKELIJK = "ontvankelijk";

    @Inject
    private CmmnRuntimeService cmmnRuntimeService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    public UUID readZaakUUID(final PlanItemInstance planItemInstance) {
        return (UUID) readCaseVariable(planItemInstance, VAR_ZAAK_UUID);
    }

    public String readZaakIdentificatie(final PlanItemInstance planItemInstance) {
        return (String) readCaseVariable(planItemInstance, VAR_ZAAK_IDENTIFICATIE);
    }

    public UUID readZaaktypeUUID(final PlanItemInstance planItemInstance) {
        return (UUID) readCaseVariable(planItemInstance, VAR_ZAAKTYPE_UUUID);
    }

    public String readZaaktypeOmschrijving(final PlanItemInstance planItemInstance) {
        return (String) readCaseVariable(planItemInstance, VAR_ZAAKTYPE_OMSCHRIJVING);
    }

    public Optional<String> readZaaktypeDomein(final PlanItemInstance planItemInstance) {
        final Object caseVariable = findCaseVariable(planItemInstance, VAR_ZAAKTYPE_DOMEIN);
        return caseVariable != null ? Optional.of((String) caseVariable) : Optional.empty();
    }

    public Optional<Boolean> findOntvangstbevestigingVerstuurd(final UUID zaakUUID) {
        final Object caseVariable = findCaseVariable(zaakUUID, VAR_ONTVANGSTBEVESTIGING_VERSTUURD);
        return caseVariable != null ? Optional.of((Boolean) caseVariable) : Optional.empty();
    }

    public void setOntvangstbevestigingVerstuurd(final UUID zaakUUID, final Boolean ontvangstbevestigingVerstuurd) {
        setVariable(zaakUUID, VAR_ONTVANGSTBEVESTIGING_VERSTUURD, ontvangstbevestigingVerstuurd);
    }

    public void setOntvankelijk(final PlanItemInstance planItemInstance, final Boolean ontvankelijk) {
        setVariable(planItemInstance, VAR_ONTVANKELIJK, ontvankelijk);
    }

    public Optional<ZonedDateTime> findDatumtijdOpgeschort(final UUID zaakUUID) {
        final Object caseVariable = findCaseVariable(zaakUUID, VAR_DATUMTIJD_OPGESCHORT);
        return caseVariable != null ? Optional.of((ZonedDateTime) caseVariable) : Optional.empty();
    }

    public void setDatumtijdOpgeschort(final UUID zaakUUID, final ZonedDateTime datumtijOpgeschort) {
        setVariable(zaakUUID, VAR_DATUMTIJD_OPGESCHORT, datumtijOpgeschort);
    }

    public void removeDatumtijdOpgeschort(final UUID zaakUUID) {
        removeVariable(zaakUUID, VAR_DATUMTIJD_OPGESCHORT);
    }

    public Optional<Integer> findVerwachteDagenOpgeschort(final UUID zaakUUID) {
        final Object caseVariable = findCaseVariable(zaakUUID, VAR_VERWACHTE_DAGEN_OPGESCHORT);
        return caseVariable != null ? Optional.of((Integer) caseVariable) : Optional.empty();
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

    private Object readCaseVariable(final PlanItemInstance planItemInstance, final String variableName) {
        final Object caseVariabele = findCaseVariable(planItemInstance, variableName);
        if (caseVariabele == null) {
            throw new RuntimeException(
                    String.format("No variable found with name '%s' for case instance id '%s'", variableName,
                                  planItemInstance.getCaseInstanceId()));
        }
        return caseVariabele;
    }

    private Object findCaseVariable(final PlanItemInstance planItemInstance, final String variableName) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(planItemInstance.getCaseInstanceId())
                .includeCaseVariables()
                .singleResult();
        if (caseInstance != null) {
            return caseInstance.getCaseVariables().get(variableName);
        }

        final HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery()
                .caseInstanceId(planItemInstance.getCaseInstanceId())
                .includeCaseVariables()
                .singleResult();
        if (historicCaseInstance != null) {
            return historicCaseInstance.getCaseVariables().get(variableName);
        }

        return null;
    }

    private Object findCaseVariable(final UUID zaakUUID, final String variableName) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .includeCaseVariables()
                .singleResult();
        if (caseInstance != null) {
            return caseInstance.getCaseVariables().get(variableName);
        }

        final HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .includeCaseVariables()
                .singleResult();
        if (historicCaseInstance != null) {
            return historicCaseInstance.getCaseVariables().get(variableName);
        }

        return null;
    }

    private void setVariable(final PlanItemInstance planItemInstance, final String variableName, final Object value) {
        cmmnRuntimeService.setVariable(planItemInstance.getCaseInstanceId(), variableName, value);
    }

    private void setVariable(final UUID zaakUUID, final String variableName, final Object value) {
        final CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
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
                .variableValueEquals(VAR_ZAAK_UUID, zaakUUID)
                .singleResult();

        if (caseInstance != null) {
            cmmnRuntimeService.removeVariable(caseInstance.getId(), variableName);
        }
    }
}
