/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaak} from './zaak';
import {InboxProductaanvraag} from '../../productaanvragen/model/inbox-productaanvraag';

export class ZaakAanmaakGegevens {
    constructor(public zaak: Zaak, public inboxProductaanvraag?: InboxProductaanvraag) {}
}
