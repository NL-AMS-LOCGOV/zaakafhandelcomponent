/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.inbox_productaanvraag
(
    id_inbox_productaanvraag    BIGINT  NOT NULL,
    uuid_productaanvraag_object UUID    NOT NULL,
    uuid_aanvraagdocument       UUID,
    productaanvraag_type        VARCHAR NOT NULL,
    ontvangstdatum              DATE    NOT NULL,
    id_initiator                VARCHAR,
    CONSTRAINT pk_inbox_productaanvraag PRIMARY KEY (id_inbox_productaanvraag),
    CONSTRAINT un_inbox_productaanvraag_uuid_productaanvraag UNIQUE (uuid_productaanvraag_object),
    CONSTRAINT un_inbox_productaanvraag_uuid_aanvraagdocument UNIQUE (uuid_aanvraagdocument)
);

CREATE SEQUENCE ${schema}.sq_inbox_productaanvraag START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_inbox_productaanvraag_id_initiator ON ${schema}.inbox_productaanvraag USING btree (id_initiator);
CREATE INDEX idx_inbox_productaanvraag_ontvangstdatum ON ${schema}.inbox_productaanvraag USING btree (ontvangstdatum);
CREATE INDEX idx_inbox_productaanvraag_productaanvraag_type ON ${schema}.inbox_productaanvraag USING btree (productaanvraag_type);
