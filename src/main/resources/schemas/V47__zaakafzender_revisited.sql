/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

DROP INDEX ${schema}.idx_zaakafzender_mail;

ALTER TABLE ${schema}.zaakafzender
    ALTER COLUMN mail SET DATA TYPE VARCHAR;

CREATE INDEX idx_zaakafzender_mail ON ${schema}.zaakafzender
    USING btree (mail);
