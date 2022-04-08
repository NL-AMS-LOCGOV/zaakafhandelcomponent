package net.atos.zac.zaaksturing.model;

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

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

@Entity
@Table(schema = SCHEMA, name = "usereventlistener_parameters")
@SequenceGenerator(schema = SCHEMA, name = "sq_usereventlistener_parameters", sequenceName =
        "sq_usereventlistener_parameters", allocationSize = 1)
public class UserEventListenerParameters {

    @Id
    @GeneratedValue(generator = "sq_usereventlistener_parameters", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_usereventlistener_parameters")
    private Long id;

    @NotBlank
    @Column(name = "id_planitem_definition", nullable = false)
    private String planItemDefinitionID;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_zaakafhandelparameters", referencedColumnName = "id_zaakafhandelparameters")
    private ZaakafhandelParameters zaakafhandelParameters;

    @Column(name = "toelichting")
    private String toelichting;

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

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }
}
