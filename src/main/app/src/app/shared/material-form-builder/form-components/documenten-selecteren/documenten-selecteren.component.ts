/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {DocumentenSelecterenFormField} from './documenten-selecteren-form-field';
import {map, startWith} from 'rxjs/operators';
import {MatChipList} from '@angular/material/chips';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {Observable} from 'rxjs';
import {FormControl} from '@angular/forms';
import {DatumPipe} from '../../../pipes/datum.pipe';

@Component({
    templateUrl: './documenten-selecteren.component.html',
    styleUrls: ['./documenten-selecteren.component.less']
})
export class DocumentenSelecterenComponent implements OnInit, IFormComponent {

    documenten: EnkelvoudigInformatieobject[];
    data: DocumentenSelecterenFormField;
    filteredDocumenten: Observable<EnkelvoudigInformatieobject[]>;
    selectedDocumenten: EnkelvoudigInformatieobject[] = new Array<EnkelvoudigInformatieobject>();
    searchControl = new FormControl();
    formControl: FormControl;
    @ViewChild('chipList') chipList: MatChipList;
    @ViewChild('searchInput') inputElementElementRef: ElementRef<HTMLInputElement>;
    datumPipe = new DatumPipe('nl');

    constructor() {
    }

    ngOnInit(): void {
        this.formControl = this.data.formControl;
        this.data.documentenObservable.subscribe((data) => {
            this.documenten = data;
            this.filteredDocumenten = this.searchControl.valueChanges.pipe(
                startWith(null),
                map((value: string | null) => (value ? this.filter(value) : this.documenten.slice()))
            );
            this.formControl.statusChanges.subscribe(status => this.chipList.errorState = (status === 'INVALID'));
        });
    }

    optionClicked($event: MouseEvent, informatieobject: EnkelvoudigInformatieobject): void {
        $event.stopPropagation();
        this.toggleSelection(informatieobject);
    }

    toggleSelection(informatieobject: EnkelvoudigInformatieobject): void {
        informatieobject['selected'] = !informatieobject['selected'];
        if (informatieobject['selected']) {
            this.selectedDocumenten.push(informatieobject);
        } else {
            const i = this.selectedDocumenten.findIndex(
                (value) =>
                    value.uuid === informatieobject.uuid
            );
            this.selectedDocumenten.splice(i, 1);
        }
        this.formControl.setValue(this.selectedDocumenten.map(value => value.uuid).join(';'));
    }

    filter(filter: string): EnkelvoudigInformatieobject[] {
        if (filter) {
            const lowerCaseFilter = filter.toLowerCase();
            return this.documenten.filter((infoObject) => {
                const searchString = [infoObject.auteur, infoObject.titel, infoObject.documentType,
                    this.datumPipe.transform(infoObject.creatiedatum)].join(' ').toLowerCase();
                return searchString.indexOf(lowerCaseFilter) > 0;
            });
        } else {
            return this.documenten.slice();
        }
    }

    remove(informatieobject: EnkelvoudigInformatieobject): void {
        this.toggleSelection(informatieobject);
    }

    blur(): void {
        const enkelvoudigInformatieobjects: EnkelvoudigInformatieobject[] = this.filter(this.inputElementElementRef.nativeElement.value);
        if (enkelvoudigInformatieobjects.length === 0) {
            this.resetInput();
        }
    }

    resetInput(): void {
        this.inputElementElementRef.nativeElement.value = '';
        this.searchControl.setValue(null);
    }
}
