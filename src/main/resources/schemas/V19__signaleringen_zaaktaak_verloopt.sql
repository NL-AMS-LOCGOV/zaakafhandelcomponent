/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

INSERT INTO ${schema}.signaleringtype(signaleringtype_enum, subjecttype_enum)
VALUES ('ZAAK_VERLOPEND', 'ZAAK');

INSERT INTO ${schema}.signaleringtype(signaleringtype_enum, subjecttype_enum)
VALUES ('TAAK_VERLOPEN', 'TAAK');
