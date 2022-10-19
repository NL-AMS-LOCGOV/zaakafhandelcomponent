/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {forkJoin} from 'rxjs';
import {CaseDefinition} from '../model/case-definition';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {Group} from '../../identity/model/group';
import {IdentityService} from '../../identity/identity.service';
import {User} from '../../identity/model/user';
import {HumanTaskParameter} from '../model/human-task-parameter';
import {MatSelectChange} from '@angular/material/select';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {ZaakbeeindigParameter} from '../model/zaakbeeindig-parameter';
import {ZaakbeeindigReden} from '../model/zaakbeeindig-reden';
import {SelectionModel} from '@angular/cdk/collections';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {UserEventListenerParameter} from '../model/user-event-listener-parameter';
import {ZaaknietontvankelijkReden} from '../model/zaaknietontvankelijk-reden';
import {ZaaknietontvankelijkParameter} from '../model/zaaknietontvankelijk-parameter';
import {AdminComponent} from '../admin/admin.component';
import {Resultaattype} from '../../zaken/model/resultaattype';
import {ZaakStatusmailOptie} from '../../zaken/model/zaak-statusmail-optie';
import {ReferentieTabel} from '../model/referentie-tabel';
import {ReferentieTabelService} from '../referentie-tabel.service';
import {FormulierDefinitie} from '../model/formulier-definitie';
import {HumanTaskReferentieTabel} from '../model/human-task-referentie-tabel';
import {FormulierVeldDefinitie} from '../model/formulier-veld-definitie';

