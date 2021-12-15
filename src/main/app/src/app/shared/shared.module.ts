/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {APP_INITIALIZER, Injector, NgModule} from '@angular/core';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
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
import {StatusLabelComponent} from './status-label/status-label.component';
import {BehandelaarVeldComponent} from './behandelaar-veld/behandelaar-veld.component';
import {DatumOverschredenComponent} from './datum-overschreden/datum-overschreden.component';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {PaginatorTranslator} from './paginator/paginator-translator';
import {DatumOverschredenPipe} from './pipes/datumOverschreden.pipe';
import {paginatorLanguageInitializerFactory} from './paginator/paginator-language-initializer';
import {OutsideClickDirective} from './directives/outside-click.directive';
import {ReadMoreComponent} from './read-more/read-more.component';
import {EditBehandelaarComponent} from './edit/edit-behandelaar/edit-behandelaar.component';
import {EditTekstComponent} from './edit/edit-tekst/edit-tekst.component';
import {EditGroepComponent} from './edit/edit-groep/edit-groep.component';
import {EditDatumComponent} from './edit/edit-datum/edit-datum.component';

@NgModule({
    declarations: [
        SideNavComponent,
        EmptyPipe,
        DatumPipe,
        DatumOverschredenPipe,
        NoStickyColumnPipe,
        ColumnToStringPipe,
        VisibleColumnPipe,
        FilterColumnPipe,
        BackButtonDirective,
        DynamicPipe,
        StaticTextComponent,
        ReadMoreComponent,
        StatusLabelComponent,
        BehandelaarVeldComponent,
        DatumOverschredenComponent,
        OutsideClickDirective,
        DatumOverschredenComponent,
        EditBehandelaarComponent,
        EditDatumComponent,
        EditGroepComponent,
        EditTekstComponent
    ],
    imports: [
        BrowserAnimationsModule,
        RouterModule,
        MaterialModule,
        MaterialFormBuilderModule.forRoot(),
        StoreModule.forFeature('shared', {
            'sideNav': sideNavReducer
        }),
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
        DatumOverschredenPipe,
        NoStickyColumnPipe,
        ColumnToStringPipe,
        VisibleColumnPipe,
        FilterColumnPipe,
        BackButtonDirective,
        DynamicPipe,
        StaticTextComponent,
        ReadMoreComponent,
        MaterialModule,
        MaterialFormBuilderModule,
        StatusLabelComponent,
        BehandelaarVeldComponent,
        DatumOverschredenComponent,
        EditBehandelaarComponent,
        EditDatumComponent,
        EditGroepComponent,
        EditTekstComponent
    ],
    providers: [
        Title,
        {
            provide: MatPaginatorIntl,
            deps: [TranslateService],
            useFactory: (translateService: TranslateService) => new PaginatorTranslator(
                translateService).getTranslatedPaginator()
        },
        {
            provide: APP_INITIALIZER,
            useFactory: paginatorLanguageInitializerFactory,
            deps: [TranslateService, Injector],
            multi: true
        }
    ]
})
export class SharedModule {
}
