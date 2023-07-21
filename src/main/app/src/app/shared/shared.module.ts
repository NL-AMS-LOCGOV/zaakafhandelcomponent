/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { APP_INITIALIZER, Injector, NgModule } from "@angular/core";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { DragDropModule } from "@angular/cdk/drag-drop";
import { Title } from "@angular/platform-browser";
import { SideNavComponent } from "./side-nav/side-nav.component";
import { RouterModule } from "@angular/router";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { FormsModule } from "@angular/forms";
import { BackButtonDirective } from "./navigation/back-button.directive";
import { StaticTextComponent } from "./static-text/static-text.component";
import { MaterialModule } from "./material/material.module";
import { MaterialFormBuilderModule } from "./material-form-builder/material-form-builder.module";
import { StatusLabelComponent } from "./status-label/status-label.component";
import { MatPaginatorIntl } from "@angular/material/paginator";
import { PaginatorTranslator } from "./paginator/paginator-translator";
import { paginatorLanguageInitializerFactory } from "./paginator/paginator-language-initializer";
import { OutsideClickDirective } from "./directives/outside-click.directive";
import { ReadMoreComponent } from "./read-more/read-more.component";
import { EditBehandelaarComponent } from "./edit/edit-behandelaar/edit-behandelaar.component";
import { EditTekstComponent } from "./edit/edit-tekst/edit-tekst.component";
import { EditAutocompleteComponent } from "./edit/edit-autocomplete/edit-autocomplete.component";
import { EditDatumComponent } from "./edit/edit-datum/edit-datum.component";
import { PipesModule } from "./pipes/pipes.module";
import { EditGroepComponent } from "./edit/edit-groep/edit-groep.component";
import { DialogComponent } from "./dialog/dialog.component";
import { NoStickyColumnPipe } from "./dynamic-table/pipes/no-sticky-column.pipe";
import { ColumnPickerComponent } from "./dynamic-table/column-picker/column-picker.component";
import { EditDatumGroepComponent } from "./edit/edit-datum-groep/edit-datum-groep.component";
import { ConfirmDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { DocumentViewerComponent } from "./document-viewer/document-viewer.component";
import { TooltipListPipe } from "./dynamic-table/pipes/tooltip-list.pipe";
import { EditSelectComponent } from "./edit/edit-select/edit-select.component";
import { DagenPipe } from "./pipes/dagen.pipe";
import { NotificationDialogComponent } from "./notification-dialog/notification-dialog.component";
import { EditInputComponent } from "./edit/edit-input/edit-input.component";
import { EditGroepBehandelaarComponent } from "./edit/edit-groep-behandelaar/edit-groep-behandelaar.component";
import { ExportButtonComponent } from "./export-button/export-button.component";
import { DateRangeFilterComponent } from "./table-zoek-filters/date-range-filter/date-range-filter.component";
import { FacetFilterComponent } from "./table-zoek-filters/facet-filter/facet-filter.component";
import { TekstFilterComponent } from "./table-zoek-filters/tekst-filter/tekst-filter.component";
import { ToggleFilterComponent } from "./table-zoek-filters/toggle-filter/toggle-filter.component";
import { ZaakIndicatiesComponent } from "./indicaties/zaak-indicaties/zaak-indicaties.component";
import { VersionComponent } from "./version/version.component";
import { SortPipe } from "./dynamic-table/pipes/sort.pipe";
import { BesluitIndicatiesComponent } from "./indicaties/besluit-indicaties/besluit-indicaties.component";

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
    EditGroepBehandelaarComponent,
    EditDatumComponent,
    EditDatumGroepComponent,
    EditAutocompleteComponent,
    EditInputComponent,
    EditTekstComponent,
    EditSelectComponent,
    DateRangeFilterComponent,
    FacetFilterComponent,
    TekstFilterComponent,
    ToggleFilterComponent,
    ConfirmDialogComponent,
    DialogComponent,
    NoStickyColumnPipe,
    ColumnPickerComponent,
    TooltipListPipe,
    DocumentViewerComponent,
    DagenPipe,
    NotificationDialogComponent,
    ExportButtonComponent,
    BesluitIndicatiesComponent,
    ZaakIndicatiesComponent,
    VersionComponent,
    SortPipe,
  ],
  imports: [
    FormsModule,
    BrowserAnimationsModule,
    RouterModule,
    PipesModule,
    MaterialModule,
    MaterialFormBuilderModule.forRoot(),
    TranslateModule,
  ],
  exports: [
    BrowserAnimationsModule,
    FormsModule,
    TranslateModule,
    DragDropModule,
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
    EditGroepBehandelaarComponent,
    EditDatumComponent,
    EditDatumGroepComponent,
    EditAutocompleteComponent,
    EditInputComponent,
    EditTekstComponent,
    EditSelectComponent,
    DateRangeFilterComponent,
    FacetFilterComponent,
    TekstFilterComponent,
    ToggleFilterComponent,
    DialogComponent,
    ConfirmDialogComponent,
    NoStickyColumnPipe,
    TooltipListPipe,
    DocumentViewerComponent,
    ColumnPickerComponent,
    DagenPipe,
    ExportButtonComponent,
    BesluitIndicatiesComponent,
    ZaakIndicatiesComponent,
    VersionComponent,
    SortPipe,
  ],
  providers: [
    Title,
    {
      provide: MatPaginatorIntl,
      deps: [TranslateService],
      useFactory: (translateService: TranslateService) =>
        new PaginatorTranslator(translateService).getTranslatedPaginator(),
    },
    {
      provide: APP_INITIALIZER,
      useFactory: paginatorLanguageInitializerFactory,
      deps: [TranslateService, Injector],
      multi: true,
    },
  ],
})
export class SharedModule {}
