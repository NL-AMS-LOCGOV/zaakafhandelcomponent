/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.ontkoppeld_document
(
    id_ontkoppeld_document BIGINT  NOT NULL,
    uuid_document          UUID    NOT NULL,
    id_document            VARCHAR NOT NULL,
    creatiedatum           DATE    NOT NULL,
    titel                  VARCHAR NOT NULL,
    bestandsnaam           VARCHAR,
    CONSTRAINT pk_ontkoppeld_document PRIMARY KEY (id_ontkoppeld_document),
    CONSTRAINT un_document UNIQUE (uuid_document)
);

CREATE SEQUENCE ${schema}.sq_ontkoppeld_document START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_ontkoppeld_document_id_document ON ${schema}.ontkoppeld_document USING btree (id_document);
CREATE INDEX idx_ontkoppeld_document_creatiedatum ON ${schema}.ontkoppeld_document USING btree (creatiedatum);
CREATE INDEX idx_ontkoppeld_document_titel ON ${schema}.ontkoppeld_document USING btree (titel);
CREATE INDEX idx_ontkoppeld_document_bestandsnaam ON ${schema}.ontkoppeld_document USING btree (bestandsnaam);
