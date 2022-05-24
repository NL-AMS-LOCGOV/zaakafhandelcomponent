/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.zaakafhandelparameters
    ADD COLUMN niet_ontvankelijk_resultaattype_uuid UUID NULL;
