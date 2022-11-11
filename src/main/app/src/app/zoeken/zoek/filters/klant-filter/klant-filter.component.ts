/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {KlantFilterDialog} from './klant-filter.dialog';

@Component({
    selector: 'zac-klant-filter',
    templateUrl: './klant-filter.component.html',
    styleUrls: ['./klant-filter.component.less']
})
export class KlantFilterComponent implements OnInit {
    oldValue: string;
    @Input() value: string;
    @Input() label: string;
    @Output() changed = new EventEmitter<string>();
    dialogOpen: boolean;

    constructor(public dialog: MatDialog) {}

    ngOnInit(): void {
        this.oldValue = this.value;
    }

    change(): void {
        if (this.oldValue !== this.value) {
            this.oldValue = this.value;
            this.changed.emit(this.value);
        }
    }

    openDialog(): void {
        this.dialogOpen = true;
        const dialogRef = this.dialog.open(KlantFilterDialog, {
            minWidth: '750px',
            backdropClass: 'noColor'
        });
        dialogRef.afterClosed().subscribe(result => {
            this.dialogOpen = false;
            if (result) {
                this.value = result.identificatie;
            }
            this.change();
        });

    }

}
