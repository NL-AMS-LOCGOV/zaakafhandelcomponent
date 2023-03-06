import {Injectable} from '@angular/core';
import {FormulierDefinitieID} from '../../admin/model/formulier-definitie';
import {ProcessFormulierenBuilder} from './process-formulieren-builder';

@Injectable({
    providedIn: 'root'
})
export class ProcessFormulierenService {

    constructor() { }

    public getFormulierBuilder(formulierDefinitie: FormulierDefinitieID): ProcessFormulierenBuilder {
        return null;
    }
}
