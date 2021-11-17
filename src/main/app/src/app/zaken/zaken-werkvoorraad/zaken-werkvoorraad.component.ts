/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {ZakenWerkvoorraadDatasource} from './zaken-werkvoorraad-datasource';
import {MatButtonToggle} from '@angular/material/button-toggle';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {Zaaktype} from '../model/zaaktype';
import {IdentityService} from '../../identity/identity.service';
import {Groep} from '../../identity/model/groep';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {ZaakRechten} from '../model/zaak-rechten';
import {SelectionModel} from '@angular/cdk/collections';
import {ZakenVerdelenDialogComponent} from '../zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
    templateUrl: './zaken-werkvoorraad.component.html',
    styleUrls: ['./zaken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class ZakenWerkvoorraadComponent implements AfterViewInit, OnInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakOverzicht>;
    dataSource: ZakenWerkvoorraadDatasource;
    selection = new SelectionModel<ZaakOverzicht>(true, []);

    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;

    expandedRow: ZaakOverzicht | null;

    groepen: Groep[] = [];
    zaakTypes: Zaaktype[] = [];

    get zaakRechten(): typeof ZaakRechten {
        return ZaakRechten;
    }

    constructor(private zakenService: ZakenService, public utilService: UtilService, private identityService: IdentityService, public dialog: MatDialog) {
    }

    ngOnInit() {
        this.utilService.setTitle('title.zaken.werkvoorraad');
        this.dataSource = new ZakenWerkvoorraadDatasource(this.zakenService, this.utilService);
        this.setColumns();

        this.zaaktypesOphalen();
        this.groepenOphalen();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    private zaaktypesOphalen() {
        this.zakenService.getZaaktypes().subscribe(zaakTypes => {
            this.zaakTypes = zaakTypes;
        });
    }

    private groepenOphalen() {
        this.identityService.getGroepen().subscribe(groepen => {
            this.groepen = groepen;
        });
    }

    private setColumns() {
        const startdatum: TableColumn = new TableColumn('startdatum', 'startdatum', true, 'startdatum');
        startdatum.pipe = DatumPipe;

        const einddatumGepland: TableColumn = new TableColumn('einddatumGepland', 'einddatumGepland');
        einddatumGepland.pipe = DatumPipe;

        const uiterlijkeEinddatumAfdoening: TableColumn = new TableColumn('uiterlijkeEinddatumAfdoening',
            'uiterlijkeDatumAfdoening');
        uiterlijkeEinddatumAfdoening.pipe = DatumPipe;

        this.dataSource.columns = [
            new TableColumn('select', 'select', true, null, true),
            new TableColumn('zaak.identificatie', 'identificatie', true),
            new TableColumn('status', 'status', true),
            this.dataSource.zoekParameters.selectie === 'groep' ?
                new TableColumn('zaaktype', 'zaaktype', true) :
                new TableColumn('groep', 'groep.naam', true),
            startdatum,
            einddatumGepland,
            new TableColumn('aanvrager', 'aanvrager', true),
            new TableColumn('behandelaar', 'behandelaar.naam', true),
            uiterlijkeEinddatumAfdoening,
            new TableColumn('toelichting', 'toelichting'),
            new TableColumn('url', 'url', true, null, true)
        ];
    }

    zoekZaken() {
        this.dataSource.zoekZaken();
        this.setColumns();
        this.selection.clear();
    }

    toekennenAanIngelogdeMedewerker(zaakOverzicht: ZaakOverzicht, $event) {
        $event.stopPropagation();

        this.zakenService.toekennenAanIngelogdeMedewerkerVanuitLijst(zaakOverzicht).subscribe(zaak => {
            zaakOverzicht.rechten = zaak.rechten;
            zaakOverzicht['behandelaar.naam'] = zaak.behandelaar.naam;
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar.naam});
        });
    }

    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataSource.data.length;
        return numSelected === numRows;
    }

    isSelected(){
        return this.selection.selected.length > 0
    }

    countSelected(){
        return this.selection.selected.length;
    }

    /** Selects all rows if they are not all selected; otherwise clear selection. */
    masterToggle() {
        if (this.isAllSelected()) {
            this.selection.clear();
            return;
        }

        this.selection.select(...this.dataSource.data);
    }

    /** The label for the checkbox on the passed row */
    checkboxLabel(row?: ZaakOverzicht): string {
        if (!row) {
            return `${this.isAllSelected() ? 'deselecteer' : 'selecteer'}.alles`;
        }

        return `${this.selection.isSelected(row) ? 'deselecteer' : 'selecteer'}`;
    }

    pageChange(): void {
        this.selection.clear();
    }

    openVerdelenScherm(): void {
        let zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVerdelenDialogComponent, {
            width: '300px',
            data: zaken
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.zoekZaken();
            }
        });
    }



}
