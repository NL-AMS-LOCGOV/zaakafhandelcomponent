/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ActionIcon} from '../../../shared/edit/action-icon';
import {Subject} from 'rxjs';

export class ActionBarAction {

    constructor(public text: string, public subText: string | null,
                public action: ActionIcon, public dissmis: Subject<void>, public actionEnabled?: () => boolean) {
        if (!this.actionEnabled) {
            this.actionEnabled = () => true;
        }
    }
}
