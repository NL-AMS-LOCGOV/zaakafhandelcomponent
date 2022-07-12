/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Resultaat} from '../../shared/model/resultaat';

export class ZoekResultaat<TYPE> extends Resultaat<TYPE> {
    filters: { [key: string]: string[] };
}
