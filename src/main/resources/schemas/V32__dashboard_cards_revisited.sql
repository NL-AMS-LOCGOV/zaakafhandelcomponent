/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.dashboard_card
    ADD COLUMN kolom INTEGER NULL;

UPDATE ${schema}.dashboard_card
SET kolom=0;

ALTER TABLE ${schema}.dashboard_card
    ALTER COLUMN kolom SET NOT NULL;
