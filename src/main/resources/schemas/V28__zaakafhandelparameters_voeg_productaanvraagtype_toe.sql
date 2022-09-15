/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
ALTER TABLE ${schema}.zaakafhandelparameters
    ADD COLUMN productaanvraagtype VARCHAR;

CREATE INDEX idx_zaakafhandelparameters_productaanvraagtype ON ${schema}.zaakafhandelparameters USING btree (productaanvraagtype);
