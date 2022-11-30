/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.mail_template_koppelingen
    ADD COLUMN id_zaakafhandelparameters BIGINT NOT NULL;

CREATE INDEX idx_mail_template_koppelingen_id_zaakafhandelparameters
    ON ${schema}.mail_template_koppelingen USING btree (id_zaakafhandelparameters);

ALTER TABLE ${schema}.mail_template_koppelingen
    ADD CONSTRAINT fk_mail_template_koppelingen_zaakafhandelparameters
        FOREIGN KEY (id_zaakafhandelparameters)
            REFERENCES ${schema}.zaakafhandelparameters (id_zaakafhandelparameters)
            MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ${schema}.mail_template_koppelingen DROP COLUMN uuid_zaaktype;
