/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {DocumentZoekObject} from '../../zoeken/model/documenten/document-zoek-object';
import {StaticTextComponent} from '../static-text/static-text.component';

@Component({
    selector: 'zac-zoekobject-static-header',
    templateUrl: './zoekobject-static-header.component.html',
    styleUrls: ['./zoekobject-static-header.component.less']
})
export class ZoekobjectStaticHeaderComponent extends StaticTextComponent {

    @Input() zaakZoekObject: ZaakZoekObject;
    @Input() documentZoekObject: DocumentZoekObject;

    constructor() {
        super();
    }
}
