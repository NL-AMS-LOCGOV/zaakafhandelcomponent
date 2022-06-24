/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.zoek_index
    ALTER COLUMN uuid SET DATA TYPE VARCHAR;
ALTER TABLE ${schema}.zoek_index
    RENAME COLUMN uuid TO object_id;
ALTER TABLE ${schema}.zoek_index
    RENAME CONSTRAINT un_uuid TO zoek_index_un_object_id;
