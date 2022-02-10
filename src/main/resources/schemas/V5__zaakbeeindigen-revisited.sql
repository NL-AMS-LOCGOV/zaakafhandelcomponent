/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

DROP INDEX ${schema}.ix_beeindigparameter_resultaattype;

ALTER TABLE ${schema}.zaakbeeindigparameter
    DROP COLUMN resultaattype_url,
    ADD COLUMN resultaattype_uuid UUID NOT NULL;

CREATE INDEX ix_beeindigparameter_resultaattype ON ${schema}.zaakbeeindigparameter USING btree (resultaattype_uuid);
