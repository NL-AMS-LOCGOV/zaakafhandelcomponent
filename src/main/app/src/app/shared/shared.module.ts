/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
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
import {FormsModule} from '@angular/forms';
import {BackButtonDirective} from './navigation/back-button.directive';
import {StaticTextComponent} from './static-text/static-text.component';
import {MaterialModule} from './material/material.module';
import {MaterialFormBuilderModule} from './material-form-builder/material-form-builder.module';
import {StatusLabelComponent} from './status-label/status-label.component';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {PaginatorTranslator} from './paginator/paginator-translator';
import {paginatorLanguageInitializerFactory} from './paginator/paginator-language-initializer';
import {OutsideClickDirective} from './directives/outside-click.directive';
import {ReadMoreComponent} from './read-more/read-more.component';
import {EditBehandelaarComponent} from './edit/edit-behandelaar/edit-behandelaar.component';
import {EditTekstComponent} from './edit/edit-tekst/edit-tekst.component';
import {EditAutocompleteComponent} from './edit/edit-autocomplete/edit-autocomplete.component';
import {EditDatumComponent} from './edit/edit-datum/edit-datum.component';
import {PipesModule} from './pipes/pipes.module';
import {EditGroepComponent} from './edit/edit-groep/edit-groep.component';
import {DialogComponent} from './dialog/dialog.component';
import {NoStickyColumnPipe} from './dynamic-table/pipes/no-sticky-column.pipe';
import {ColumnPickerComponent} from './dynamic-table/column-picker/column-picker.component';
import {EditDatumGroepComponent} from './edit/edit-datum-groep/edit-datum-groep.component';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {DocumentViewerComponent} from './document-viewer/document-viewer.component';
import {PdfJsViewerModule} from 'ng2-pdfjs-viewer';
import {TooltipListPipe} from './dynamic-table/pipes/tooltip-list.pipe';
import {EditSelectComponent} from './edit/edit-select/edit-select.component';
import {NgxSkeletonLoaderModule} from 'ngx-skeleton-loader';
import {SkeletonLoaderComponent} from './skeleton-loader/skeleton-loader.component';
import {DagenPipe} from './pipes/dagen.pipe';
import {NotificationDialogComponent} from './notification-dialog/notification-dialog.component';

@NgModule({
    declarations: [
        SideNavComponent,
        BackButtonDirective,
        StaticTextComponent,
        ReadMoreComponent,
        StatusLabelComponent,
        OutsideClickDirective,
        EditBehandelaarComponent,
        EditGroepComponent,
        EditDatumComponent,
        EditDatumGroepComponent,
        EditAutocompleteComponent,
        EditTekstComponent,
        EditSelectComponent,
        ConfirmDialogComponent,
        DialogComponent,
        NoStickyColumnPipe,
        ColumnPickerComponent,
        TooltipListPipe,
        DocumentViewerComponent,
        SkeletonLoaderComponent,
        DagenPipe,
        NotificationDialogComponent
    ],
    imports: [
        FormsModule,
        BrowserAnimationsModule,
        FlexLayoutModule,
        RouterModule,
        PipesModule,
        MaterialModule,
        MaterialFormBuilderModule.forRoot(),
        TranslateModule,
        PdfJsViewerModule,
        NgxSkeletonLoaderModule.forRoot({animation: 'pulse'})
    ],
    exports: [
        BrowserAnimationsModule,
        FormsModule,
        TranslateModule,
        DragDropModule,
        FlexLayoutModule,
        SideNavComponent,
        BackButtonDirective,
        StaticTextComponent,
        ReadMoreComponent,
        PipesModule,
        MaterialModule,
        MaterialFormBuilderModule,
        StatusLabelComponent,
        EditBehandelaarComponent,
        EditGroepComponent,
        EditDatumComponent,
        EditDatumGroepComponent,
        EditAutocompleteComponent,
        EditTekstComponent,
        EditSelectComponent,
        DialogComponent,
        ConfirmDialogComponent,
        NoStickyColumnPipe,
        TooltipListPipe,
        DocumentViewerComponent,
        ColumnPickerComponent,
        SkeletonLoaderComponent,
        DagenPipe
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
