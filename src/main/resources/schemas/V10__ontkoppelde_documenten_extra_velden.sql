/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

DELETE
FROM ${schema}.ontkoppeld_document;

ALTER TABLE ${schema}.ontkoppeld_document
    ADD COLUMN id_zaak            VARCHAR                  NOT NULL,
    ADD COLUMN id_ontkoppeld_door VARCHAR                  NOT NULL,
    ADD COLUMN ontkoppeld_op      TIMESTAMP WITH TIME ZONE NOT NULL,
    ADD COLUMN reden              VARCHAR;

CREATE INDEX idx_ontkoppeld_document_id_zaak ON ${schema}.ontkoppeld_document USING btree (id_zaak);
CREATE INDEX idx_ontkoppeld_document_ontkoppeld_op ON ${schema}.ontkoppeld_document USING btree (ontkoppeld_op);
CREATE INDEX idx_ontkoppeld_document_id_ontkoppeld_door ON ${schema}.ontkoppeld_document USING btree (id_ontkoppeld_door);
