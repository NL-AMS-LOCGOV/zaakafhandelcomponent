/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormulierVeldDefinitie} from './formulier-veld-definitie';

export class FormulierDefinitie {
    id: FormulierDefinitieID;
    veldDefinities: FormulierVeldDefinitie[];
}

export type FormulierDefinitieID =
    'DEFAULT_TAAKFORMULIER' |
    'AANVULLENDE_INFORMATIE' |
    'ADVIES' |
    'EXTERN_ADVIES_VASTLEGGEN' |
    'EXTERN_ADVIES_MAIL' |
    'GOEDKEUREN' |
    'DOCUMENT_VERZENDEN_POST';
