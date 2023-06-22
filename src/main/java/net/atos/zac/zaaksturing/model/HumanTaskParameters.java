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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "humantask_parameters")
@SequenceGenerator(schema = SCHEMA, name = "sq_humantask_parameters", sequenceName = "sq_humantask_parameters",
        allocationSize = 1)
public class HumanTaskParameters {

    @Id
    @GeneratedValue(generator = "sq_humantask_parameters", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_humantask_parameters")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_zaakafhandelparameters", referencedColumnName = "id_zaakafhandelparameters")
    private ZaakafhandelParameters zaakafhandelParameters;

    @Column(name = "actief")
    private boolean actief;

    @Column(name = "id_start_formulier_definition")
    private String startformulierDefinitieID;

    @Column(name = "id_afhandel_formulier_definition")
    private String afhandelformulierDefinitieID;

    @NotBlank
    @Column(name = "id_planitem_definition", nullable = false)
    private String planItemDefinitionID;

    @Column(name = "id_groep", nullable = false)
    private String groepID;

    @Min(value = 0)
    @Column(name = "doorlooptijd")
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

    public String getStartformulierDefinitieID() {
        return startformulierDefinitieID;
    }

    public void setStartformulierDefinitieID(final String startformulierDefinitieID) {
        this.startformulierDefinitieID = startformulierDefinitieID;
    }

    public String getAfhandelformulierDefinitieID() {
        return afhandelformulierDefinitieID;
    }

    public void setAfhandelformulierDefinitieID(final String afhandelformulierDefinitieID) {
        this.afhandelformulierDefinitieID = afhandelformulierDefinitieID;
    }

    public String getPlanItemDefinitionID() {
        return planItemDefinitionID;
    }

    public void setPlanItemDefinitionID(final String planItemDefinitionID) {
        this.planItemDefinitionID = planItemDefinitionID;
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

    public boolean isActief() {
        return actief;
    }

    public void setActief(final boolean actief) {
        this.actief = actief;
    }
}
