/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE IF EXISTS ${schema}.planitem_parameters
    RENAME COLUMN id_planitem_parameters TO id_humantask_parameters;

ALTER TABLE IF EXISTS ${schema}.planitem_parameters
    RENAME CONSTRAINT fk_planitem_parameters_zaakafhandelparameters TO fk_humantask_parameters_zaakafhandelparameters;

ALTER TABLE IF EXISTS ${schema}.planitem_parameters
    RENAME CONSTRAINT pk_planitem_parameters to pk_humantask_parameters;

ALTER SEQUENCE IF EXISTS ${schema}.sq_planitem_parameters
    RENAME TO sq_humantask_parameters;

ALTER INDEX IF EXISTS idx_planitem_parameters_id_zaakafhandelparameters
    RENAME TO idx_humantask_parameters_id_zaakafhandelparameters;

ALTER INDEX IF EXISTS pk_planitem_parameters
    RENAME TO pk_humantask_parameters;

ALTER TABLE IF EXISTS ${schema}.planitem_parameters
    RENAME TO humantask_parameters;
