/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {PlanItem} from '../../plan-items/model/plan-item';
import {PlanItemsService} from '../../plan-items/plan-items.service';

@Component({
    selector: 'zac-confirm-met-reden-dialog',
    templateUrl: 'confirm-dialog-met-reden.component.html'
})

export class ConfirmDialogMetRedenComponent {

    reden = '';
    loading = false;
    redenEmpty = true;
    redenError = false;

    constructor(
        public dialogRef: MatDialogRef<ConfirmDialogMetRedenComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ConfirmMetRedenDialogData,
        private planItemsService: PlanItemsService) {
    }

    confirm(): void {
        this.loading = true;
        this.dialogRef.disableClose = true;
        this.data.planItem.toelichting = this.reden;
        this.planItemsService.doPlanItem(this.data.planItem).subscribe(() => {
            this.dialogRef.close(true);
        });
    }

    cancel(): void {
        this.dialogRef.close(false);
    }

    updateState(): void {
        this.redenEmpty = this.redenError = this.reden.length === 0;
    }
}

export class ConfirmMetRedenDialogData {
    constructor(public melding: string, public planItem?: PlanItem) {
    }
}
