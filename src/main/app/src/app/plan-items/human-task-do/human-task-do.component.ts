/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute} from '@angular/router';
import {PlanItemsService} from '../plan-items.service';
import {FormGroup} from '@angular/forms';
import {PlanItem} from '../model/plan-item';
import {PlanItemType} from '../model/plan-item-type.enum';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {AbstractFormulier} from '../../formulieren/model/abstract-formulier';
import {TaakFormulierenService} from '../../formulieren/taak-formulieren.service';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';

@Component({
    selector: 'zac-human-task-do',
    templateUrl: './human-task-do.component.html',
    styleUrls: ['./human-task-do.component.less']
})
export class HumanTaskDoComponent implements OnInit {

    formItems: Array<AbstractFormField[]>;
    formConfig: FormConfig;
    private formulier: AbstractFormulier;
    @Input() planItem: PlanItem;
    @Output() done = new EventEmitter<void>();

    constructor(private route: ActivatedRoute, private planItemsService: PlanItemsService, private taakFormulierenService: TaakFormulierenService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.starten').cancelText('actie.annuleren').build();
        if (this.planItem.type === PlanItemType.HumanTask) {
            this.formulier = this.taakFormulierenService.getFormulierBuilder(this.planItem.formulierDefinitie)
                                 .startForm(this.planItem).build();
            if (this.formulier.disablePartialSave) {
                this.formConfig.partialButtonText = null;
            }
            this.formItems = this.formulier.form;
        } else {
            this.formItems = [[]];
        }
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.planItem = this.formulier.getPlanItem(formGroup);
            this.planItemsService.doHumanTask(this.planItem).subscribe(() => {
                this.done.emit();
            });
        } else { // cancel button clicked
            this.done.emit();
        }
    }
}
