/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ActivatedRoute} from '@angular/router';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
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
    humanTaskFormGroup: FormGroup;
    UserEventListenerFormGroup: FormGroup;
    zaakbeeindigFormGroup: FormGroup;

    caseDefinitionControl = new FormControl(null, [Validators.required]);
    groepControl = new FormControl(null, [Validators.required]);
    behandelaarControl = new FormControl();
    einddatumGeplandWaarschuwingControl = new FormControl();
    uiterlijkeEinddatumAfdoeningWaarschuwingControl = new FormControl();
    intakeMailControl = new FormControl();
    afrondenMailControl = new FormControl();
    mailOpties: { label: string, value: string }[];

    caseDefinitions: Observable<CaseDefinition[]>;
    groepen: Observable<Group[]>;
    medewerkers: Observable<User[]>;
    resultaattypes: Observable<Resultaattype[]>;
    referentieTabellen: Observable<ReferentieTabel[]>;

    constructor(public utilService: UtilService, public adminService: ZaakafhandelParametersService, private identityService: IdentityService,
                private route: ActivatedRoute, private formBuilder: FormBuilder, private referentieTabelService: ReferentieTabelService) {
        super(utilService);
        this.route.data.subscribe(data => {
            this.parameters = data.parameters;
            this.userEventListenerParameters = this.parameters.userEventListenerParameters;
            this.humanTaskParameters = this.parameters.humanTaskParameters;
            this.caseDefinitionControl.setValue(this.parameters.caseDefinition);
            this.groepControl.setValue(this.parameters.defaultGroep);
            this.behandelaarControl.setValue(this.parameters.defaultBehandelaar);
            this.einddatumGeplandWaarschuwingControl.setValue(this.parameters.einddatumGeplandWaarschuwing);
            this.uiterlijkeEinddatumAfdoeningWaarschuwingControl.setValue(this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing);
            this.intakeMailControl.setValue(this.parameters.intakeMail);
            this.afrondenMailControl.setValue(this.parameters.afrondenMail);
            this.resultaattypes = adminService.listResultaattypes(this.parameters.zaaktype.uuid);
        });
        this.caseDefinitions = adminService.listCaseDefinitions();
        this.groepen = identityService.listGroups();
        this.medewerkers = identityService.listUsers();
        this.referentieTabellen = referentieTabelService.listReferentieTabellen();
    }

    ngOnInit(): void {
        this.mailOpties = this.utilService.getEnumAsSelectList('statusmail.optie', ZaakStatusmailOptie);
        this.setupMenu('title.parameters.wijzigen');
        this.createForm();
    }

    readPlanItemDefinitions(event: MatSelectChange): void {
        this.readHumanTaskParameters(event);
        this.readUserEventListenerParameters(event);
    }

    private readHumanTaskParameters(event: MatSelectChange): void {
        this.humanTaskParameters = [];
        const changedCaseDefinition = event.value;
        if (this.compareObject(changedCaseDefinition, this.parameters.caseDefinition)) {
            this.humanTaskParameters = this.parameters.humanTaskParameters;
            this.updateHumanTaskForm();
        } else {
            this.adminService.readCaseDefinition(changedCaseDefinition.key).subscribe(caseDefinition => {
                caseDefinition.humanTaskDefinitions.forEach(humanTaskDefinition => {
                    const humanTaskParameter: HumanTaskParameter = new HumanTaskParameter();
                    humanTaskParameter.planItemDefinition = humanTaskDefinition;
                    humanTaskParameter.defaultGroep = this.parameters.defaultGroep;
                    humanTaskParameter.referentieTabellen = humanTaskDefinition.referentieTabellen;
                    this.humanTaskParameters.push(humanTaskParameter);
                });
                this.updateHumanTaskForm();
            });
        }
    }

    private readUserEventListenerParameters(event: MatSelectChange): void {
        this.userEventListenerParameters = [];
        const changedCaseDefinition = event.value;
        if (this.compareObject(changedCaseDefinition, this.parameters.caseDefinition)) {
            this.userEventListenerParameters = this.parameters.userEventListenerParameters;
            this.updateUserEventListenerForm();
        } else {
            this.adminService.readCaseDefinition(changedCaseDefinition.key).subscribe(caseDefinition => {
                caseDefinition.userEventListenerDefinitions.forEach(userEventListenerDefinition => {
                    const userEventListenerParameter: UserEventListenerParameter = new UserEventListenerParameter();
                    userEventListenerParameter.id = userEventListenerDefinition.id;
                    userEventListenerParameter.naam = userEventListenerDefinition.naam;
                    this.userEventListenerParameters.push(userEventListenerParameter);
                });
                this.updateUserEventListenerForm();
            });
        }
    }

    getHumanTaskControl(parameter: HumanTaskParameter, field: string): FormControl {
        return this.humanTaskFormGroup.get(`${parameter.planItemDefinition.id}__${field}`) as FormControl;
    }

    createForm() {
        this.algemeenFormGroup = this.formBuilder.group({
            caseDefinitionControl: this.caseDefinitionControl,
            groepControl: this.groepControl,
            behandelaarControl: this.behandelaarControl,
            einddatumGeplandWaarschuwingControl: this.einddatumGeplandWaarschuwingControl,
            uiterlijkeEinddatumAfdoeningWaarschuwingControl: this.uiterlijkeEinddatumAfdoeningWaarschuwingControl,
            intakeMailControl: this.intakeMailControl,
            afrondenMailControl: this.afrondenMailControl
        });
        this.updateHumanTaskForm();
        this.updateUserEventListenerForm();
        this.createZaakbeeindigForm();
    }

    isHumanTaskParameterValid(humanTaskParameter: HumanTaskParameter): boolean {
        if (!this.getHumanTaskControl(humanTaskParameter, 'defaultGroep').valid ||
            !this.getHumanTaskControl(humanTaskParameter, 'doorlooptijd').valid) {
            return false;
        }
        for (const tabel of humanTaskParameter.referentieTabellen) {
            if (!this.getHumanTaskControl(humanTaskParameter, 'referentieTabel' + tabel.veld).valid) {
                return false;
            }
        }
        return true;
    }

    getActieControl(parameter: UserEventListenerParameter, field: string): FormControl {
        return this.UserEventListenerFormGroup.get(`${parameter.id}__${field}`) as FormControl;
    }

    private updateHumanTaskForm() {
        this.humanTaskFormGroup = this.formBuilder.group({});
        this.humanTaskParameters.forEach(parameter => {
            this.humanTaskFormGroup.addControl(parameter.planItemDefinition.id + '__defaultGroep',
                new FormControl(parameter.defaultGroep));
            this.humanTaskFormGroup.addControl(parameter.planItemDefinition.id + '__doorlooptijd',
                new FormControl(parameter.doorlooptijd, [Validators.min(0)]));
            for (const item of parameter.referentieTabellen) {
                this.humanTaskFormGroup.addControl(parameter.planItemDefinition.id + '__referentieTabel' + item.veld,
                    new FormControl(item.tabel, [Validators.required]));
            }
        });
    }

    private updateUserEventListenerForm() {
        this.UserEventListenerFormGroup = this.formBuilder.group({});
        this.userEventListenerParameters.forEach(parameter => {
            this.UserEventListenerFormGroup.addControl(parameter.id + '__toelichting', new FormControl(parameter.toelichting));
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
            this.humanTaskFormGroup.valid &&
            this.zaakbeeindigFormGroup.valid;
    }

    opslaan(): void {
        this.parameters.caseDefinition = this.caseDefinitionControl.value;
        this.parameters.defaultGroep = this.groepControl.value;
        this.parameters.defaultBehandelaar = this.behandelaarControl.value;
        this.parameters.einddatumGeplandWaarschuwing = this.einddatumGeplandWaarschuwingControl.value;
        this.parameters.uiterlijkeEinddatumAfdoeningWaarschuwing = this.uiterlijkeEinddatumAfdoeningWaarschuwingControl.value;
        this.parameters.intakeMail = this.intakeMailControl.value;
        this.parameters.afrondenMail = this.afrondenMailControl.value;

        this.humanTaskParameters.forEach(param => {
            param.defaultGroep = this.getHumanTaskControl(param, 'defaultGroep').value;
            param.doorlooptijd = this.getHumanTaskControl(param, 'doorlooptijd').value;
            for (const tabel of param.referentieTabellen) {
                tabel.tabel = this.getHumanTaskControl(param, 'referentieTabel' + tabel.veld).value;
            }
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

        this.adminService.updateZaakafhandelparameters(this.parameters).subscribe(() => {
            this.utilService.openSnackbar('msg.zaakafhandelparameters.opgeslagen');
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
}
