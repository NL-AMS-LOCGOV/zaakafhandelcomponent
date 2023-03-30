/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {BAGService} from '../../bag.service';
import {ListAdressenParameters} from '../../model/list-adressen-parameters';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../../shared/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {UtilService} from '../../../core/service/util.service';
import {BAGObject} from '../../model/bagobject';
import {BAGObjecttype} from '../../model/bagobjecttype';
import {Adres} from '../../model/adres';

@Component({
    selector: 'zac-bag-zoek',
    templateUrl: './bag-zoek.component.html',
    styleUrls: ['./bag-zoek.component.less']
})
export class BagZoekComponent implements OnInit {

    @Output() bagObject = new EventEmitter<BAGObject>();
    @Input() gekoppeldeBagObjecten: BAGObject[];
    BAGObjecttype = BAGObjecttype;
    trefwoorden = new FormControl('', [Validators.required, Validators.maxLength(255)]);

    @ViewChild(MatTable) table: MatTable<BAGObject>;
    bagObjecten: MatTableDataSource<BAGObject> = new MatTableDataSource<BAGObject>();
    loading = false;
    formGroup: FormGroup;
    columns: string[] = ['expand', 'id', 'type', 'omschrijving', 'acties'];

    constructor(private bagService: BAGService, private utilService: UtilService, private dialog: MatDialog) { }

    ngOnInit(): void {}

    zoek(): void {
        this.loading = true;
        this.utilService.setLoading(true);
        this.bagObjecten.data = [];
        this.bagService.listAdressen(
            new ListAdressenParameters(this.trefwoorden.value))
            .subscribe(adressen => {
                this.bagObjecten.data = adressen.resultaten;
                this.loading = false;
                this.utilService.setLoading(false);
            });
    }

    selectBagObject(bagObject: BAGObject): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData('msg.bagobject.koppelen.bevestigen')
        }).afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.gekoppeldeBagObjecten.push(bagObject);
                this.bagObject.emit(bagObject);
            }
        });
    }

    expandable(bagObject: BAGObject) {
        if (bagObject.bagObjectType === BAGObjecttype.ADRES) {
            const adres: Adres = bagObject as Adres;
            return adres.openbareRuimte || adres.nummeraanduiding || adres.woonplaats || adres.panden?.length;
        }
        return false;
    }

    expand(bagObject) {
        this.bagObjecten.data = this.bagObjecten.data.filter(b => b['child'] !== true);
        if (bagObject.expanded) {
            bagObject.expanded = false;
            return;
        }

        this.bagObjecten.data.forEach(b => b['expanded'] = false);
        bagObject.expanded = true;

        const childeren: BAGObject[] = [];
        if (bagObject.bagObjectType === BAGObjecttype.ADRES) {
            const adres: Adres = bagObject as Adres;
            if (adres.nummeraanduiding) {
                childeren.push(adres.nummeraanduiding);
            }
            if (adres.openbareRuimte) {
                childeren.push(adres.openbareRuimte);
            }
            if (adres.woonplaats) {
                childeren.push(adres.woonplaats);
            }
            if (adres.panden?.length) {
                adres.panden.forEach(p => childeren.push(p));
            }
        }
        childeren.forEach(d => d['child'] = true);
        this.bagObjecten.data.splice(this.bagObjecten.data.indexOf(bagObject) + 1, 0, ...childeren);
        this.table.renderRows();
    }

    reedsGekoppeld(row: BAGObject): boolean {
        if (this.gekoppeldeBagObjecten?.length) {
            return this.gekoppeldeBagObjecten.some(b => b.identificatie === row.identificatie && b.bagObjectType === row.bagObjectType);
        }
        return false;
    }
}