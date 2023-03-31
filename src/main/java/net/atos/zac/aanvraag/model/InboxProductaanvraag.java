/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "inbox_productaanvraag")
@SequenceGenerator(schema = SCHEMA, name = "sq_inbox_productaanvraag", sequenceName = "sq_inbox_productaanvraag", allocationSize = 1)
public class InboxProductaanvraag {

    /**
     * Naam van property: {@link InboxProductaanvraag#initiatorID}
     */
    public static final String INITIATOR = "initiatorID";

    /**
     * Naam van property: {@link InboxProductaanvraag#type}
     */
    public static final String TYPE = "type";

    /**
     * Naam van property: {@link InboxProductaanvraag#ontvangstdatum}
     */
    public static final String ONTVANGSTDATUM = "ontvangstdatum";

    @Id
    @GeneratedValue(generator = "sq_inbox_productaanvraag", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_inbox_productaanvraag")
    private Long id;

    @NotNull
    @Column(name = "uuid_productaanvraag_object", nullable = false)
    private UUID productaanvraagObjectUUID;

    @Column(name = "uuid_aanvraagdocument")
    private UUID aanvraagdocumentUUID;

    @NotNull
    @Column(name = "ontvangstdatum", nullable = false)
    private LocalDate ontvangstdatum;

    @NotBlank
    @Column(name = "productaanvraag_type", nullable = false)
    private String type;

    @Column(name = "id_initiator")
    private String initiatorID;


    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getProductaanvraagObjectUUID() {
        return productaanvraagObjectUUID;
    }

    public void setProductaanvraagObjectUUID(final UUID productaanvraagObjectUUID) {
        this.productaanvraagObjectUUID = productaanvraagObjectUUID;
    }

    public UUID getAanvraagdocumentUUID() {
        return aanvraagdocumentUUID;
    }

    public void setAanvraagdocumentUUID(final UUID aanvraagdocumentUUID) {
        this.aanvraagdocumentUUID = aanvraagdocumentUUID;
    }

    public LocalDate getOntvangstdatum() {
        return ontvangstdatum;
    }

    public void setOntvangstdatum(final LocalDate ontvangstdatum) {
        this.ontvangstdatum = ontvangstdatum;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getInitiatorID() {
        return initiatorID;
    }

    public void setInitiatorID(final String initiatorID) {
        this.initiatorID = initiatorID;
    }
}
