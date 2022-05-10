/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

alter table ${schema}.humantask_parameters
    alter column doorlooptijd DROP NOT NULL;