/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "zaakafhandelparameters")
@SequenceGenerator(schema = SCHEMA, name = "sq_zaakafhandelparameters", sequenceName = "sq_zaakafhandelparameters", allocationSize = 1)
public class ZaakafhandelParameters {

    /**
     * Naam van property: {@link ZaakafhandelParameters#zaakTypeUUID}
     */
    public static final String ZAAKTYPE_UUID = "zaakTypeUUID";

    @Id
    @GeneratedValue(generator = "sq_zaakafhandelparameters", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_zaakafhandelparameters")
    private Long id;

    @NotNull
    @Column(name = "uuid_zaaktype", nullable = false)
    private UUID zaakTypeUUID;

    @NotBlank
    @Column(name = "id_case_definition", nullable = false)
    private String caseDefinitionID;

    @NotBlank
    @Column(name = "id_groep", nullable = false)
    private String groepID;

    @Column(name = "gebruikersnaam_behandelaar")
    private String gebruikersnaamMedewerker;

    @OneToMany(mappedBy = "zaakafhandelParameters", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<PlanItemParameters> planItemParametersCollection;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getZaakTypeUUID() {
        return zaakTypeUUID;
    }

    public void setZaakTypeUUID(final UUID zaakTypeUUID) {
        this.zaakTypeUUID = zaakTypeUUID;
    }

    public String getCaseDefinitionID() {
        return caseDefinitionID;
    }

    public void setCaseDefinitionID(final String caseDefinitionID) {
        this.caseDefinitionID = caseDefinitionID;
    }

    public String getGroepID() {
        return groepID;
    }

    public void setGroepID(final String groepID) {
        this.groepID = groepID;
    }

    public String getGebruikersnaamMedewerker() {
        return gebruikersnaamMedewerker;
    }

    public void setGebruikersnaamMedewerker(final String gebruikersnaamMedewerker) {
        this.gebruikersnaamMedewerker = gebruikersnaamMedewerker;
    }

    public Collection<PlanItemParameters> getPlanItemParametersCollection() {
        if (planItemParametersCollection == null) {
            planItemParametersCollection = new ArrayList<>();
        }
        return planItemParametersCollection;
    }

    public void addPlanItemParameters(PlanItemParameters planItemParameters) {
        planItemParameters.setZaakafhandelParameters(this);
        getPlanItemParametersCollection().add(planItemParameters);
    }

    public void setPlanItemParametersCollection(final Collection<PlanItemParameters> collection) {
        getPlanItemParametersCollection().clear();
        collection.forEach(this::addPlanItemParameters);
    }
}
