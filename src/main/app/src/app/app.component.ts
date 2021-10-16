/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {UtilService} from './core/service/util.service';

@Component({
    selector: 'zac-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.less']
})
export class AppComponent implements OnInit {

    constructor(private translate: TranslateService, private titleService: Title, public utilService: UtilService) {

    }

    ngOnInit(): void {
        this.titleService.setTitle('Zaakafhandelcomponent');
        this.translate.addLangs(['nl', 'en']);
        this.translate.setDefaultLang('nl');

        let browserLanguage = this.translate.getBrowserLang();
        this.translate.use(browserLanguage.match(/nl|en/) ? browserLanguage : 'nl');
    }
}
