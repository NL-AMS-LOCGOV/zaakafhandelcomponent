/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
ALTER TABLE ${schema}.referentie_waarde
    ADD COLUMN volorde INT NOT NULL DEFAULT (0);
CREATE INDEX ix_referentie_waarde_volgorde ON ${schema}.referentie_waarde USING btree (volgorde);
