/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

ALTER TABLE ${schema}.signalering
    ADD COLUMN detail VARCHAR NULL;

CREATE INDEX ix_signalering_detail ON ${schema}.signalering
    USING btree (detail);

ALTER TABLE ${schema}.signalering_verzonden
    RENAME COLUMN subjectfield_enum TO detail;

ALTER TABLE ${schema}.signalering_verzonden
    ALTER COLUMN detail DROP NOT NULL;

ALTER INDEX ${schema}.ix_signalering_verzonden_subjectfield
    RENAME TO ix_signalering_verzonden_detail;
