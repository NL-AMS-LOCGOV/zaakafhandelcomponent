/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {FormGroup} from '@angular/forms';
import {PlanItem} from '../model/plan-item';
import {PlanItemsService} from '../plan-items.service';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {VerzendenBesluitFormulier} from '../../formulieren/taken/model/verzenden-besluit-formulier';
import {ProcessTaskData} from '../model/process-task-data';

@Component({
    selector: 'zac-process-task-do',
    templateUrl: './process-task-do.component.html',
    styleUrls: ['./process-task-do.component.less']
})
export class ProcessTaskDoComponent implements OnInit {

    formConfig: FormConfig;
    formulier: VerzendenBesluitFormulier;
    @Input() planItem: PlanItem;
    @Output() done = new EventEmitter<void>();

    constructor(private planItemsService: PlanItemsService) {
    }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder()
        .saveText('actie.starten')
        .cancelText('actie.annuleren')
        .build();
        this.formulier = new VerzendenBesluitFormulier();
        this.formulier.initForm();
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            const processTaskData = new ProcessTaskData();
            processTaskData.planItemInstanceId = this.planItem.id;
            processTaskData.data = this.formulier.getData(formGroup);
            this.planItemsService.doProcessTaskPlanItem(processTaskData).subscribe(() => {
                this.done.emit();
            });
        } else { // cancel button clicked
            this.done.emit();
        }
    }
}
