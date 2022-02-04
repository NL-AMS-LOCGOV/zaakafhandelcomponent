/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ModuleWithProviders, NgModule} from '@angular/core';
import {FormComponent} from './form/form/form.component';
import {FormFieldComponent} from './form/form-field/form-field.component';
import {InputComponent} from './form-components/input/input.component';
import {FormFieldDirective} from './form/form-field/form-field.directive';
import {ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';
import {MatFormFieldModule} from '@angular/material/form-field';
import {DateComponent} from './form-components/date/date.component';
import {HeadingComponent} from './form-components/heading/heading.component';
import {SelectComponent} from './form-components/select/select.component';
import {TextareaComponent} from './form-components/textarea/textarea.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {DateAdapter, MAT_DATE_LOCALE} from '@angular/material/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIconModule} from '@angular/material/icon';
import {MatSelectModule} from '@angular/material/select';
import {MatMomentDateModule, MomentDateAdapter} from '@angular/material-moment-adapter';
import {GoogleMapsComponent} from './form-components/google-maps/google-maps.component';
import {GoogleMapsModule} from '@angular/google-maps';
import {HttpClientJsonpModule, HttpClientModule} from '@angular/common/http';
import {ReadonlyComponent} from './form-components/readonly/readonly.component';
import {BUILDER_CONFIG, MaterialFormBuilderConfig} from './material-form-builder-config';
import {FileComponent} from './form-components/file/file.component';
import {TranslateModule} from '@ngx-translate/core';
import {AutocompleteComponent} from './form-components/autocomplete/autocomplete.component';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {PipesModule} from '../pipes/pipes.module';
import {CheckboxComponent} from './form-components/checkbox/checkbox.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {DocumentenLijstComponent} from './form-components/documenten-lijst/documenten-lijst.component';
import {MatTableModule} from '@angular/material/table';
import {RouterModule} from '@angular/router';
import {TaakDocumentUploadComponent} from './form-components/taak-document-upload/taak-document-upload.component';

@NgModule({
    declarations: [
        FormComponent,
        FormFieldComponent,
        DateComponent,
        HeadingComponent,
        InputComponent,
        FileComponent,
        SelectComponent,
        CheckboxComponent,
        TextareaComponent,
        GoogleMapsComponent,
        FormFieldDirective,
        ReadonlyComponent,
        AutocompleteComponent,
        DocumentenLijstComponent,
        TaakDocumentUploadComponent
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
        MatIconModule,
        MatButtonModule,
        MatSelectModule,
        MatCheckboxModule,
        MatMomentDateModule,
        MatDatepickerModule,
        MatAutocompleteModule,
        MatChipsModule,
        TranslateModule,
        PipesModule,
        MatTableModule,
        RouterModule
    ],
    exports: [
        FormComponent,
        FormFieldComponent,
        DateComponent,
        HeadingComponent,
        InputComponent,
        FileComponent,
        SelectComponent,
        CheckboxComponent,
        TextareaComponent,
        GoogleMapsComponent,
        AutocompleteComponent,
        DocumentenLijstComponent,
        TaakDocumentUploadComponent
    ],
    providers: [
        {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]}
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
