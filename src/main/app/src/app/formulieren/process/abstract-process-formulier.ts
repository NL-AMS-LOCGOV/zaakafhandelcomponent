/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormGroup} from '@angular/forms';
import {ProcessTaskData} from '../../plan-items/model/process-task-data';

export abstract class AbstractProcessFormulier {

    getData(formGroup: FormGroup): ProcessTaskData {
        return null;
    }
}
