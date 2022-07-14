/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

DELETE
FROM ${schema}.zaakafhandelparameters;

ALTER TABLE ${schema}.zaakafhandelparameters
    ALTER COLUMN id_case_definition DROP NOT NULL,
    ALTER COLUMN id_groep DROP NOT NULL,
    ADD COLUMN zaaktype_omschrijving VARCHAR                  NOT NULL,
    ADD COLUMN creatiedatum          TIMESTAMP WITH TIME ZONE NOT NULL;


CREATE INDEX idx_zaakafhandelparameters_zaaktype_omschrijving ON ${schema}.zaakafhandelparameters USING btree (zaaktype_omschrijving);
CREATE INDEX idx_zaakafhandelparameters_creatiedatum ON ${schema}.zaakafhandelparameters USING btree (creatiedatum);
