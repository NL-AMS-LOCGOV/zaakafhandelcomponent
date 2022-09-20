/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';

import {Besluit} from '../model/besluit';
import {ZakenService} from '../zaken.service';
import {map, shareReplay} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';

@Component({
    selector: 'zac-besluit-view',
    templateUrl: './besluit-view.component.html',
    styleUrls: ['./besluit-view.component.less']
})
export class BesluitViewComponent implements OnInit {
    @Input() besluit: Besluit;

    besluitInformatieobjecten: AbstractFormField;

    constructor(private zakenService: ZakenService) {
    }

    ngOnInit(): void {
        const besluitUuid = this.besluit.url.split('/').pop();
        const besluitInformatieobjectenList = this.zakenService.listBesluitInformatieobjecten(besluitUuid).pipe(
            shareReplay()
        );
        this.besluitInformatieobjecten = new DocumentenLijstFieldBuilder().id('documenten')
                                                                          .label('documenten')
                                                                          .documenten(besluitInformatieobjectenList)
                                                                          .verbergStatus()
                                                                          .readonly(true)
                                                                          .build();

    }
}
