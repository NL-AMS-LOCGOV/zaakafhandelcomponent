/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.zaakafhandelparameters
(
    id_zaakafhandelparameters   BIGINT   NOT NULL,
    uuid_zaaktype               UUID     NOT NULL,
    id_case_definition          VARCHAR  NOT NULL,
    id_groep                    VARCHAR  NOT NULL,
    gebruikersnaam_behandelaar  VARCHAR,
    CONSTRAINT pk_zaakafhandelparameters PRIMARY KEY (id_zaakafhandelparameters),
    CONSTRAINT un_zaaktype UNIQUE (uuid_zaaktype)
);

CREATE SEQUENCE ${schema}.sq_zaakafhandelparameters START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE ${schema}.planitem_parameters
(
    id_planitem_parameters      BIGINT   NOT NULL,
    id_zaakafhandelparameters   BIGINT   NOT NULL,
    id_planitem_definition      VARCHAR  NOT NULL,
    id_formulier_definition     VARCHAR  NOT NULL,
    id_groep                    VARCHAR,
    doorlooptijd                BIGINT   NOT NULL,
    CONSTRAINT pk_planitem_parameters PRIMARY KEY (id_planitem_parameters),
    CONSTRAINT un_zaakafhandelparameters_planitem_definition UNIQUE (id_zaakafhandelparameters, id_planitem_definition),
    CONSTRAINT fk_planitem_parameters_zaakafhandelparameters FOREIGN KEY (id_zaakafhandelparameters)
        REFERENCES ${schema}.zaakafhandelparameters (id_zaakafhandelparameters)
        MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_planitem_parameters_id_zaakafhandelparameters ON ${schema}.planitem_parameters USING btree (id_zaakafhandelparameters);
CREATE SEQUENCE ${schema}.sq_planitem_parameters START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
