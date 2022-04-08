/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.usereventlistener_parameters
(
    id_usereventlistener_parameters     BIGINT   NOT NULL,
    id_planitem_definition              VARCHAR  NOT NULL,
    id_zaakafhandelparameters           BIGINT   NOT NULL,
    toelichting                         VARCHAR,
    CONSTRAINT pk_usereventlistener_parameters PRIMARY KEY (id_usereventlistener_parameters),
    CONSTRAINT fk_usereventlistener_parameters_zaakafhandelparameters FOREIGN KEY (id_zaakafhandelparameters)
        REFERENCES ${schema}.zaakafhandelparameters (id_zaakafhandelparameters)
        MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_usereventlistener_parameters_id_zaakafhandelparameters ON ${schema}.usereventlistener_parameters USING btree (id_zaakafhandelparameters);
CREATE SEQUENCE ${schema}.sq_usereventlistener_parameters START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
