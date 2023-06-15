/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormulierVeldtype} from './formulier-veld-type.enum';
import {FormControl, FormGroup, Validators} from '@angular/forms';

export class FormulierVeldDefinitie {
    id: number;
    systeemnaam: string;
    volgorde: number;
    label: string;
    veldtype: FormulierVeldtype;
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
            veldtype: new FormControl(vd.veldtype, Validators.required),
            defaultWaarde: new FormControl(vd.defaultWaarde),
            verplicht: new FormControl(!!vd.verplicht),
            meerkeuzeOpties: new FormControl({
                value: vd.meerkeuzeOpties,
                disabled: !this.isMeerkeuzeVeld(vd.veldtype)
            }, Validators.required),
            volgorde: new FormControl(vd.volgorde, Validators.required)
        });
    }

    static isMeerkeuzeVeld(veldtype: FormulierVeldtype) {
        return veldtype === FormulierVeldtype.CHECKBOXES ||
                veldtype === FormulierVeldtype.RADIO ||
                veldtype === FormulierVeldtype.KEUZELIJST ||
                veldtype === FormulierVeldtype.DOCUMENTEN_LIJST;
    }

    static isFataldatum(fvd: FormulierVeldDefinitie) {
        return fvd.veldtype === FormulierVeldtype.DATUM && fvd.systeemnaam === 'fatale-datum';
    }

    static isOpschorten(fvd: FormulierVeldDefinitie) {
        return fvd.veldtype === FormulierVeldtype.CHECKBOX && fvd.systeemnaam === 'zaak-opschorten';
    }

    static isHervatten(fvd: FormulierVeldDefinitie) {
        return fvd.veldtype === FormulierVeldtype.CHECKBOX && fvd.systeemnaam === 'zaak-hervatten';
    }

    static isToekenningGroep(fvd: FormulierVeldDefinitie) {
        return fvd.veldtype === FormulierVeldtype.GROEP_KEUZELIJST && fvd.systeemnaam === 'toekenning-groep';
    }

    static isToekenningBehandelaar(fvd: FormulierVeldDefinitie) {
        return fvd.veldtype === FormulierVeldtype.MEDEWERKER_KEUZELIJST && fvd.systeemnaam === 'toekenning-behandelaar';
    }

    static isOndertekenen(fvd: FormulierVeldDefinitie) { // zou ook een eigen veldtype kunnen zijn
        return fvd.veldtype === FormulierVeldtype.DOCUMENTEN_LIJST && fvd.systeemnaam === 'ondertekenen';
    }
}
