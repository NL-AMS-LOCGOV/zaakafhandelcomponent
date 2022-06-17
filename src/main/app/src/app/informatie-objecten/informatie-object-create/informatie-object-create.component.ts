/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDrawer} from '@angular/material/sidenav';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup, Validators} from '@angular/forms';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {DocumentCreatieGegevens} from '../model/document-creatie-gegevens';
import {UtilService} from '../../core/service/util.service';

@Component({
    selector: 'zac-informatie-object-create',
    templateUrl: './informatie-object-create.component.html',
    styleUrls: ['./informatie-object-create.component.less']
})
export class InformatieObjectCreateComponent implements OnInit {

    @Input() zaakUUID: string;
    @Input() taskId: string;
    @Input() sideNav: MatDrawer;
    @Output() redirectUrl = new EventEmitter<string>();
    @Output() melding = new EventEmitter<string>();

    fields: Array<AbstractFormField[]>;
    formConfig: FormConfig;

    constructor(private informatieObjectenService: InformatieObjectenService, private utilService: UtilService) { }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.maken').cancelText('actie.annuleren').build();

        const titel = new InputFormFieldBuilder().id('titel').label('titel')
                                                 .maxlength(100)
                                                 .build();

        const informatieobjectType = new SelectFormFieldBuilder().id('informatieobjectTypeUUID')
                                                                 .label('informatieobjectType')
                                                                 .options(this.informatieObjectenService.listInformatieobjecttypesForZaak(this.zaakUUID))
                                                                 .optionLabel('omschrijving')
                                                                 .validators(Validators.required)
                                                                 .build();

        this.fields = [[informatieobjectType], [titel]];
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const documentCreatieGegeven = new DocumentCreatieGegevens();
            documentCreatieGegeven.zaakUUID = this.zaakUUID;
            documentCreatieGegeven.taskId = this.taskId;
            documentCreatieGegeven.informatieobjecttypeUUID = formGroup.controls['informatieobjectTypeUUID'].value.uuid;
            documentCreatieGegeven.titel = formGroup.controls['titel'].value;
            this.informatieObjectenService.maakDocument(documentCreatieGegeven)
                .subscribe((documentCreatieResponse) => {
                    if (documentCreatieResponse.redirectURL) {
                        this.redirectUrl.emit(documentCreatieResponse.redirectURL);
                    } else {
                        this.melding.emit(documentCreatieResponse.message);
                    }
                    this.resetAndClose();
                });
        } else {
            this.resetAndClose();
        }
    }

    private resetAndClose() {
        this.fields.forEach(row => row.forEach(field => field.formControl.reset()));
        this.sideNav.close();
    }
}
