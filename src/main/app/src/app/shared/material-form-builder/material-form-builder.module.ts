/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ModuleWithProviders, NgModule} from '@angular/core';
import {FormComponent} from './form/form/form.component';
import {FormFieldComponent} from './form/form-field/form-field.component';
import {InputComponent} from './form-components/input/input.component';
import {FormFieldDirective} from './form/form-field/form-field.directive';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyButtonModule as MatButtonModule} from '@angular/material/legacy-button';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';
import {MatLegacyFormFieldModule as MatFormFieldModule} from '@angular/material/legacy-form-field';
import {DateComponent} from './form-components/date/date.component';
import {HeadingComponent} from './form-components/heading/heading.component';
import {SelectComponent} from './form-components/select/select.component';
import {TextareaComponent} from './form-components/textarea/textarea.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatLegacyInputModule as MatInputModule} from '@angular/material/legacy-input';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIconModule} from '@angular/material/icon';
import {MatLegacySelectModule as MatSelectModule} from '@angular/material/legacy-select';
import {GoogleMapsComponent} from './form-components/google-maps/google-maps.component';
import {GoogleMapsModule} from '@angular/google-maps';
import {HttpClientJsonpModule, HttpClientModule} from '@angular/common/http';
import {ReadonlyComponent} from './form-components/readonly/readonly.component';
import {BUILDER_CONFIG, MaterialFormBuilderConfig} from './material-form-builder-config';
import {FileComponent} from './form-components/file/file.component';
import {TranslateModule} from '@ngx-translate/core';
import {AutocompleteComponent} from './form-components/autocomplete/autocomplete.component';
import {MatLegacyAutocompleteModule as MatAutocompleteModule} from '@angular/material/legacy-autocomplete';
import {PipesModule} from '../pipes/pipes.module';
import {CheckboxComponent} from './form-components/checkbox/checkbox.component';
import {MatLegacyCheckboxModule as MatCheckboxModule} from '@angular/material/legacy-checkbox';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';
import {DocumentenLijstComponent} from './form-components/documenten-lijst/documenten-lijst.component';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';
import {RouterModule} from '@angular/router';
import {TaakDocumentUploadComponent} from './form-components/taak-document-upload/taak-document-upload.component';
import {RadioComponent} from './form-components/radio/radio.component';
import {MatLegacyRadioModule as MatRadioModule} from '@angular/material/legacy-radio';
import {ParagraphComponent} from './form-components/paragraph/paragraph.component';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from '@angular/material-moment-adapter';
import {MatLegacyProgressSpinnerModule as MatProgressSpinnerModule} from '@angular/material/legacy-progress-spinner';
import {MedewerkerGroepComponent} from './form-components/medewerker-groep/medewerker-groep.component';
import {DividerComponent} from './form-components/divider/divider.component';
import {MatDividerModule} from '@angular/material/divider';
import {HiddenComponent} from './form-components/hidden/hidden.component';
import {HtmlEditorComponent} from './form-components/html-editor/html-editor.component';
import {NgxEditorModule} from 'ngx-editor';
import {HtmlEditorVariabelenKiesMenuComponent} from './form-components/html-editor/html-editor-variabelen-kies-menu.component';
import {MatLegacyMenuModule as MatMenuModule} from '@angular/material/legacy-menu';

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
        TaakDocumentUploadComponent,
        RadioComponent,
        ParagraphComponent,
        DividerComponent,
        HiddenComponent
    ],
    imports: [
        CommonModule,
        HttpClientModule,
        HttpClientJsonpModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        FlexLayoutModule,
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
        MatMenuModule
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
        TaakDocumentUploadComponent,
        RadioComponent,
        ParagraphComponent
    ],
    providers: [
        {
            provide: DateAdapter,
            useClass: MomentDateAdapter,
            deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
        }, {
            provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
            useValue: {
                strict: false
            }
        },
        {
            provide: MAT_DATE_FORMATS,
            useValue: {
                parse: {
                    dateInput: 'DD-MM-YYYY'
                },
                display: {
                    dateInput: 'DD-MM-YYYY',
                    monthYearLabel: 'MMMM YYYY',
                    dateA11yLabel: 'LL',
                    monthYearA11yLabel: 'MMMM YYYY'
                }
            }
        }
    ]
})
export class MaterialFormBuilderModule {

    static forRoot(config?: MaterialFormBuilderConfig): ModuleWithProviders<MaterialFormBuilderModule> {
        return {
            ngModule: MaterialFormBuilderModule,
            providers: [
                {provide: BUILDER_CONFIG, useValue: config}
            ]
        };
    }
}
