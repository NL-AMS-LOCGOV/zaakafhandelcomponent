/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {DocumentenLijstFormField} from './documenten-lijst-form-field';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {SelectionModel} from '@angular/cdk/collections';
import {MatTableDataSource} from '@angular/material/table';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {DatumPipe} from '../../../pipes/datum.pipe';
import {InformatieObjectenService} from '../../../../informatie-objecten/informatie-objecten.service';

@Component({
    templateUrl: './documenten-lijst.component.html',
    styleUrls: ['./documenten-lijst.component.less']
})
export class DocumentenLijstComponent implements OnInit, IFormComponent {

    data: DocumentenLijstFormField;
    columns: string[] = ['select', 'titel', 'documentType', 'status', 'versie', 'auteur', 'creatiedatum', 'bestandsomvang', 'url'];
    selection = new SelectionModel<EnkelvoudigInformatieobject>(true, []);
    dataSource: MatTableDataSource<EnkelvoudigInformatieobject>;
    datumPipe = new DatumPipe('nl');

    constructor(private informatieObjectenService: InformatieObjectenService) {
    }

    ngOnInit(): void {
        if (this.data.readonly) {
            this.columns.splice(this.columns.indexOf('select'), 1);
        }
        this.data.documenten$.subscribe(documenten => {
            for (const document of documenten) {
                document.creatiedatum = this.datumPipe.transform(document.creatiedatum); // nodig voor zoeken
                document['viewLink'] = `/informatie-objecten/${document.uuid}`;
                document['downloadLink'] = this.informatieObjectenService.getDownloadURL(document.uuid);
            }
            this.data.formControl.setValue(documenten.map(v => v.uuid).join(';'));
            this.dataSource = new MatTableDataSource(documenten);
        });
    }

    applyFilter($event: KeyboardEvent): void {
        const filterValue = ($event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();
    }

    change($event: MatCheckboxChange, document): void {
        if ($event) {
            this.selection.toggle(document);
            this.data.formControl.setValue(this.selection.selected.map(value => value.uuid).join(';'));
        }
    }
}
