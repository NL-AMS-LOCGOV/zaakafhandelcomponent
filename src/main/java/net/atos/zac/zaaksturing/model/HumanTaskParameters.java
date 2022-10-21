/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import net.atos.zac.app.planitems.model.DefaultHumanTaskFormulierKoppeling;

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

    @Column(name = "id_formulier_definition")
    private String formulierDefinitieID;

    @NotBlank
    @Column(name = "id_planitem_definition", nullable = false)
    private String planItemDefinitionID;

    @Column(name = "id_groep", nullable = false)
    private String groepID;

    @Min(value = 0)
    @Column(name = "doorlooptijd")
    private Integer doorlooptijd;

    @OneToMany(mappedBy = "humantask", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<HumanTaskReferentieTabel> referentieTabellen = new ArrayList<>();

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

    public String getFormulierDefinitieID() {
        return formulierDefinitieID != null ? formulierDefinitieID : DefaultHumanTaskFormulierKoppeling.readFormulierDefinitie(planItemDefinitionID).name();
    }

    public void setFormulierDefinitieID(final String formulierDefinitieID) {
        this.formulierDefinitieID = formulierDefinitieID;
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

    public List<HumanTaskReferentieTabel> getReferentieTabellen() {
        return Collections.unmodifiableList(referentieTabellen);
    }

    public void setReferentieTabellen(final List<HumanTaskReferentieTabel> referentieTabellen) {
        this.referentieTabellen.clear();
        referentieTabellen.forEach(this::addReferentieTabel);
    }

    private HumanTaskReferentieTabel getReferentieTabel(final String veld) {
        return referentieTabellen.stream()
                .filter(referentieTabel -> referentieTabel.getVeld().equals(veld))
                .findAny()
                .orElse(null);
    }

    private boolean addReferentieTabel(final HumanTaskReferentieTabel referentieTabel) {
        referentieTabel.setHumantask(this);
        return referentieTabellen.add(referentieTabel);
    }

    private boolean removeReferentieTabel(final HumanTaskReferentieTabel referentieTabel) {
        return referentieTabellen.remove(referentieTabel);
    }

    public ReferentieTabel getTabel(final String veld) {
        final HumanTaskReferentieTabel referentieTabel = getReferentieTabel(veld);
        return referentieTabel == null ? null : referentieTabel.getTabel();
    }

    public ReferentieTabel putTabel(final String veld, final ReferentieTabel tabel) {
        final HumanTaskReferentieTabel referentieTabel = getReferentieTabel(veld);
        if (referentieTabel == null) {
            addReferentieTabel(new HumanTaskReferentieTabel(veld, tabel));
            return null;
        } else {
            final ReferentieTabel previous = referentieTabel.getTabel();
            referentieTabel.setTabel(tabel);
            return previous;
        }
    }

    public ReferentieTabel removeTabel(final String veld) {
        final HumanTaskReferentieTabel referentieTabel = getReferentieTabel(veld);
        if (referentieTabel == null) {
            return null;
        }
        removeReferentieTabel(referentieTabel);
        return referentieTabel.getTabel();
    }

    public boolean isActief() {
        return actief;
    }

    public void setActief(final boolean actief) {
        this.actief = actief;
    }
}
