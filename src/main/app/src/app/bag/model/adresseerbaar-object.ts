/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {BAGObject} from './bagobject';

export class AdresseerbaarObject extends BAGObject {
    status: string;
    typeAdresseerbaarObject: 'LIGPLAATS' | 'STANDPLAATS' | 'VERBLIJFSOBJECT';
    vboDoel: string;
    vboOppervlakte: number;
}
