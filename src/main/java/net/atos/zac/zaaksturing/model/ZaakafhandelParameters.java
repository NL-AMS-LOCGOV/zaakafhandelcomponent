/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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

    // The set is necessary for Hibernate when you have more than one eager collection on an entity.
    @OneToMany(mappedBy = "zaakafhandelParameters", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<HumanTaskParameters> humanTaskParametersCollection;

    // The set is necessary for Hibernate when you have more than one eager collection on an entity.
    @OneToMany(mappedBy = "zaakafhandelParameters", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<UserEventListenerParameters> userEventListenerParametersCollection;

    // The set is necessary for Hibernate when you have more than one eager collection on an entity.
    @OneToMany(mappedBy = "zaakafhandelParameters", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ZaakbeeindigParameter> zaakbeeindigParameters;

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

    public Collection<HumanTaskParameters> getHumanTaskParametersCollection() {
        if (humanTaskParametersCollection == null) {
            humanTaskParametersCollection = new HashSet<>();
        }
        return humanTaskParametersCollection;
    }

    public void addHumanTaskParameters(HumanTaskParameters humanTaskParameters) {
        humanTaskParameters.setZaakafhandelParameters(this);
        getHumanTaskParametersCollection().add(humanTaskParameters);
    }

    public void setHumanTaskParametersCollection(final Collection<HumanTaskParameters> collection) {
        getHumanTaskParametersCollection().clear();
        collection.forEach(this::addHumanTaskParameters);
    }

    public Collection<ZaakbeeindigParameter> getZaakbeeindigParameters() {
        if (zaakbeeindigParameters == null) {
            zaakbeeindigParameters = new HashSet<>();
        }
        return zaakbeeindigParameters;
    }

    public void addZaakbeeindigParameter(ZaakbeeindigParameter zaakbeeindigParameter) {
        zaakbeeindigParameter.setZaakafhandelParameters(this);
        getZaakbeeindigParameters().add(zaakbeeindigParameter);
    }

    public void setZaakbeeindigParameters(final Collection<ZaakbeeindigParameter> collection) {
        getZaakbeeindigParameters().clear();
        collection.forEach(this::addZaakbeeindigParameter);
    }

    public Collection<UserEventListenerParameters> getUserEventListenerParameters() {
        if (userEventListenerParametersCollection == null) {
            userEventListenerParametersCollection = new HashSet<>();
        }
        return userEventListenerParametersCollection;
    }

    public void addUserEventListenerParameter(final UserEventListenerParameters userEventListenerParameters) {
        userEventListenerParameters.setZaakafhandelParameters(this);
        getUserEventListenerParameters().add(userEventListenerParameters);
    }

    public void setUserEventListenerParameters(final Collection<UserEventListenerParameters> collection) {
        getUserEventListenerParameters().clear();
        collection.forEach(this::addUserEventListenerParameter);
    }
}

