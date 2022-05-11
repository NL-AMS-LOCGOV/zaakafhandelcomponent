/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.zoek_index
(
    id_zoek_index BIGINT  NOT NULL,
    uuid          UUID    NOT NULL,
    type          VARCHAR NOT NULL,
    status        VARCHAR NOT NULL,
    CONSTRAINT pk_zoek_index PRIMARY KEY (id_zoek_index),
    CONSTRAINT un_uuid UNIQUE (uuid)
);

CREATE SEQUENCE ${schema}.sq_zoek_index START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_zoek_index_type ON ${schema}.zoek_index USING btree (type);
CREATE INDEX idx_zoek_index_status ON ${schema}.zoek_index USING btree (status);

