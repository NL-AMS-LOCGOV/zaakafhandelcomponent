/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {Title} from '@angular/platform-browser';
import {FlexLayoutModule} from '@angular/flex-layout';
import {SideNavComponent} from './side-nav/side-nav.component';
import {RouterModule} from '@angular/router';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {EmptyPipe} from './pipes/empty.pipe';
import {FormsModule} from '@angular/forms';
import {NoStickyColumnPipe} from './dynamic-table/pipes/no-sticky-column.pipe';
import {ColumnToStringPipe} from './dynamic-table/pipes/column-to-string.pipe';
import {VisibleColumnPipe} from './dynamic-table/pipes/visible-column.pipe';
import {FilterColumnPipe} from './dynamic-table/pipes/filter-column.pipe';
import {DatumPipe} from './pipes/datum.pipe';
import {BackButtonDirective} from './navigation/back-button.directive';
import {DynamicPipe} from './dynamic-table/pipes/dynamic.pipe';
import {StoreModule} from '@ngrx/store';
import {sideNavReducer} from './state/side-nav.reducer';
import {StaticTextComponent} from './static-text/static-text.component';
import {MaterialModule} from './material/material.module';
import {MaterialFormBuilderModule} from './material-form-builder/material-form-builder.module';
import {NotitiesComponent} from './notities/notities.component';

@NgModule({
    declarations: [
        SideNavComponent,
        EmptyPipe,
        DatumPipe,
        NoStickyColumnPipe,
        ColumnToStringPipe,
        VisibleColumnPipe,
        FilterColumnPipe,
        BackButtonDirective,
        DynamicPipe,
        StaticTextComponent,
        NotitiesComponent
    ],
    imports: [
        BrowserAnimationsModule,
        RouterModule,
        MaterialModule,
        MaterialFormBuilderModule.forRoot(),
        StoreModule.forFeature('shared', {
            'sideNav': sideNavReducer
        }),
        //MatBadgeModule,
        TranslateModule
    ],
    exports: [
        BrowserAnimationsModule,
        FormsModule,
        TranslateModule,
        DragDropModule,
        FlexLayoutModule,
        SideNavComponent,
        EmptyPipe,
        DatumPipe,
        NoStickyColumnPipe,
        ColumnToStringPipe,
        VisibleColumnPipe,
        FilterColumnPipe,
        BackButtonDirective,
        DynamicPipe,
        StaticTextComponent,
        MaterialModule,
        MaterialFormBuilderModule,
        NotitiesComponent
    ],
    providers: [
        Title
    ]
})
export class SharedModule {
}
