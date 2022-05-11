/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Taal} from '../../configuratie/model/taal';

export class EnkelvoudigInformatieObjectVersieGegevens {
    uuid: string;
    titel: string;
    beschrijving: string;
    vertrouwelijkheidaanduiding: string;
    auteur: string;
    status: string;
    taal: Taal;
    bestandsnaam: string;
    verzenddatum: string;
    toelichting: string;
}
