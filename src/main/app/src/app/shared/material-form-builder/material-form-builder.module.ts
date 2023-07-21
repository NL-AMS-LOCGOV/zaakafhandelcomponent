/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ModuleWithProviders, NgModule } from "@angular/core";
import { FormComponent } from "./form/form/form.component";
import { FormFieldComponent } from "./form/form-field/form-field.component";
import { InputComponent } from "./form-components/input/input.component";
import { FormFieldDirective } from "./form/form-field/form-field.directive";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { CommonModule } from "@angular/common";
import { MatFormFieldModule } from "@angular/material/form-field";
import { DateComponent } from "./form-components/date/date.component";
import { HeadingComponent } from "./form-components/heading/heading.component";
import { SelectComponent } from "./form-components/select/select.component";
import { TextareaComponent } from "./form-components/textarea/textarea.component";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatInputModule } from "@angular/material/input";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatIconModule } from "@angular/material/icon";
import { MatSelectModule } from "@angular/material/select";
import { GoogleMapsComponent } from "./form-components/google-maps/google-maps.component";
import { GoogleMapsModule } from "@angular/google-maps";
import { HttpClientJsonpModule, HttpClientModule } from "@angular/common/http";
import { ReadonlyComponent } from "./form-components/readonly/readonly.component";
import {
  BUILDER_CONFIG,
  MaterialFormBuilderConfig,
} from "./material-form-builder-config";
import { FileComponent } from "./form-components/file/file.component";
import { TranslateModule } from "@ngx-translate/core";
import { AutocompleteComponent } from "./form-components/autocomplete/autocomplete.component";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { PipesModule } from "../pipes/pipes.module";
import { CheckboxComponent } from "./form-components/checkbox/checkbox.component";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatChipsModule } from "@angular/material/chips";
import { MatTableModule } from "@angular/material/table";
import { RouterModule } from "@angular/router";
import { TaakDocumentUploadComponent } from "./form-components/taak-document-upload/taak-document-upload.component";
import { RadioComponent } from "./form-components/radio/radio.component";
import { MatRadioModule } from "@angular/material/radio";
import { ParagraphComponent } from "./form-components/paragraph/paragraph.component";
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
} from "@angular/material/core";
import {
  MAT_MOMENT_DATE_ADAPTER_OPTIONS,
  MomentDateAdapter,
} from "@angular/material-moment-adapter";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MedewerkerGroepComponent } from "./form-components/medewerker-groep/medewerker-groep.component";
import { DividerComponent } from "./form-components/divider/divider.component";
import { MatDividerModule } from "@angular/material/divider";
import { HiddenComponent } from "./form-components/hidden/hidden.component";
import { HtmlEditorComponent } from "./form-components/html-editor/html-editor.component";
import { NgxEditorModule } from "ngx-editor";
import { HtmlEditorVariabelenKiesMenuComponent } from "./form-components/html-editor/html-editor-variabelen-kies-menu.component";
import { MatMenuModule } from "@angular/material/menu";
import { MatListModule } from "@angular/material/list";
import { DocumentenLijstComponent } from "./form-components/documenten-lijst/documenten-lijst.component";
import { DocumentenOndertekenenComponent } from "./form-components/documenten-ondertekenen/documenten-ondertekenen.component";
import { DocumentIconComponent } from "../document-icon/document-icon.component";
import { DragAndDropDirective } from "./form-components/file/drag-and-drop.directive";
import { MessageComponent } from "./form-components/message/message.component";
import { InformatieObjectIndicatiesComponent } from "../indicaties/informatie-object-indicaties/informatie-object-indicaties.component";

@NgModule({
  declarations: [
    FormComponent,
    FormFieldComponent,
    DateComponent,
    HeadingComponent,
    HtmlEditorComponent,
    HtmlEditorVariabelenKiesMenuComponent,
    InputComponent,
    FileComponent,
    SelectComponent,
    MedewerkerGroepComponent,
    CheckboxComponent,
    TextareaComponent,
    GoogleMapsComponent,
    FormFieldDirective,
    ReadonlyComponent,
    AutocompleteComponent,
    DocumentenLijstComponent,
    DocumentenOndertekenenComponent,
    TaakDocumentUploadComponent,
    RadioComponent,
    ParagraphComponent,
    DividerComponent,
    HiddenComponent,
    DragAndDropDirective,
    MessageComponent,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    HttpClientJsonpModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    GoogleMapsModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatAutocompleteModule,
    MatChipsModule,
    TranslateModule,
    PipesModule,
    MatTableModule,
    RouterModule,
    FormsModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    NgxEditorModule,
    MatMenuModule,
    MatListModule,
    DocumentIconComponent,
    InformatieObjectIndicatiesComponent,
  ],
  exports: [
    FormComponent,
    FormFieldComponent,
    DateComponent,
    HeadingComponent,
    HtmlEditorComponent,
    InputComponent,
    FileComponent,
    SelectComponent,
    MedewerkerGroepComponent,
    CheckboxComponent,
    TextareaComponent,
    GoogleMapsComponent,
    AutocompleteComponent,
    DocumentenLijstComponent,
    DocumentenOndertekenenComponent,
    TaakDocumentUploadComponent,
    RadioComponent,
    ParagraphComponent,
    MessageComponent,
  ],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS],
    },
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
      useValue: {
        strict: false,
      },
    },
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: "DD-MM-YYYY",
        },
        display: {
          dateInput: "DD-MM-YYYY",
          monthYearLabel: "MMMM YYYY",
          dateA11yLabel: "LL",
          monthYearA11yLabel: "MMMM YYYY",
        },
      },
    },
  ],
})
export class MaterialFormBuilderModule {
  static forRoot(
    config?: MaterialFormBuilderConfig,
  ): ModuleWithProviders<MaterialFormBuilderModule> {
    return {
      ngModule: MaterialFormBuilderModule,
      providers: [{ provide: BUILDER_CONFIG, useValue: config }],
    };
  }
}
