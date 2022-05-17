/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.inbox_document
(
    id_inbox_document                BIGINT  NOT NULL,
    uuid_enkelvoudiginformatieobject UUID    NOT NULL,
    id_enkelvoudiginformatieobject   VARCHAR NOT NULL,
    creatiedatum                     DATE    NOT NULL,
    titel                            VARCHAR NOT NULL,
    bestandsnaam                     VARCHAR,
    CONSTRAINT pk_inbox_document PRIMARY KEY (id_inbox_document),
    CONSTRAINT un_inbox_document_uuid_enkelvoudiginformatieobject UNIQUE (uuid_enkelvoudiginformatieobject)
);

CREATE SEQUENCE ${schema}.sq_inbox_document START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_inbox_document_id_document ON ${schema}.inbox_document USING btree (id_enkelvoudiginformatieobject);
CREATE INDEX idx_inbox_document_creatiedatum ON ${schema}.inbox_document USING btree (creatiedatum);
CREATE INDEX idx_inbox_document_titel ON ${schema}.inbox_document USING btree (titel);
CREATE INDEX idx_inbox_document_bestandsnaam ON ${schema}.inbox_document USING btree (bestandsnaam);
