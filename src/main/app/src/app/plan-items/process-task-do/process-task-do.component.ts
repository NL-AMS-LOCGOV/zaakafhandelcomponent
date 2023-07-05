/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PlanItem} from '../model/plan-item';
import {PlanItemsService} from '../plan-items.service';
import {ProcessTaskData} from '../model/process-task-data';
import {Zaak} from '../../zaken/model/zaak';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';
import {FormulierRuntimeContext} from '../../admin/model/formulieren/formulier-runtime-context';
import {FormulierRuntimeService} from '../../admin/formulier-runtime.service';

@Component({
    selector: 'zac-process-task-do',
    templateUrl: './process-task-do.component.html',
    styleUrls: ['./process-task-do.component.less']
})
export class ProcessTaskDoComponent implements OnInit {

    formulierDefinitie: FormulierDefinitie;
    @Input() planItem: PlanItem;
    @Input() zaak: Zaak;
    @Output() done = new EventEmitter<void>();

    constructor(private planItemsService: PlanItemsService, private formulierRuntimeService: FormulierRuntimeService) {
    }

    ngOnInit(): void {
        const context = new FormulierRuntimeContext();
        context.formulierSysteemnaam = this.planItem.startformulierDefinitie;
        context.zaak = this.zaak;
        this.formulierRuntimeService.run(context)
                .subscribe(fd => this.formulierDefinitie = fd);
    }

    onFormSubmit(formState: {}): void {
        if (formState) {
            const processTaskData = new ProcessTaskData();
            processTaskData.data = formState;
            processTaskData.planItemInstanceId = this.planItem.id;
            this.planItemsService.doProcessTaskPlanItem(processTaskData).subscribe(() => {
                this.done.emit();
            });
        } else { // cancel button clicked
            this.done.emit();
        }
    }
}
