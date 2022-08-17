/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ToggleSwitchOptions} from './toggle-switch-options';

@Component({
    selector: 'zac-toggle-filter',
    templateUrl: './toggle-filter.component.html',
    styleUrls: ['./toggle-filter.component.less']
})
export class ToggleFilterComponent {
    @Input() selected: ToggleSwitchOptions = ToggleSwitchOptions.INDETERMINATE;
    @Input() checkedIcon: string = 'check_circle_outline';
    @Input() unCheckedIcon: string = 'cancel';
    @Input() indeterminateIcon: string = 'radio_button_unchecked';
    @Output() changed = new EventEmitter<ToggleSwitchOptions>();

    toggle() {
        switch (this.selected) {
            case ToggleSwitchOptions.CHECKED:
                this.selected = ToggleSwitchOptions.UNCHECKED;
                break;
            case ToggleSwitchOptions.UNCHECKED:
                this.selected = ToggleSwitchOptions.INDETERMINATE;
                break;
            case ToggleSwitchOptions.INDETERMINATE:
                this.selected = ToggleSwitchOptions.CHECKED;
                break;
        }

        this.changed.emit(this.selected);
    }

}
