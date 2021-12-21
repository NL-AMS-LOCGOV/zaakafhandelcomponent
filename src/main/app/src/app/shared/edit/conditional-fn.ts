/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormControl} from '@angular/forms';
import * as moment from 'moment';

export declare interface ConditionalFn {
    (control: FormControl): boolean;
}

export class Conditionals {

    static isAfterDate(actual?: Date | moment.Moment | string): ConditionalFn {

        return (control: FormControl): boolean => {
            if (control.value) {
                let limit: moment.Moment = moment(control.value, moment.ISO_8601);
                if (actual) {
                    return limit.isBefore(moment(actual, moment.ISO_8601));
                } else {
                    return limit.isBefore();
                }
            }
            return false;
        };
    }
}
