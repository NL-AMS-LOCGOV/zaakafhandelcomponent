/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Taak} from '../model/taak';
import {MatButtonToggle} from '@angular/material/button-toggle';
import {TakenWerkvoorraadDatasource} from './taken-werkvoorraad-datasource';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {UtilService} from '../../core/service/util.service';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {TaakSortering} from '../model/taak-sortering';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {TaakRechten} from '../model/taak-rechten';
import {DatumOverschredenPipe} from '../../shared/pipes/datumOverschreden.pipe';
import {SelectionModel} from '@angular/cdk/collections';
import {WerkvoorraadVerdelenDialogComponent} from '../../shared/werkvoorraad-verdelen-dialog/werkvoorraad-verdelen-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
    templateUrl: './taken-werkvoorraad.component.html',
    styleUrls: ['./taken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class TakenWerkvoorraadComponent implements AfterViewInit, OnInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<Taak>;
    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;

    dataSource: TakenWerkvoorraadDatasource;
    expandedRow: Taak | null;
    selection = new SelectionModel<Taak>(true, []);

    get taakRechten(): typeof TaakRechten {
        return TaakRechten;
    }

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService, public dialog: MatDialog) {
    }

    ngOnInit() {
        this.utilService.setTitle('title.taken.werkvoorraad');
        this.dataSource = new TakenWerkvoorraadDatasource(this.takenService, this.utilService);
        this.setColumns();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }

    private setColumns() {
        const creatieDatum: TableColumn = new TableColumn('creatiedatumTijd', 'creatiedatumTijd', true,
            TaakSortering.CREATIEDATUM)
        .pipe(DatumPipe);

        const streefDatum: TableColumn = new TableColumn('streefdatum', 'streefdatum', true, TaakSortering.STREEFDATUM)
        .pipe(DatumOverschredenPipe);

        this.dataSource.columns = [
            new TableColumn('select', 'select', true, null, true),
            new TableColumn('naam', 'naam', true, TaakSortering.TAAKNAAM),
            new TableColumn('zaakIdentificatie', 'zaakIdentificatie', true),
            new TableColumn('zaaktypeOmschrijving', 'zaaktypeOmschrijving', true),
            creatieDatum,
            streefDatum,
            new TableColumn('groep', 'groep.naam', true),
            new TableColumn('behandelaar', 'behandelaar.naam', true, TaakSortering.BEHANDELAAR),
            new TableColumn('url', 'url', true, null, true)
        ];
    }

    toekennenAanIngelogdeMedewerker(taak: Taak, event) {
        event.stopPropagation();
        this.takenService.assignToLoggedOnUser(taak).subscribe(taakResponse => {
            taak['behandelaar.naam'] = taakResponse.behandelaar.naam;
            taak.rechten = taakResponse.rechten;
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taakResponse.behandelaar.naam});
        });
    }

    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected(): boolean {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataSource.data.length;
        return numSelected === numRows;
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
    checkboxLabel(row?: Taak): string {
        if (!row) {
            return `actie.alles.${this.isAllSelected() ? 'deselecteren' : 'selecteren'}`;
        }

        return `actie.${this.selection.isSelected(row) ? 'deselecteren' : 'selecteren'}`;
    }

    isSelected(): boolean {
        return this.selection.selected.length > 0;
    }

    countSelected(): number {
        return this.selection.selected.length;
    }

    openVerdelenScherm() {
        let taken = this.selection.selected;
        const dialogRef = this.dialog.open(WerkvoorraadVerdelenDialogComponent, {
            width: '300px',
            data: taken
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.verdeeld.taak');
                } else {
                    this.utilService.openSnackbar('msg.verdeeld.taken', {aantal: this.selection.selected.length});
                }
                this.zoekTaken();
            }
        });
    }

    private zoekTaken() {
        this.dataSource.load();
        this.setColumns();
        this.selection.clear();
    }
}
