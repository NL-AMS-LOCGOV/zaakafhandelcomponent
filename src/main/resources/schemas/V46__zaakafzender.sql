/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE SEQUENCE ${schema}.sq_zaakafzender START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE ${schema}.zaakafzender
(
    id_zaakafzender           BIGINT  NOT NULL,
    id_zaakafhandelparameters BIGINT  NOT NULL,
    mail                      BIGINT  NOT NULL,
    default_mail              BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_zaakafzender PRIMARY KEY (id_zaakafzender),
    CONSTRAINT fk_zaakafzender_zaakafhandelparameters FOREIGN KEY (id_zaakafhandelparameters)
        REFERENCES ${schema}.zaakafhandelparameters (id_zaakafhandelparameters)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE INDEX idx_zaakafzender_zaakafhandelparameters ON ${schema}.zaakafzender
    USING btree (id_zaakafhandelparameters);
CREATE INDEX idx_zaakafzender_mail ON ${schema}.zaakafzender
    USING btree (mail);
