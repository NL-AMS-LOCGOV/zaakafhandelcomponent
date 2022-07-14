/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
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

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(schema = SCHEMA, name = "zaakafhandelparameters")
@SequenceGenerator(schema = SCHEMA, name = "sq_zaakafhandelparameters", sequenceName = "sq_zaakafhandelparameters", allocationSize = 1)
public class ZaakafhandelParameters {

    /** Naam van property: {@link ZaakafhandelParameters#zaakTypeUUID} */
    public static final String ZAAKTYPE_UUID = "zaakTypeUUID";

    /** Naam van property: {@link ZaakafhandelParameters#zaaktypeOmschrijving} */
    public static final String ZAAKTYPE_OMSCHRIJVING = "zaaktypeOmschrijving";

    /** Naam van property: {@link ZaakafhandelParameters#creatiedatum} */
    public static final String CREATIEDATUM = "creatiedatum";

    @Id
    @GeneratedValue(generator = "sq_zaakafhandelparameters", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_zaakafhandelparameters")
    private Long id;

    @NotNull
    @Column(name = "uuid_zaaktype", nullable = false)
    private UUID zaakTypeUUID;

    @NotBlank
    @Column(name = "zaaktype_omschrijving", nullable = false)
    private String zaaktypeOmschrijving;

    @Column(name = "id_case_definition", nullable = false)
    private String caseDefinitionID;

    @Column(name = "id_groep", nullable = false)
    private String groepID;

    @Column(name = "gebruikersnaam_behandelaar")
    private String gebruikersnaamMedewerker;

    @Column(name = "eindatum_gepland_waarschuwing")
    private Integer einddatumGeplandWaarschuwing;

    @Column(name = "uiterlijke_einddatum_afdoening_waarschuwing")
    private Integer uiterlijkeEinddatumAfdoeningWaarschuwing;

    @Column(name = "niet_ontvankelijk_resultaattype_uuid")
    private UUID nietOntvankelijkResultaattype;

    @Column(name = "creatiedatum", nullable = false)
    private ZonedDateTime creatiedatum;

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

    public Integer getEinddatumGeplandWaarschuwing() {
        return einddatumGeplandWaarschuwing;
    }

    public void setEinddatumGeplandWaarschuwing(final Integer streefdatumWaarschuwing) {
        this.einddatumGeplandWaarschuwing = streefdatumWaarschuwing;
    }

    public Integer getUiterlijkeEinddatumAfdoeningWaarschuwing() {
        return uiterlijkeEinddatumAfdoeningWaarschuwing;
    }

    public void setUiterlijkeEinddatumAfdoeningWaarschuwing(final Integer fataledatumWaarschuwing) {
        this.uiterlijkeEinddatumAfdoeningWaarschuwing = fataledatumWaarschuwing;
    }

    public UUID getNietOntvankelijkResultaattype() {
        return nietOntvankelijkResultaattype;
    }

    public void setNietOntvankelijkResultaattype(final UUID nietOntvankelijkResultaattype) {
        this.nietOntvankelijkResultaattype = nietOntvankelijkResultaattype;
    }

    public Set<HumanTaskParameters> getHumanTaskParametersCollection() {
        return humanTaskParametersCollection != null ? humanTaskParametersCollection : Collections.emptySet();
    }

    public void setHumanTaskParametersCollection(final Collection<HumanTaskParameters> humanTaskParametersCollection) {
        if (this.humanTaskParametersCollection == null) {
            this.humanTaskParametersCollection = new HashSet<>();
        } else {
            this.humanTaskParametersCollection.clear();
        }
        humanTaskParametersCollection.forEach(this::addHumanTaskParameters);
    }

    public Set<ZaakbeeindigParameter> getZaakbeeindigParameters() {
        return zaakbeeindigParameters != null ? zaakbeeindigParameters : Collections.emptySet();
    }

    public void setZaakbeeindigParameters(final Collection<ZaakbeeindigParameter> zaakbeeindigParameters) {
        if (this.zaakbeeindigParameters == null) {
            this.zaakbeeindigParameters = new HashSet<>();
        } else {
            this.zaakbeeindigParameters.clear();
        }
        zaakbeeindigParameters.forEach(this::addZaakbeeindigParameter);
    }

    public Set<UserEventListenerParameters> getUserEventListenerParametersCollection() {
        return userEventListenerParametersCollection != null ? userEventListenerParametersCollection : Collections.emptySet();
    }

    public void setUserEventListenerParametersCollection(final Collection<UserEventListenerParameters> userEventListenerParametersCollection) {
        if (this.userEventListenerParametersCollection == null) {
            this.userEventListenerParametersCollection = new HashSet<>();
        } else {
            this.userEventListenerParametersCollection.clear();
        }
        userEventListenerParametersCollection.forEach(this::addUserEventListenerParameters);
    }

    private void addHumanTaskParameters(final HumanTaskParameters humanTaskParameters) {
        humanTaskParameters.setZaakafhandelParameters(this);
        humanTaskParametersCollection.add(humanTaskParameters);
    }

    private void addZaakbeeindigParameter(final ZaakbeeindigParameter zaakbeeindigParameter) {
        zaakbeeindigParameter.setZaakafhandelParameters(this);
        zaakbeeindigParameters.add(zaakbeeindigParameter);
    }

    private void addUserEventListenerParameters(final UserEventListenerParameters userEventListenerParameters) {
        userEventListenerParameters.setZaakafhandelParameters(this);
        userEventListenerParametersCollection.add(userEventListenerParameters);
    }

    public String getZaaktypeOmschrijving() {
        return zaaktypeOmschrijving;
    }

    public void setZaaktypeOmschrijving(final String zaaktypeOmschrijving) {
        this.zaaktypeOmschrijving = zaaktypeOmschrijving;
    }

    public ZonedDateTime getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final ZonedDateTime creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    /**
     * Geeft aan dat er voldoende gegevens zijn ingevuld om een zaak te starten
     *
     * @return true indien er een zaak kan worden gestart
     */
    public boolean isValide() {
        return StringUtils.isNotBlank(groepID) && StringUtils.isNotBlank(caseDefinitionID) && nietOntvankelijkResultaattype != null;
    }

}

