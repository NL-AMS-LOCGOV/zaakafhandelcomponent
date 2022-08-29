/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
CREATE SEQUENCE ${schema}.sq_referentie_tabel START WITH 2 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.referentie_tabel
(
    id_referentie_tabel BIGINT  NOT NULL,
    code                VARCHAR NOT NULL, -- bijvoorbeeld: ADVIES
    naam                VARCHAR NOT NULL, -- bijvoorbeeld: Advies
    CONSTRAINT pk_referentie_tabel PRIMARY KEY (id_referentie_tabel),
    CONSTRAINT un_referentie_tabel_code UNIQUE (code)
);
CREATE INDEX ix_referentie_tabel_naam ON ${schema}.referentie_tabel USING btree (naam);

INSERT INTO ${schema}.referentie_tabel(id_referentie_tabel, code, naam)
VALUES (1, 'ADVIES', 'Advies');

CREATE SEQUENCE ${schema}.sq_referentie_waarde START WITH 6 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.referentie_waarde
(
    id_referentie_waarde BIGINT  NOT NULL,
    id_referentie_tabel  BIGINT  NOT NULL,
    naam                 VARCHAR NOT NULL, -- bijvoorbeeld: Positief
    CONSTRAINT pk_referentie_waarde PRIMARY KEY (id_referentie_waarde),
    CONSTRAINT un_referentie_waarde_naam UNIQUE (id_referentie_tabel, naam),
    CONSTRAINT fk_referentie_waarde_tabel FOREIGN KEY (id_referentie_tabel)
        REFERENCES ${schema}.referentie_tabel (id_referentie_tabel)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE CASCADE
);
CREATE INDEX ix_referentie_waarde_tabel ON ${schema}.referentie_waarde USING btree (id_referentie_tabel);
CREATE INDEX ix_referentie_waarde_naam ON ${schema}.referentie_waarde USING btree (naam);

INSERT INTO ${schema}.referentie_waarde(id_referentie_waarde, id_referentie_tabel, naam)
VALUES (1, 1, 'Positief');
INSERT INTO ${schema}.referentie_waarde(id_referentie_waarde, id_referentie_tabel, naam)
VALUES (2, 1, 'Positief onder voorwaarde');
INSERT INTO ${schema}.referentie_waarde(id_referentie_waarde, id_referentie_tabel, naam)
VALUES (3, 1, 'Negatief');
INSERT INTO ${schema}.referentie_waarde(id_referentie_waarde, id_referentie_tabel, naam)
VALUES (4, 1, 'Negatief tenzij');
INSERT INTO ${schema}.referentie_waarde(id_referentie_waarde, id_referentie_tabel, naam)
VALUES (5, 1, 'Niet beoordeeld');

CREATE SEQUENCE ${schema}.sq_humantask_referentie_tabel START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.humantask_referentie_tabel
(
    id_humantask_referentie_tabel BIGINT  NOT NULL,
    id_referentie_tabel           BIGINT  NOT NULL,
    id_humantask_parameters       BIGINT  NOT NULL,
    veld                          VARCHAR NOT NULL, -- bijvoorbeeld: advies
    CONSTRAINT pk_humantask_referentie_tabel PRIMARY KEY (id_humantask_referentie_tabel),
    CONSTRAINT un_humantask_referentie_tabel_veld UNIQUE (id_humantask_parameters, veld),
    CONSTRAINT fk_humantask_referentie_tabel_tabel FOREIGN KEY (id_referentie_tabel)
        REFERENCES ${schema}.referentie_tabel (id_referentie_tabel)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_humantask_referentie_tabel_humantask FOREIGN KEY (id_humantask_parameters)
        REFERENCES ${schema}.humantask_parameters (id_humantask_parameters)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE CASCADE
);
CREATE INDEX ix_humantask_referentie_tabel_tabel ON ${schema}.humantask_referentie_tabel USING btree (id_referentie_tabel);
CREATE INDEX ix_humantask_referentie_tabel_humantask ON ${schema}.humantask_referentie_tabel USING btree (id_humantask_parameters);
CREATE INDEX ix_humantask_referentie_tabel_veld ON ${schema}.humantask_referentie_tabel USING btree (veld);
