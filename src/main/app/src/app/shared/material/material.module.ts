/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {MatLegacyPaginatorModule as MatPaginatorModule} from '@angular/material/legacy-paginator';
import {MatIconModule} from '@angular/material/icon';
import {MatLegacyInputModule as MatInputModule} from '@angular/material/legacy-input';
import {MatLegacyFormFieldModule as MatFormFieldModule} from '@angular/material/legacy-form-field';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';
import {MatLegacyTabsModule as MatTabsModule} from '@angular/material/legacy-tabs';
import {MatSortModule} from '@angular/material/sort';
import {MatLegacyProgressBarModule as MatProgressBarModule} from '@angular/material/legacy-progress-bar';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatLegacyButtonModule as MatButtonModule} from '@angular/material/legacy-button';
import {MatLegacyCheckboxModule as MatCheckboxModule} from '@angular/material/legacy-checkbox';
import {
    MAT_LEGACY_SNACK_BAR_DEFAULT_OPTIONS as MAT_SNACK_BAR_DEFAULT_OPTIONS,
    MatLegacySnackBarModule as MatSnackBarModule
} from '@angular/material/legacy-snack-bar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatLegacyListModule as MatListModule} from '@angular/material/legacy-list';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatLegacySelectModule as MatSelectModule} from '@angular/material/legacy-select';
import {MatLegacyMenuModule as MatMenuModule} from '@angular/material/legacy-menu';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {MatLegacyRadioModule as MatRadioModule} from '@angular/material/legacy-radio';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';
import {MatBadgeModule} from '@angular/material/badge';
import {MatLegacyDialogModule as MatDialogModule} from '@angular/material/legacy-dialog';
import {MatLegacyProgressSpinnerModule as MatProgressSpinnerModule} from '@angular/material/legacy-progress-spinner';
import {MatLegacyTooltipModule as MatTooltipModule} from '@angular/material/legacy-tooltip';
import {MatLegacyAutocompleteModule as MatAutocompleteModule} from '@angular/material/legacy-autocomplete';
import {ReactiveFormsModule} from '@angular/forms';
import {MatStepperModule} from '@angular/material/stepper';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';
import {MatDatepickerModule} from '@angular/material/datepicker';

@NgModule({
    exports: [
        MatPaginatorModule,
        MatIconModule,
        MatInputModule,
        MatSelectModule,
        MatFormFieldModule,
        MatTableModule,
        MatTabsModule,
        MatSortModule,
        MatProgressBarModule,
        MatToolbarModule,
        MatButtonModule,
        MatCheckboxModule,
        MatMenuModule,
        MatSidenavModule,
        MatListModule,
        MatExpansionModule,
        MatSnackBarModule,
        MatCardModule,
        MatRadioModule,
        MatSlideToggleModule,
        MatStepperModule,
        MatBadgeModule,
        MatDialogModule,
        MatBottomSheetModule,
        MatProgressSpinnerModule,
        MatTooltipModule,
        MatAutocompleteModule,
        ReactiveFormsModule,
        MatChipsModule,
        MatDatepickerModule
    ],
    providers: [{
        provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: {
            verticalPosition: 'top'
        }
    }
    ]
})
export class MaterialModule {
}
