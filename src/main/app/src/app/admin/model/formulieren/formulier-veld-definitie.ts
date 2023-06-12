/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormulierVeldType} from './formulier-veld-type.enum';
import {FormControl, FormGroup, Validators} from '@angular/forms';

export class FormulierVeldDefinitie {
    id: number;
    systeemnaam: string;
    volgorde: number;
    label: string;
    veldType: FormulierVeldType;
    beschrijving: string;
    helptekst: string;
    verplicht: boolean;
    defaultWaarde: string;
    meerkeuzeOpties: string;
    validaties: string[];

    static asFormGroup(vd: FormulierVeldDefinitie): FormGroup {
        return new FormGroup({
            id: new FormControl(vd.id),
            label: new FormControl(vd.label, Validators.required),
            systeemnaam: new FormControl(vd.systeemnaam, Validators.required),
            beschrijving: new FormControl(vd.beschrijving),
            helptekst: new FormControl(vd.helptekst),
            veldType: new FormControl(vd.veldType, Validators.required),
            defaultWaarde: new FormControl(vd.defaultWaarde),
            verplicht: new FormControl(!!vd.verplicht),
            meerkeuzeOpties: new FormControl({
                value: vd.meerkeuzeOpties,
                disabled: !this.isMeerkeuzeVeld(vd.veldType)
            }),
            volgorde: new FormControl(vd.volgorde, Validators.required)
        });
    }

    static isMeerkeuzeVeld(veldType: FormulierVeldType) {
        return veldType === FormulierVeldType.CHECKBOXES || veldType === FormulierVeldType.RADIO || veldType === FormulierVeldType.KEUZELIJST;
    }
}
