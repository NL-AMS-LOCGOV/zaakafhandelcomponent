/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';

import {detailExpand} from '../../shared/animations/animations';

import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {UtilService} from '../../core/service/util.service';
import {SelectionModel} from '@angular/cdk/collections';
import {ZakenService} from '../zaken.service';
import {IdentityService} from '../../identity/identity.service';
import {MatDialog} from '@angular/material/dialog';
import {MatTable} from '@angular/material/table';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {User} from '../../identity/model/user';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';

import {ZakenVerdelenDialogComponent} from '../zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {ZakenVrijgevenDialogComponent} from '../zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component';
import {ZakenWerkvoorraadDatasource} from './zaken-werkvoorraad-datasource';
import {FilterVeld} from 'src/app/zoeken/model/filter-veld';
import {ZoekVeld} from 'src/app/zoeken/model/zoek-veld';
import {SorteerVeld} from 'src/app/zoeken/model/sorteer-veld';
import {DatumVeld} from 'src/app/zoeken/model/datum-veld';
import {PolicyService} from '../../policy/policy.service';
import {ZakenActies} from '../../policy/model/zaken-acties';

@Component({
    templateUrl: './zaken-werkvoorraad.component.html',
    styleUrls: ['./zaken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class ZakenWerkvoorraadComponent implements AfterViewInit, OnInit {

    selection = new SelectionModel<ZaakZoekObject>(true, []);
    dataSource: ZakenWerkvoorraadDatasource;
    acties = new ZakenActies();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakZoekObject>;
    ingelogdeMedewerker: User;
    expandedRow: ZaakZoekObject | null;
    ZoekVeld = ZoekVeld;
    SorteerVeld = SorteerVeld;
    FilterVeld = FilterVeld;
    DatumVeld = DatumVeld;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService, private zoekenService: ZoekenService, public utilService: UtilService,
                private identityService: IdentityService, public dialog: MatDialog, private policyService: PolicyService) {
        this.dataSource = new ZakenWerkvoorraadDatasource(this.zoekenService, this.utilService);
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.werkvoorraad');
        this.getIngelogdeMedewerker();
        this.dataSource.initColumns(this.defaultColumns());
        this.policyService.readZakenActies().subscribe(acties => this.acties = acties);
    }

    defaultColumns(): Map<string, ColumnPickerValue> {
        const columns = new Map([
            ['select', ColumnPickerValue.STICKY],
            ['zaak.identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['omschrijving', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['openstaandeTaken', ColumnPickerValue.HIDDEN],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['dagenTotStreefdatum', ColumnPickerValue.HIDDEN],
            ['behandelaar', ColumnPickerValue.VISIBLE],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['dagenTotFataledatum', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['url', ColumnPickerValue.STICKY]
        ]);
        if (!this.acties.verdelenEnVrijgeven) {
            columns.delete('select');
        }
        return columns;
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
        this.dataSource.load();
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataSource.data.length;
        return numSelected === numRows;
    }

    isSelected() {
        return this.selection.selected.length > 0;
    }

    countSelected() {
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
    checkboxLabel(row?: ZaakZoekObject): string {
        if (!row) {
            return `actie.alles.${this.isAllSelected() ? 'deselecteren' : 'selecteren'}`;
        }

        return `actie.${this.selection.isSelected(row) ? 'deselecteren' : 'selecteren'}`;
    }

    pageChange(): void {
        this.selection.clear();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    resetColumns(): void {
        this.dataSource.resetColumns();
    }

    filtersChange(): void {
        this.selection.clear();
        this.dataSource.filtersChanged();
    }

    assignToMe(zaakZoekObject: ZaakZoekObject, $event) {
        $event.stopPropagation();

        this.zakenService.toekennenAanIngelogdeMedewerkerVanuitLijst(zaakZoekObject).subscribe(zaak => {
            zaakZoekObject.behandelaarNaam = zaak.behandelaar.naam;
            zaakZoekObject.behandelaarGebruikersnaam = zaak.behandelaar.id;
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar.naam});
        });
    }

    showAssignToMe(zaakZoekObject: ZaakZoekObject): boolean {
        return this.acties.toekennenAanMijzelf && this.ingelogdeMedewerker && this.ingelogdeMedewerker.id !== zaakZoekObject.behandelaarGebruikersnaam;
    }

    openVerdelenScherm(): void {
        const zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVerdelenDialogComponent, {
            data: zaken
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.verdeeld.zaak');
                } else {
                    this.utilService.openSnackbar('msg.verdeeld.zaken', {aantal: this.selection.selected.length});
                }
                this.filtersChange();
            }
        });
    }

    openVrijgevenScherm(): void {
        const zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVrijgevenDialogComponent, {
            data: zaken
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.vrijgegeven.zaak');
                } else {
                    this.utilService.openSnackbar('msg.vrijgegeven.zaken', {aantal: this.selection.selected.length});
                }
                this.filtersChange();
            }
        });
    }
}
