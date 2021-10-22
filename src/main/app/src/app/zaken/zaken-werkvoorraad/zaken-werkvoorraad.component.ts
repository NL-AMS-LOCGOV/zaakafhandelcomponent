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
import {TableColumnFilter} from '../../shared/dynamic-table/filter/table-column-filter';
import {Zaaktype} from '../model/zaaktype';
import {IdentityService} from '../../identity/identity.service';
import {Groep} from '../../identity/model/groep';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Medewerker} from '../../identity/model/medewerker';

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
    geselecteerdeGroep: Groep;
    geselecteerdeZaaktype: Zaaktype;
    selectie: string = '';
    selecties = ['Groep', 'Zaaktype'];

    ingelogdeMedewerker: Medewerker;

    constructor(private zakenService: ZakenService, private titleService: Title, public utilService: UtilService, private identityService: IdentityService, private snackbar: MatSnackBar) {
    }

    ngOnInit() {
        this.titleService.setTitle('Werkvoorraad zaken');
        this.utilService.setHeaderTitle('Werkvoorraad zaken');
        this.dataSource = new ZakenWerkvoorraadDatasource(this.zakenService, this.utilService);
        this.zakenService.getZaaktypes().subscribe(zaaktypes => {
            this.zaakTypes = zaaktypes;
            const zaaktypeColumn: TableColumn = new TableColumn('zaaktype', 'zaaktype', true);
            zaaktypeColumn.filter = new TableColumnFilter<Zaaktype>('zaaktype', zaaktypes, 'omschrijving', 'identificatie');

            const startdatum: TableColumn = new TableColumn('startdatum', 'startdatum', true, 'startdatum');
            startdatum.pipe = DatumPipe;

            const einddatumGepland: TableColumn = new TableColumn('streefdatum', 'einddatumGepland');
            einddatumGepland.pipe = DatumPipe;

            const uiterlijkedatumafdoening: TableColumn = new TableColumn('fataledatum', 'uiterlijkeDatumAfdoening');
            uiterlijkedatumafdoening.pipe = DatumPipe;

            this.dataSource.columns = [
                new TableColumn('zaaknummer', 'identificatie', true),
                new TableColumn('status', 'status', true),
                zaaktypeColumn,
                new TableColumn('groep', 'groep.naam', false),
                startdatum,
                einddatumGepland,
                new TableColumn('aanvrager', 'aanvrager', true),
                new TableColumn('behandelaar', 'behandelaar.naam', true),
                uiterlijkedatumafdoening,
                new TableColumn('toelichting', 'toelichting'),
                new TableColumn('url', 'url', true, null, true)
            ];
        });
        this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });

        this.groepenOphalen();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    groepenOphalen() {
        this.identityService.getGroepen().subscribe(response => {
            this.groepen = response;
        });
    }

    zakenVanGroepOphalen() {
        // Kolom Zaaktype moet zichtbaar worden
        this.dataSource.columns.find(kolom => kolom.label === 'groep').visible = false;
        this.dataSource.columns.find(kolom => kolom.label === 'zaaktype').visible = true;

        // Filter van zaaktype weggooien, omdat we alleen willen zoeken op groepId
        this.dataSource.removeFilter('zaaktype');
        this.dataSource.setFilter('groepId', this.geselecteerdeGroep?.id);
    }

    zakenVanZaaktypeOphalen() {
        // Kolom Groep moet zichtbaar worden
        this.dataSource.columns.find(kolom => kolom.label === 'groep').visible = true;
        this.dataSource.columns.find(kolom => kolom.label === 'zaaktype').visible = false;

        // Filter van groepId weggooien, omdat we alleen willen zoeken op zaaktype
        this.dataSource.removeFilter('groepId');
        this.dataSource.setFilter('zaaktype', this.geselecteerdeZaaktype?.identificatie);
    }

    toekennenAanIngelogdeMedewerker(zaakOverzicht: ZaakOverzicht, event) {
        event.stopPropagation();

        this.zakenService.toekennenAanIngelogdeMedewerkerVanuitLijst(zaakOverzicht).subscribe(zaak => {
            zaakOverzicht['behandelaar.naam'] = zaak.behandelaar.naam;
            this.snackbar.open(`Taak toegekend aan ${zaak.behandelaar.naam}`, "Sluit", {
                duration: 3000,
            });
        });
    }
}
