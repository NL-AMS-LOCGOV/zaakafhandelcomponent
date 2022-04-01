/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {PlanItem} from '../../plan-items/model/plan-item';
import {PlanItemsService} from '../../plan-items/plan-items.service';

@Component({
    selector: 'zac-confirm-dialog',
    templateUrl: 'confirm-dialog.component.html'
})

export class ConfirmDialogComponent {

    redenText = '';
    loading = false;
    redenEmpty = true;
    redenError = false;

    constructor(
        public dialogRef: MatDialogRef<ConfirmDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData,
        private planItemsService: PlanItemsService) {
    }

    confirm(): void {
        if (this.data.observable) {
            this.loading = true;
            this.dialogRef.disableClose = true;
            this.data.observable.subscribe(() => {
                this.dialogRef.close(true);
            });
        } else if (this.data.planItem && this.data.reden) {
            this.loading = true;
            this.dialogRef.disableClose = true;
            this.data.planItem.toelichting = this.redenText;
            this.planItemsService.doPlanItem(this.data.planItem).subscribe(() => {
                this.dialogRef.close(true);
            });
        } else {
            this.dialogRef.close(true);
        }
    }

    cancel(): void {
        this.dialogRef.close(false);
    }

    updateState(): void {
        this.redenEmpty = this.redenError = this.redenText.length === 0;
    }
}

export class ConfirmDialogData {
    constructor(public melding: string, public reden: boolean, public planItem?: PlanItem,
                public observable?: Observable<any>) {
    }
}
