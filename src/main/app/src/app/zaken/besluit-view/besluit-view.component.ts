/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';

import {Besluit} from '../model/besluit';
import {of} from 'rxjs';
import {MatTableDataSource} from '@angular/material/table';
import {HistorieRegel} from '../../shared/historie/model/historie-regel';
import {ZakenService} from '../zaken.service';
import {DocumentSelectFormField} from '../../shared/material-form-builder/form-components/document-select/document-select-form-field';
import {DocumentSelectFieldBuilder} from '../../shared/material-form-builder/form-components/document-select/document-select-field-builder';

@Component({
    selector: 'zac-besluit-view',
    templateUrl: './besluit-view.component.html',
    styleUrls: ['./besluit-view.component.less']
})
export class BesluitViewComponent implements OnInit, OnChanges {
    @Input() besluiten: Besluit[];
    @Input() readonly: boolean;
    @Output() besluitWijzigen = new EventEmitter<Besluit>();
    histories: Record<string, MatTableDataSource<HistorieRegel>> = {};

    besluitInformatieobjecten: Record<string, DocumentSelectFormField> = {};

    constructor(private zakenService: ZakenService) {}

    ngOnInit(): void {
        if (this.besluiten.length > 0) {
            this.loadBesluitData(this.besluiten[0].uuid);
        }
    }

    ngOnChanges() {
        for (const key in this.besluitInformatieobjecten) {
            if (this.besluitInformatieobjecten.hasOwnProperty(key)) {
                this.besluitInformatieobjecten[key].updateDocumenten(of(this.getBesluit(key).informatieobjecten));
            }
        }

        for (const historieKey in this.histories) {
            if (this.histories.hasOwnProperty(historieKey)) {
                this.loadHistorie(historieKey);
            }
        }
    }

    loadBesluitData(uuid) {
        if (!this.histories[uuid]) {
            this.loadHistorie(uuid);
        }

        if (!this.besluitInformatieobjecten[uuid]) {
            const besluit = this.getBesluit(uuid);
            this.besluitInformatieobjecten[uuid] = new DocumentSelectFieldBuilder().id('documenten')
                                                                                   .label('documenten')
                                                                                   .documenten(of(besluit.informatieobjecten))
                                                                                   .removeColumn('status')
                                                                                   .readonly(true)
                                                                                   .build();
        }
    }

    private loadHistorie(uuid) {
        this.zakenService.listBesluitHistorie(uuid).subscribe(historie => {
            this.histories[uuid] = new MatTableDataSource<HistorieRegel>();
            this.histories[uuid].data = historie;
        });
    }

    private getBesluit(uuid: string): Besluit {
        return this.besluiten.find(value => value.uuid === uuid);
    }

}
