/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.zoekopdracht
(
    id_zoekopdracht   BIGINT  NOT NULL,
    id_lijst          VARCHAR NOT NULL,
    id_medewerker     VARCHAR NOT NULL,
    actief            BOOLEAN NOT NULL DEFAULT FALSE,
    creatiedatum      DATE    NOT NULL,
    naam              VARCHAR NOT NULL,
    json_zoekopdracht VARCHAR NOT NULL,
    CONSTRAINT pk_zoekopdracht PRIMARY KEY (id_zoekopdracht)
);

CREATE SEQUENCE ${schema}.sq_zoekopdracht START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_zoekopdracht_id_lijst ON ${schema}.zoekopdracht USING btree (id_lijst);
CREATE INDEX idx_zoekopdracht_id_medewerker ON ${schema}.zoekopdracht USING btree (id_medewerker);
CREATE INDEX idx_zoekopdracht_creatiedatum ON ${schema}.zoekopdracht USING btree (creatiedatum);
CREATE INDEX idx_zoekopdracht_naam ON ${schema}.zoekopdracht USING btree (naam);

