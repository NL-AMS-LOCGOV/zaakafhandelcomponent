/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FileFormat} from './file-format';
import {User} from '../../identity/model/user';
import {EnkelvoudigInformatieobjectActies} from './enkelvoudig-informatieobject-acties';
import {Ondertekening} from './ondertekening';

export class EnkelvoudigInformatieobject {
    uuid: string;
    identificatie: string;
    titel: string;
    beschrijving: string;
    creatiedatum: string;
    registratiedatumTijd: string;
    ontvangstdatum: string;
    verzenddatum: string;
    bronorganisatie: string;
    vertrouwelijkheidaanduiding: string;
    auteur: string;
    status: string;
    formaat: FileFormat;
    taal: string;
    versie: number;
    informatieobjectTypeUUID: string;
    informatieobjectTypeOmschrijving: string;
    bestandsnaam: string;
    bestandsomvang: number;
    link: string;
    indicatieGebruiksrecht: boolean;
    gelockedDoor: User;
    acties: EnkelvoudigInformatieobjectActies;
    ondertekening: Ondertekening|null;
}
