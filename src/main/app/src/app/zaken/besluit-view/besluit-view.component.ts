/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';

import {Besluit} from '../model/besluit';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {DocumentenLijstFormField} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-form-field';
import {of} from 'rxjs';
import {MatTableDataSource} from '@angular/material/table';
import {HistorieRegel} from '../../shared/historie/model/historie-regel';
import {ZakenService} from '../zaken.service';

@Component({
    selector: 'zac-besluit-view',
    templateUrl: './besluit-view.component.html',
    styleUrls: ['./besluit-view.component.less']
})
export class BesluitViewComponent implements OnInit, OnChanges {
    @Input() besluit: Besluit;
    @Output() besluitWijzigen = new EventEmitter<void>();
    historie: MatTableDataSource<HistorieRegel> = new MatTableDataSource<HistorieRegel>();

    besluitInformatieobjecten: DocumentenLijstFormField;

    constructor(private zakenService: ZakenService) {}

    ngOnInit(): void {
        this.besluitInformatieobjecten = new DocumentenLijstFieldBuilder().id('documenten')
                                                                          .label('documenten')
                                                                          .documenten(of(this.besluit.informatieobjecten))
                                                                          .verbergStatus()
                                                                          .readonly(true)
                                                                          .build();
        this.loadHistorie();
    }

    ngOnChanges() {
        if (this.besluitInformatieobjecten) {
            this.besluitInformatieobjecten.documentenChanged.emit(of(this.besluit.informatieobjecten));
        }
    }

    private loadHistorie(): void {
        this.zakenService.listBesluitHistorie(this.besluit.uuid).subscribe(historie => {
            this.historie.data = historie;
        });
    }

}
