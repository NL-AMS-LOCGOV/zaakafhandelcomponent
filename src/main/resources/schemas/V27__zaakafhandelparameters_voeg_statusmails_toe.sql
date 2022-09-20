/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.zaakafhandelparameters
    ADD COLUMN intake_mail   VARCHAR NOT NULL DEFAULT 'BESCHIKBAAR_UIT',
    ADD COLUMN afronden_mail VARCHAR NOT NULL DEFAULT 'BESCHIKBAAR_UIT';

UPDATE ${schema}.zaakafhandelparameters SET intake_mail='BESCHIKBAAR_UIT', afronden_mail='BESCHIKBAAR_UIT';

ALTER TABLE ${schema}.zaakafhandelparameters
    ALTER intake_mail   DROP DEFAULT,
    ALTER afronden_mail DROP DEFAULT;
