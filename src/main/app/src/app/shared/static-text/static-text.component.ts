/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TextIcon} from '../edit/text-icon';
import {FormControl} from '@angular/forms';

@Component({
    selector: 'zac-static-text',
    templateUrl: './static-text.component.html',
    styleUrls: ['./static-text.component.less']
})
export class StaticTextComponent implements OnInit {

    @Input() label: string;
    @Input() value: any;
    @Input() icon: TextIcon;
    @Output() iconClicked = new EventEmitter<void>();

    showIcon: boolean;

    constructor() {
    }

    ngOnInit(): void {
        this.showIcon = this.icon?.showIcon(new FormControl(this.value));
    }

    get hasIcon(): boolean {
        return this.showIcon;
    }
}
