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

    mailOpties: { label: string, value: string }[];

    caseDefinitions: CaseDefinition[];
    groepen: Group[];
    medewerkers: User[];
    resultaattypes: Resultaattype[];
    referentieTabellen: ReferentieTabel[];
    formulierDefinities: FormulierDefinitie[];
    zaakbeeindigRedenen: ZaakbeeindigReden[];
    loading: boolean;

    constructor(public utilService: UtilService, public adminService: ZaakafhandelParametersService, private identityService: IdentityService,
                private route: ActivatedRoute, private formBuilder: FormBuilder, private referentieTabelService: ReferentieTabelService) {
        super(utilService);
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.userEventListenerParameters = this.parameters.userEventListenerParameters;
            this.humanTaskParameters = this.parameters.humanTaskParameters;
            adminService.listResultaattypes(this.parameters.zaaktype.uuid).subscribe(resultaattypes => this.resultaattypes = resultaattypes);
            forkJoin([
                adminService.listCaseDefinitions(),
                adminService.listFormulierDefinities(),
                referentieTabelService.listReferentieTabellen(),
                identityService.listGroups(),
                identityService.listUsers(),
                this.adminService.listZaakbeeindigRedenen()
            ]).subscribe(([caseDefinitions, formulierDefinities, referentieTabellen, groepen, medewerkers, zaakbeeindigRedenen]) => {
                this.caseDefinitions = caseDefinitions;
                this.formulierDefinities = formulierDefinities;
                this.referentieTabellen = referentieTabellen;
                this.groepen = groepen;
                this.medewerkers = medewerkers;
                this.zaakbeeindigRedenen = zaakbeeindigRedenen;
                this.createForm();
            });
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
        const formGroup = this.humanTasksFormGroup.get(parameter.planItemDefinition.id) as FormGroup;
        return formGroup.get(field) as FormControl;
    }

    createForm() {
        this.algemeenFormGroup = this.formBuilder.group({
            caseDefinition: [this.parameters.caseDefinition, [Validators.required]],
            defaultGroepId: [this.parameters.defaultGroepId, [Validators.required]],
            defaultBehandelaarId: [this.parameters.defaultBehandelaarId],
            einddatumGeplandWaarschuwing: [this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing],
            uiterlijkeEinddatumAfdoeningWaarschuwing: [this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing],
            intakeMail: [this.parameters.intakeMail, [Validators.required]],
            afrondenMail: [this.parameters.afrondenMail, [Validators.required]],
            productaanvraagtype: [this.parameters.productaanvraagtype]
        });
        this.createHumanTasksForm();
        this.createUserEventListenerForm();
        this.createZaakbeeindigForm();
    }

    isHumanTaskParameterValid(humanTaskParameter: HumanTaskParameter): boolean {
        return this.humanTasksFormGroup.get(humanTaskParameter.planItemDefinition.id).status === 'VALID';
    }

    private createHumanTasksForm() {
        this.humanTasksFormGroup = this.formBuilder.group({});
        this.humanTaskParameters.forEach(parameter => {
            this.humanTasksFormGroup.addControl(parameter.planItemDefinition.id, this.getHumanTaskFormGroup(parameter));
        });
    }

    private getHumanTaskFormGroup(humanTaskParameters: HumanTaskParameter): FormGroup {
        const humanTaskFormGroup: FormGroup = this.formBuilder.group({
            formulierDefinitie: [humanTaskParameters.formulierDefinitieId, [Validators.required]],
            defaultGroep: [humanTaskParameters.defaultGroepId],
            doorlooptijd: [humanTaskParameters.doorlooptijd, [Validators.min(0)]],
            actief: [humanTaskParameters.actief]
        });

        if (humanTaskParameters.formulierDefinitieId) {
            for (const veld of this.getVeldDefinities(humanTaskParameters.formulierDefinitieId)) {
                humanTaskFormGroup.addControl('referentieTabel' + veld.naam,
                    this.formBuilder.control(this.getReferentieTabel(humanTaskParameters, veld), Validators.required));
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
            const formGroup = this.formBuilder.group({
                toelichting: [parameter.toelichting]
            });
            this.userEventListenersFormGroup.addControl(parameter.id, formGroup);
        });
    }

    createZaakbeeindigForm() {
        this.zaakbeeindigFormGroup = this.formBuilder.group({});
        this.addZaakbeeindigParameter(this.getZaaknietontvankelijkParameter(this.parameters));
        for (const reden of this.zaakbeeindigRedenen) {
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
        this.loading = true;
        Object.assign(this.parameters, this.algemeenFormGroup.value);
        this.humanTaskParameters.forEach(param => {
            param.formulierDefinitieId = this.getHumanTaskControl(param, 'formulierDefinitie').value;
            param.defaultGroepId = this.getHumanTaskControl(param, 'defaultGroep').value;
            param.actief = this.getHumanTaskControl(param, 'actief').value;
            param.doorlooptijd = this.getHumanTaskControl(param, 'doorlooptijd').value;
            const bestaandeHumanTaskParameter: HumanTaskParameter = this.parameters.humanTaskParameters.find(
                htp => htp.planItemDefinition.id === param.planItemDefinition.id);
            const bestaandeReferentietabellen = bestaandeHumanTaskParameter ? bestaandeHumanTaskParameter.referentieTabellen : [];
            param.referentieTabellen = [];
            this.getVeldDefinities(param.formulierDefinitieId).forEach(value => {
                const bestaandeHumanTaskReferentieTabel: HumanTaskReferentieTabel = bestaandeReferentietabellen.find(o => o.veld === value.naam);
                const tabel = bestaandeHumanTaskReferentieTabel != null ? bestaandeHumanTaskReferentieTabel : new HumanTaskReferentieTabel();
                tabel.veld = value.naam;
                tabel.tabel = this.getHumanTaskControl(param, 'referentieTabel' + tabel.veld).value;
                param.referentieTabellen.push(tabel);
            });
        });
        this.parameters.humanTaskParameters = this.humanTaskParameters;
        this.userEventListenerParameters.forEach(param => {
            param.toelichting = this.userEventListenersFormGroup.get(param.id).get('toelichting').value;
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
            this.loading = false;
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
