/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';

export class Zaaktype {
    uuid: string;
    identificatie: string;
    doel: string;
    omschrijving: string;
    referentieproces: string;
    servicenorm: boolean;
    versiedatum: string;
    beginGeldigheid: string;
    eindeGeldigheid: string;
    nuGeldig: boolean;
    vertrouwelijkheidaanduiding: Vertrouwelijkheidaanduiding;
}
