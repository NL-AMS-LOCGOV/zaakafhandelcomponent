import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {ZaakFormulierenService} from '../../formulieren/zaken/zaak-formulieren.service';
import {Zaak} from '../model/zaak';
import {AbstractZaakFormulier} from '../../formulieren/zaken/abstract-zaak-formulier';
import {ZakenService} from '../zaken.service';
import {MatDrawer} from '@angular/material/sidenav';

@Component({
    selector: 'zac-zaakdata',
    templateUrl: './zaakdata.component.html',
    styleUrls: ['./zaakdata.component.less']
})
export class ZaakdataComponent implements OnInit {

    @Input() zaak: Zaak;
    @Input() sideNav: MatDrawer;
    @Output() done = new EventEmitter<void>();

    private formulier: AbstractZaakFormulier;
    formConfig: FormConfig;
    formItems: Array<AbstractFormField[]> = [];

    constructor(private zaakFormulierenService: ZaakFormulierenService, private zakenService: ZakenService) { }

    ngOnInit(): void {
        this.formConfig = new FormConfigBuilder().saveText('actie.opslaan').cancelText('actie.annuleren').build();

        this.formulier = this.zaakFormulierenService.getFormulierBuilder(this.zaak.zaaktype.identificatie)
                             .form(this.zaak).build();
        this.formItems = this.formulier.form;
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.zakenService.updateZaakdata(this.formulier.getZaak(formGroup)).subscribe(() => {
                this.done.emit();
            });
        } else {// cancel button clicked
            this.done.emit();
        }
    }
}
