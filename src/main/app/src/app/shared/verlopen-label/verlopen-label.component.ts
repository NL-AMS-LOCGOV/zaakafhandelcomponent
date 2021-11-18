/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, Input, LOCALE_ID, OnInit} from '@angular/core';
import {TaakStatus} from '../../taken/model/taak-status.enum';
import * as moment from 'moment';
import {StaticTextComponent} from '../static-text/static-text.component';

@Component({
    selector: 'zac-verlopen-label',
    templateUrl: './verlopen-label.component.html',
    styleUrls: ['../static-text/static-text.component.less','./verlopen-label.component.less']
})
export class VerlopenLabelComponent extends StaticTextComponent implements OnInit { // TODO ESUITEDEV-25900 opruimen

    @Input() ended: string;

    constructor(@Inject(LOCALE_ID) public locale: string) {
        super();
    }

    ngOnInit(): void { }

    bepaalVerlopen(value: string, ended: string): boolean {
        if (value) {
            var limit: moment.Moment = moment(value, moment.ISO_8601).locale(this.locale);
            if (ended) {
                var actual: moment.Moment = moment(ended, moment.ISO_8601).locale(this.locale);
                return limit.isBefore(actual);
            } else {
                return limit.isBefore();
            }
        }
        return false;
    }
}