@Component({
    templateUrl: './parameter-edit.component.html',
    styleUrls: ['./parameter-edit.component.less']
})
export class ParameterEditComponent extends AdminComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    parameters: ZaakafhandelParameters;
    humanTaskParameters: HumanTaskParameter[] = [];
    userEventListenerParameters: UserEventListenerParameter[] = [];
    zaakbeeindigParameters: ZaakbeeindigParameter[] = [];
    selection = new SelectionModel<ZaakbeeindigParameter>(true);

    algemeenFormGroup: FormGroup;
    humanTasksFormGroup: FormGroup;
    userEventListenersFormGroup: FormGroup;
    zaakbeeindigFormGroup: FormGroup;

    caseDefinitionControl = new FormControl(null, [Validators.required]);
    groepControl = new FormControl(null, [Validators.required]);
    behandelaarControl = new FormControl();
    einddatumGeplandWaarschuwingControl = new FormControl();
    uiterlijkeEinddatumAfdoeningWaarschuwingControl = new FormControl();
    intakeMailControl = new FormControl(null, [Validators.required]);
    afrondenMailControl = new FormControl(null, [Validators.required]);
    productaanvraagtypeControl = new FormControl();
    mailOpties: { label: string, value: string }[];

    caseDefinitions: CaseDefinition[];
    groepen: Group[];
    medewerkers: User[];
    resultaattypes: Resultaattype[];
    referentieTabellen: ReferentieTabel[];
    formulierDefinities: FormulierDefinitie[];

    constructor(public utilService: UtilService, public adminService: ZaakafhandelParametersService, private identityService: IdentityService,
                private route: ActivatedRoute, private formBuilder: FormBuilder, private referentieTabelService: ReferentieTabelService) {
        super(utilService);
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.userEventListenerParameters = this.parameters.userEventListenerParameters;
            this.humanTaskParameters = this.parameters.humanTaskParameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
            this.groepControl.setValue(this.parameters.defaultGroepId);
            this.behandelaarControl.setValue(this.parameters.defaultBehandelaarId);
            this.einddatumGeplandWaarschuwingControl.setValue(this.parameters.einddatumGeplandWaarschuwing);
            this.uiterlijkeEinddatumAfdoeningWaarschuwingControl.setValue(this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing);
            this.intakeMailControl.setValue(this.parameters.intakeMail);
            this.afrondenMailControl.setValue(this.parameters.afrondenMail);
            this.productaanvraagtypeControl.setValue(this.parameters.productaanvraagtype);
            adminService.listResultaattypes(this.parameters.zaaktype.uuid).subscribe(resultaattypes => this.resultaattypes = resultaattypes);
        });
        identityService.listGroups().subscribe(groepen => this.groepen = groepen);
        identityService.listUsers().subscribe(medewerkers => this.medewerkers = medewerkers);
        forkJoin([
            this.adminService.listCaseDefinitions(),
            this.adminService.listFormulierDefinities(),
            this.referentieTabelService.listReferentieTabellen()
        ]).subscribe(([caseDefinitions, formulierDefinities, referentieTabellen]) => {
            this.caseDefinitions = caseDefinitions;
            this.formulierDefinities = formulierDefinities;
            this.referentieTabellen = referentieTabellen;
            this.createForm();
        });
    }

    ngOnInit(): void {
        this.mailOpties = this.utilService.getEnumAsSelectList('statusmail.optie', ZaakStatusmailOptie);
        this.setupMenu('title.parameters.wijzigen');
    }

    caseDefinitionChanged(event: MatSelectChange): void {
        this.readHumanTaskParameters(event.value);
        this.readUserEventListenerParameters(event.value);
    }

    private readHumanTaskParameters(caseDefinition: CaseDefinition): void {
        this.humanTaskParameters = [];
        this.caseDefinitions.find(cd => cd.key === caseDefinition.key).humanTaskDefinitions.forEach(humanTaskDefinition => {
            const humanTaskParameter: HumanTaskParameter = new HumanTaskParameter();
            humanTaskParameter.planItemDefinition = humanTaskDefinition;
            humanTaskParameter.defaultGroepId = this.parameters.defaultGroepId;
            humanTaskParameter.formulierDefinitieId = humanTaskDefinition.defaultFormulierDefinitie;
            humanTaskParameter.referentieTabellen = [];
            this.humanTaskParameters.push(humanTaskParameter);
        });
        this.createHumanTasksForm();
    }

    private readUserEventListenerParameters(caseDefinition: CaseDefinition): void {
        this.userEventListenerParameters = [];
        this.caseDefinitions.find(cd => cd.key === caseDefinition.key).userEventListenerDefinitions.forEach(userEventListenerDefinition => {
            const userEventListenerParameter: UserEventListenerParameter = new UserEventListenerParameter();
            userEventListenerParameter.id = userEventListenerDefinition.id;
            userEventListenerParameter.naam = userEventListenerDefinition.naam;
            this.userEventListenerParameters.push(userEventListenerParameter);
        });
        this.createUserEventListenerForm();
    }

    getHumanTaskControl(parameter: HumanTaskParameter, field: string): FormControl {
        const formGroup: FormGroup = this.humanTasksFormGroup.get(parameter.planItemDefinition.id) as FormGroup;
        return formGroup.get(field) as FormControl;
    }

    createForm() {
        this.algemeenFormGroup = this.formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl,
            einddatumGeplandWaarschuwingControl: this.einddatumGeplandWaarschuwingControl,
            uiterlijkeEinddatumAfdoeningWaarschuwingControl: this.uiterlijkeEinddatumAfdoeningWaarschuwingControl,
            intakeMailControl: this.intakeMailControl,
            afrondenMailControl: this.afrondenMailControl,
            productaanvraagtypeControl: this.productaanvraagtypeControl
        });
        this.createHumanTasksForm();
        this.createUserEventListenerForm();
        this.createZaakbeeindigForm();
    }

    isHumanTaskParameterValid(humanTaskParameter: HumanTaskParameter): boolean {
        if (!this.getHumanTaskControl(humanTaskParameter, 'defaultGroep').valid ||
            !this.getHumanTaskControl(humanTaskParameter, 'doorlooptijd').valid) {
            return false;
        }
        if (humanTaskParameter.formulierDefinitieId) {
            for (const veld of this.getVeldDefinities(humanTaskParameter.formulierDefinitieId)) {
                if (!this.getHumanTaskControl(humanTaskParameter, 'referentieTabel' + veld.naam).valid) {
                    return false;
                }
            }
        }
        return true;
    }

    getActieControl(parameter: UserEventListenerParameter, field: string): FormControl {
        return this.userEventListenersFormGroup.get(parameter.id).get(field) as FormControl;
    }

    private createHumanTasksForm() {
        this.humanTasksFormGroup = this.formBuilder.group({});
        this.humanTaskParameters.forEach(parameter => {
            this.humanTasksFormGroup.addControl(parameter.planItemDefinition.id, this.getHumanTaskFormGroup(parameter));
        });
    }

    private getHumanTaskFormGroup(humanTaskParameters: HumanTaskParameter): FormGroup {
        const humanTaskFormGroup = this.formBuilder.group({});
        humanTaskFormGroup.addControl('formulierDefinitie', new FormControl(humanTaskParameters.formulierDefinitieId, [Validators.required]));
        humanTaskFormGroup.addControl('defaultGroep', new FormControl(humanTaskParameters.defaultGroepId));
        humanTaskFormGroup.addControl('doorlooptijd', new FormControl(humanTaskParameters.doorlooptijd, [Validators.min(0)]));
        if (humanTaskParameters.formulierDefinitieId) {
            for (const veld of this.getVeldDefinities(humanTaskParameters.formulierDefinitieId)) {
                humanTaskFormGroup.addControl('referentieTabel' + veld.naam,
                    new FormControl(this.getReferentieTabel(humanTaskParameters, veld), [Validators.required]));
            }
        }
        return humanTaskFormGroup;
    }

    private getReferentieTabel(humanTaskParameters: HumanTaskParameter, veld: FormulierVeldDefinitie): ReferentieTabel {
        const humanTaskReferentieTabel: HumanTaskReferentieTabel = humanTaskParameters.referentieTabellen.find(r => r.veld = veld.naam);
        return humanTaskReferentieTabel != null ? humanTaskReferentieTabel.tabel : this.referentieTabellen.find(r => r.code = veld.naam);
    }

    private createUserEventListenerForm() {
        this.userEventListenersFormGroup = this.formBuilder.group({});
        this.userEventListenerParameters.forEach(parameter => {
            const formGroup = this.formBuilder.group({});
            formGroup.addControl('toelichting', new FormControl(parameter.toelichting));
            this.userEventListenersFormGroup.addControl(parameter.id, formGroup);
        });
    }

    createZaakbeeindigForm() {
        this.zaakbeeindigFormGroup = this.formBuilder.group({});
        this.readZaaknietontvankelijkParameter(this.parameters);
        this.adminService.listZaakbeeindigRedenen().subscribe(redenen => {
            this.readZaakbeeindigRedenen(redenen);
        });
    }

    readZaaknietontvankelijkParameter(zaakafhandelParameters: ZaakafhandelParameters): void {
        this.addZaakbeeindigParameter(this.getZaaknietontvankelijkParameter(zaakafhandelParameters));
    }

    readZaakbeeindigRedenen(zaakbeeindigRedenen: ZaakbeeindigReden[]): void {
        for (const reden of zaakbeeindigRedenen) {
            this.addZaakbeeindigParameter(this.getZaakbeeindigParameter(reden));
        }
    }

    isZaaknietontvankelijkParameter(parameter): boolean {
        return ZaaknietontvankelijkReden.is(parameter.zaakbeeindigReden);
    }

    private addZaakbeeindigParameter(parameter: ZaakbeeindigParameter): void {
        this.zaakbeeindigParameters.push(parameter);
        this.zaakbeeindigFormGroup.addControl(parameter.zaakbeeindigReden.id + '__beeindigResultaat', new FormControl(parameter.resultaattype));
        this.updateZaakbeeindigForm(parameter);
    }

    private getZaaknietontvankelijkParameter(zaakafhandelParameters: ZaakafhandelParameters): ZaaknietontvankelijkParameter {
        const parameter: ZaaknietontvankelijkParameter = new ZaaknietontvankelijkParameter();
        parameter.resultaattype = zaakafhandelParameters.zaakNietOntvankelijkResultaattype;
        this.selection.select(parameter);
        return parameter;
    }

    private getZaakbeeindigParameter(reden: ZaakbeeindigReden): ZaakbeeindigParameter {
        let parameter: ZaakbeeindigParameter = null;
        for (const item of this.parameters.zaakbeeindigParameters) {
            if (this.compareObject(item.zaakbeeindigReden, reden)) {
                parameter = item;
                this.selection.select(parameter);
                break;
            }
        }
        if (parameter === null) {
            parameter = new ZaakbeeindigParameter();
            parameter.zaakbeeindigReden = reden;
        }
        return parameter;
    }

    updateZaakbeeindigForm(parameter: ZaakbeeindigParameter) {
        const control: FormControl = this.getZaakbeeindigControl(parameter, 'beeindigResultaat');
        if (this.selection.isSelected(parameter)) {
            control.addValidators([Validators.required]);
        } else {
            control.clearValidators();
        }
        control.updateValueAndValidity({emitEvent: false});
    }

    changeSelection($event: MatCheckboxChange, parameter: ZaakbeeindigParameter): void {
        if ($event) {
            this.selection.toggle(parameter);
            this.updateZaakbeeindigForm(parameter);
        }
    }

    getZaakbeeindigControl(parameter: ZaakbeeindigParameter, field: string): FormControl {
        return this.zaakbeeindigFormGroup.get(`${parameter.zaakbeeindigReden.id}__${field}`) as FormControl;
    }

    isValid(): boolean {
        return this.algemeenFormGroup.valid &&
            this.humanTasksFormGroup.valid &&
            this.zaakbeeindigFormGroup.valid;
    }

    opslaan(): void {
        this.parameters.caseDefinition = this.caseDefinitionControl.value;
        this.parameters.defaultGroepId = this.groepControl.value;
        this.parameters.defaultBehandelaarId = this.behandelaarControl.value;
        this.parameters.einddatumGeplandWaarschuwing = this.einddatumGeplandWaarschuwingControl.value;
        this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing = this.uiterlijkeEinddatumAfdoeningWaarschuwingControl.value;
        this.parameters.intakeMail = this.intakeMailControl.value;
        this.parameters.afrondenMail = this.afrondenMailControl.value;
        this.parameters.productaanvraagtype = this.productaanvraagtypeControl.value;

        this.humanTaskParameters.forEach(param => {
            param.formulierDefinitieId = this.getHumanTaskControl(param, 'formulierDefinitie').value;
            param.defaultGroepId = this.getHumanTaskControl(param, 'defaultGroep').value;
            param.doorlooptijd = this.getHumanTaskControl(param, 'doorlooptijd').value;
            const old = this.parameters.humanTaskParameters.find(htp => htp.planItemDefinition.id === param.planItemDefinition.id).referentieTabellen;
            param.referentieTabellen = [];
            this.getVeldDefinities(param.formulierDefinitieId).forEach(value => {
                const oldHumanTaskReferentieTabel: HumanTaskReferentieTabel = old.find(o => o.veld === value.naam);
                const tabel = oldHumanTaskReferentieTabel != null ? oldHumanTaskReferentieTabel : new HumanTaskReferentieTabel();
                tabel.veld = value.naam;
                tabel.tabel = this.getHumanTaskControl(param, 'referentieTabel' + tabel.veld).value;
                param.referentieTabellen.push(tabel);
            });
        });
        this.parameters.humanTaskParameters = this.humanTaskParameters;

        this.userEventListenerParameters.forEach(param => {
            param.toelichting = this.getActieControl(param, 'toelichting').value;
        });
        this.parameters.userEventListenerParameters = this.userEventListenerParameters;

        this.parameters.zaakbeeindigParameters = [];
        this.selection.selected.forEach(param => {
            if (this.isZaaknietontvankelijkParameter(param)) {
                this.parameters.zaakNietOntvankelijkResultaattype = this.getZaakbeeindigControl(param, 'beeindigResultaat').value;
            } else {
                param.resultaattype = this.getZaakbeeindigControl(param, 'beeindigResultaat').value;
                this.parameters.zaakbeeindigParameters.push(param);
            }
        });

        this.adminService.updateZaakafhandelparameters(this.parameters).subscribe(data => {
            this.utilService.openSnackbar('msg.zaakafhandelparameters.opgeslagen');
            this.parameters = data;
        });
    }

    compareObject(object1: any, object2: any): boolean {
        if (typeof object1 === 'string') {
            return object1 === object2;
        }
        if (object1 && object2) {
            if (object1.hasOwnProperty('key')) {
                return object1.key === object2.key;
            } else if (object1.hasOwnProperty('id')) {
                return object1.id === object2.id;
            } else if (object1.hasOwnProperty('naam')) {
                return object1.naam === object2.naam;
            } else if (object1.hasOwnProperty('name')) {
                return object1.name === object2.name;
            }
            return object1 === object2;
        }
        return false;
    }

    formulierDefinitieChanged($event: MatSelectChange, humanTaskParameter: HumanTaskParameter): void {
        humanTaskParameter.formulierDefinitieId = $event.value;
        this.humanTasksFormGroup.setControl(humanTaskParameter.planItemDefinition.id, this.getHumanTaskFormGroup(humanTaskParameter));
    }

    getVeldDefinities(formulierDefinitieId: string): FormulierVeldDefinitie[] {
        return this.formulierDefinities.find(f => f.id === formulierDefinitieId).veldDefinities;
    }
}
