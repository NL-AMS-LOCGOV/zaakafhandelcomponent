/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, DoCheck, OnInit} from '@angular/core';
import {FormComponent} from '../../model/form-component';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {SelectionModel} from '@angular/cdk/collections';
import {MatTableDataSource} from '@angular/material/table';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {DatumPipe} from '../../../pipes/datum.pipe';
import {InformatieObjectenService} from '../../../../informatie-objecten/informatie-objecten.service';
import {TranslateService} from '@ngx-translate/core';
import {DocumentSelectFormField} from './document-select-form-field';
import {Observable} from 'rxjs';

@Component({
    templateUrl: './document-select.component.html',
    styleUrls: ['./document-select.component.less']
})
export class DocumentSelectComponent extends FormComponent implements OnInit, DoCheck {

    data: DocumentSelectFormField;
    documenten: Observable<EnkelvoudigInformatieobject[]>;
    selection = new SelectionModel<EnkelvoudigInformatieobject>(true, []);
    dataSource: MatTableDataSource<EnkelvoudigInformatieobject> = new MatTableDataSource<EnkelvoudigInformatieobject>();
    datumPipe = new DatumPipe('nl');
    loading = false;

    constructor(public translate: TranslateService, private informatieObjectenService: InformatieObjectenService) {
        super();
    }

    ngOnInit(): void {
        this.documenten = this.data.documenten;
        if (this.data.readonly) {
            this.data.removeColumn('select');
        }
        this.ophalenDocumenten();
    }

    ophalenDocumenten() {
        if (this.data.documenten) {
            this.loading = true;
            this.dataSource.data = [];
            this.data.documenten.subscribe(documenten => {
                this.selection.clear();
                for (const document of documenten) {
                    document.creatiedatum = this.datumPipe.transform(document.creatiedatum); // nodig voor zoeken
                    document['viewLink'] = `/informatie-objecten/${document.uuid}`;
                    document['downloadLink'] = this.informatieObjectenService.getDownloadURL(document.uuid);
                    if (this.data.documentenChecked?.includes(document.uuid)) {
                        this.selection.toggle(document);
                    }
                }
                this.dataSource.data = documenten;
                if (this.selection.selected.length > 0) {
                    this.data.formControl.setValue(this.selection.selected.map(value => value.uuid).join(';'));
                }
                this.loading = false;
            });
        }
    }

    applyFilter($event: KeyboardEvent): void {
        const filterValue = ($event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();
    }

    updateSelected($event: MatCheckboxChange, document): void {
        if ($event) {
            this.selection.toggle(document);
            this.data.formControl.setValue(this.selection.selected.map(value => value.uuid).join(';'));
        }
    }

    ngDoCheck(): void {
        if (this.data.documenten !== this.documenten) {
            this.documenten = this.data.documenten;
            this.ophalenDocumenten();
        }
    }
}
