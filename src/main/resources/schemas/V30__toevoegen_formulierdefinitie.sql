/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.humantask_parameters
    ADD COLUMN id_formulier_definition VARCHAR;

CREATE INDEX idx_humantask_parameters_id_formulier_definition ON ${schema}.humantask_parameters USING btree (id_formulier_definition);
