/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {UtilService} from './core/service/util.service';
import {InformatieObjectenService} from './informatie-objecten/informatie-objecten.service';
import {SessionStorageUtil} from './shared/storage/session-storage.util';
import {ZakenService} from './zaken/zaken.service';

@Component({
    selector: 'zac-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.less']
})
export class AppComponent implements OnInit, AfterViewInit {

    initialized = false;

    constructor(private translate: TranslateService, private titleService: Title,
                private infoService: InformatieObjectenService, private zakenService: ZakenService,
                public utilService: UtilService) {

    }

    ngOnInit(): void {
        SessionStorageUtil.clearSessionStorage();
        this.titleService.setTitle('Zaakafhandelcomponent');
        this.translate.addLangs(['nl', 'en']);
        this.translate.setDefaultLang('nl');
        const browserLanguage = this.translate.getBrowserLang();
        this.translate.use(browserLanguage.match(/nl|en/) ? browserLanguage : 'nl');
    }

    ngAfterViewInit(): void {
        if (!this.initialized) {
            this.infoService.appInit();
            this.zakenService.appInit();
            this.initialized = true;
        }
    }
}
