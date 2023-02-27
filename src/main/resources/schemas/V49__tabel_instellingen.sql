/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.tabel_instellingen
(
    id_tabel_instellingen BIGINT  NOT NULL,
    id_lijst_enum         VARCHAR NOT NULL,
    id_medewerker         VARCHAR NOT NULL,
    aantal_per_pagina     INT     NOT NULL,
    CONSTRAINT pk_tabel_instellingen PRIMARY KEY (id_tabel_instellingen),
    CONSTRAINT un_tabel_instellingen_lijst_medewerker UNIQUE (id_lijst_enum, id_medewerker)
);

CREATE SEQUENCE ${schema}.sq_tabel_instellingen START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_tabel_instellingen_id_lijst_enum ON ${schema}.tabel_instellingen USING btree (id_lijst_enum);
CREATE INDEX idx_tabel_instellingen_id_medewerker ON ${schema}.tabel_instellingen USING btree (id_medewerker);
