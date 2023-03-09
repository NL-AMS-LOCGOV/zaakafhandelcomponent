/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, DoCheck, OnInit} from '@angular/core';
import {InformatieObjectenService} from '../../../../informatie-objecten/informatie-objecten.service';
import {TranslateService} from '@ngx-translate/core';
import {DocumentSelectComponent} from '../document-select/document-select.component';
import {DocumentOndertekenenFormField} from './document-ondertekenen-form-field';

@Component({
    templateUrl: '../document-select/document-select.component.html',
    styleUrls: ['../document-select/document-select.component.less']
})
export class DocumentOndertekenenComponent extends DocumentSelectComponent implements OnInit, DoCheck {

    data: DocumentOndertekenenFormField;

    constructor(public translate: TranslateService, public informatieObjectenService: InformatieObjectenService) {
        super(translate, informatieObjectenService);
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    ngDoCheck(): void {
        super.ngDoCheck();
    }

    toonFilterVeld(): boolean {
        return false;
    }

    selectDisabled(document): boolean {
        return this.data.readonly || !document.rechten.ondertekenen || document.ondertekening;
    }

    isSelected(document): boolean {
        return this.selection.isSelected(document) || document.ondertekening;
    }
}
