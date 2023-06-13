/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.formulier_definitie
(
    id_formulier_definitie BIGINT                   NOT NULL,
    systeemnaam            VARCHAR                  NOT NULL,
    naam                   VARCHAR                  NOT NULL,
    beschrijving           VARCHAR,
    uitleg                 VARCHAR,
    creatiedatum           TIMESTAMP WITH TIME ZONE NOT NULL,
    wijzigingsdatum        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_formulier_definitie PRIMARY KEY (id_formulier_definitie),
    CONSTRAINT un_formulier_definitie_systeemnaam UNIQUE (systeemnaam)
);

CREATE SEQUENCE ${schema}.sq_formulier_definitie START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE INDEX idx_formulier_definitie_naam ON ${schema}.formulier_definitie USING btree (naam);
CREATE INDEX idx_formulier_definitie_beschrijving ON ${schema}.formulier_definitie USING btree (beschrijving);
CREATE INDEX idx_formulier_definitie_uitleg ON ${schema}.formulier_definitie USING btree (uitleg);
CREATE INDEX idx_formulier_definitie_creatiedatum ON ${schema}.formulier_definitie USING btree (creatiedatum);
CREATE INDEX idx_formulier_definitie_wijzigingsdatum ON ${schema}.formulier_definitie USING btree (wijzigingsdatum);


CREATE TABLE ${schema}.formulier_veld_definitie
(
    id_formulier_veld_definitie BIGINT  NOT NULL,
    id_formulier_definitie      BIGINT  NOT NULL,
    systeemnaam                 VARCHAR NOT NULL,
    volgorde                    INTEGER NOT NULL DEFAULT 0,
    label                       VARCHAR NOT NULL,
    veldtype                    VARCHAR NOT NULL,
    beschrijving                VARCHAR,
    helptekst                   VARCHAR,
    verplicht                   BOOLEAN NOT NULL DEFAULT FALSE,
    default_waarde              VARCHAR,
    meerkeuze_opties            VARCHAR,
    validaties                  VARCHAR,
    CONSTRAINT pk_formulier_veld_definitie PRIMARY KEY (id_formulier_veld_definitie),
    CONSTRAINT un_formulier_veld_definitie_formulier_definitie UNIQUE (id_formulier_veld_definitie, id_formulier_definitie),
    CONSTRAINT un_formulier_veld_definitie_systeemnaam UNIQUE (id_formulier_definitie, systeemnaam),
    CONSTRAINT fk_formulier_veld_definitie_formulier_definitie FOREIGN KEY (id_formulier_definitie)
        REFERENCES ${schema}.formulier_definitie (id_formulier_definitie)
            MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE ${schema}.sq_formulier_veld_definitie START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE INDEX idx_formulier_veld_definitie_id_formulier_definitie ON ${schema}.formulier_veld_definitie USING btree (id_formulier_definitie);
