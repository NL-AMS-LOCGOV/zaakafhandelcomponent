/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.humantask_parameters
    ADD COLUMN id_start_formulier_definition VARCHAR;

ALTER TABLE ${schema}.humantask_parameters
    ADD COLUMN id_afhandel_formulier_definition VARCHAR;

CREATE INDEX idx_humantask_parameters_id_start_formulier_definition
    ON ${schema}.humantask_parameters USING btree (id_start_formulier_definition);

CREATE INDEX idx_humantask_parameters_id_afhandel_formulier_definition
    ON ${schema}.humantask_parameters USING btree (id_afhandel_formulier_definition);
