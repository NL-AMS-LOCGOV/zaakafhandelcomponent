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
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {Zaaktype} from '../model/zaaktype';
import {IdentityService} from '../../identity/identity.service';
import {Groep} from '../../identity/model/groep';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ZaakRechten} from '../model/zaak-rechten';

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

    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;

    expandedRow: ZaakOverzicht | null;

    groepen: Groep[] = [];
    zaakTypes: Zaaktype[] = [];

    get zaakRechten(): typeof ZaakRechten {
        return ZaakRechten;
    }

    constructor(private zakenService: ZakenService, private titleService: Title, public utilService: UtilService, private identityService: IdentityService, private snackbar: MatSnackBar) {
    }

    ngOnInit() {
        this.titleService.setTitle('Werkvoorraad zaken');
        this.utilService.setHeaderTitle('Werkvoorraad zaken');
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
            new TableColumn('zaaknummer', 'identificatie', true),
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
    }

    toekennenAanIngelogdeMedewerker(zaakOverzicht: ZaakOverzicht, $event) {
        $event.stopPropagation();

        this.zakenService.toekennenAanIngelogdeMedewerkerVanuitLijst(zaakOverzicht).subscribe(zaak => {
            zaakOverzicht.rechten = zaak.rechten;
            // TODO de vraagtekens zijn overbodig als de Behandelaar weer gevuld wordt in de RESTOverzichtConverter
            zaakOverzicht['behandelaar.naam'] = zaak.behandelaar?.naam;
            this.snackbar.open(`Zaak toegekend aan ${zaak.behandelaar?.naam}`, 'Sluit', {
                duration: 3000
            });
        });
    }
}
