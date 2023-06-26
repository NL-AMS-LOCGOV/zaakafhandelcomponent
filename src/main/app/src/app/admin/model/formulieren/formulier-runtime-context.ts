/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaak} from '../../../zaken/model/zaak';
import {Taak} from '../../../taken/model/taak';

export class FormulierRuntimeContext {
    formulierSysteemnaam: string;
    zaakUUID: string;
    taakID: string;
    zaak: Zaak;
    taak: Taak;
}
