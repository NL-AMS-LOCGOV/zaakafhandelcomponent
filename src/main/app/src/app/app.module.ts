/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injector, NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ServiceWorkerModule} from '@angular/service-worker';
import {environment} from '../environments/environment';
import {CoreModule} from './core/core.module';
import {APP_BASE_HREF, LocationStrategy, PathLocationStrategy} from '@angular/common';
import {SharedModule} from './shared/shared.module';
import {ZakenModule} from './zaken/zaken.module';
import {FoutAfhandelingModule} from './fout-afhandeling/fout-afhandeling.module';
import {DashboardModule} from './dashboard/dashboard.module';
import {InformatieObjectenModule} from './informatie-objecten/informatie-objecten.module';
import {PlanItemsModule} from './plan-items/plan-items.module';
import {TakenModule} from './taken/taken.module';
import {ToolbarComponent} from './core/toolbar/toolbar.component';

@NgModule({
    declarations: [
        AppComponent,
        ToolbarComponent
    ],
    imports: [
        HttpClientModule,
        CoreModule,
        SharedModule,
        ServiceWorkerModule.register('ngsw-worker.js', {enabled: environment.production}),
        DashboardModule,
        FoutAfhandelingModule,
        ZakenModule,
        InformatieObjectenModule,
        PlanItemsModule,
        TakenModule,
        AppRoutingModule
    ],
    exports: [
        ToolbarComponent
    ],
    providers: [{provide: APP_BASE_HREF, useValue: '/'}, {provide: LocationStrategy, useClass: PathLocationStrategy}],
    bootstrap: [AppComponent]
})
export class AppModule {
    static injector: Injector;

    constructor(injector: Injector) {
        AppModule.injector = injector;
    }
}
