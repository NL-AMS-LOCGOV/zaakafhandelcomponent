/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Resultaat} from '../../shared/model/resultaat';
import {FilterVeld} from './filter-veld';

export class ZoekResultaat<TYPE> extends Resultaat<TYPE> {
    filters: Partial<Record<FilterVeld, string[]>> = {};
}
