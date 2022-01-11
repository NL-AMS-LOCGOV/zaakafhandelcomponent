/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(schema = SCHEMA, name = "planitem_parameters")
@SequenceGenerator(schema = SCHEMA, name = "sq_planitem_parameters", sequenceName = "sq_planitem_parameters", allocationSize = 1)
public class PlanItemParameters {

    @Id
    @GeneratedValue(generator = "sq_planitem_parameters", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_planitem_parameters")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_zaakafhandelparameters", referencedColumnName = "id_zaakafhandelparameters")
    private ZaakafhandelParameters zaakafhandelParameters;

    @NotBlank
    @Column(name = "id_planitem_definition", nullable = false)
    private String planItemDefinitionID;

    @NotBlank
    @Column(name = "id_formulier_definition", nullable = false)
    private String formulierDefinitieID;

    @Column(name = "id_groep", nullable = false)
    private String groepID;

    @Positive
    @Column(name = "doorlooptijd", nullable = false)
    private Integer doorlooptijd;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ZaakafhandelParameters getZaakafhandelParameters() {
        return zaakafhandelParameters;
    }

    public void setZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        this.zaakafhandelParameters = zaakafhandelParameters;
    }

    public String getPlanItemDefinitionID() {
        return planItemDefinitionID;
    }

    public void setPlanItemDefinitionID(final String planItemDefinitionID) {
        this.planItemDefinitionID = planItemDefinitionID;
    }

    public String getFormulierDefinitieID() {
        return formulierDefinitieID;
    }

    public void setFormulierDefinitieID(final String formulierDefinitieID) {
        this.formulierDefinitieID = formulierDefinitieID;
    }

    public String getGroepID() {
        return groepID;
    }

    public void setGroepID(final String groepID) {
        this.groepID = groepID;
    }

    public Integer getDoorlooptijd() {
        return doorlooptijd;
    }

    public void setDoorlooptijd(final Integer doorlooptijd) {
        this.doorlooptijd = doorlooptijd;
    }

}
