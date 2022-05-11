/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.zaakafhandelparameters
    ADD COLUMN eindatum_gepland_waarschuwing               INTEGER,
    ADD COLUMN uiterlijke_einddatum_afdoening_waarschuwing INTEGER;
