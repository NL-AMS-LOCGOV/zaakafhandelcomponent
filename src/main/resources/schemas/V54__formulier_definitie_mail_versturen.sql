/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.formulier_definitie
    ADD COLUMN mail_versturen BOOLEAN,
    ADD COLUMN mail_to VARCHAR,
    ADD COLUMN mail_from VARCHAR,
    ADD COLUMN mail_subject VARCHAR,
    ADD COLUMN mail_body VARCHAR,
    ADD COLUMN mail_cc VARCHAR,
    ADD COLUMN mail_bcc VARCHAR;
