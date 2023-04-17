/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.inbox_productaanvraag
    ADD COLUMN aantal_bijlagen INT NOT NULL DEFAULT (0);

CREATE INDEX ix_inbox_productaanvraag_aantal_bijlagen
    ON ${schema}.inbox_productaanvraag USING btree (aantal_bijlagen);
