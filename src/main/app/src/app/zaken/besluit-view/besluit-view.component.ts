/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

import {Besluit} from '../model/besluit';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {DocumentenLijstFormField} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-form-field';
import {of} from 'rxjs';

@Component({
    selector: 'zac-besluit-view',
    templateUrl: './besluit-view.component.html',
    styleUrls: ['./besluit-view.component.less']
})
export class BesluitViewComponent implements OnInit {
    @Input() besluit: Besluit;
    @Output() besluitWijzigen = new EventEmitter<void>();

    besluitInformatieobjecten: DocumentenLijstFormField;

    constructor() {}

    ngOnInit(): void {
        const besluitUuid = this.besluit.url.split('/').pop();

        this.besluitInformatieobjecten = new DocumentenLijstFieldBuilder().id('documenten')
                                                                          .label('documenten')
                                                                          .documenten(of(this.besluit.informatieobjecten))
                                                                          .verbergStatus()
                                                                          .readonly(true)
                                                                          .build();
    }
}
