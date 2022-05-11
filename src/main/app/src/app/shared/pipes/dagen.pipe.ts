/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Pipe, PipeTransform} from '@angular/core';
import * as moment from 'moment';
import {TranslateService} from '@ngx-translate/core';

@Pipe({name: 'dagen'})
export class DagenPipe implements PipeTransform {

    constructor(private translate: TranslateService) {}

    transform(value: any): string {
        if (value) {
            const vandaag = moment(new Date()).startOf('day');
            const vergelijkDatum = moment(value).startOf('day');
            const aantalDagen = vergelijkDatum.diff(vandaag, 'days');

            let result;
            if (aantalDagen === 0) {
                result = this.translate.instant('verloopt.vandaag');
            } else if (aantalDagen >= 1) {
                result = aantalDagen === 1 ? this.translate.instant('verloopt.over.dag', {dagen: aantalDagen}) :
                    this.translate.instant('verloopt.over.dagen', {dagen: aantalDagen});
            }

            return result;
        }
    }

}
