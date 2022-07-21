import {Klant} from './klant';
import {Roltype} from './roltype';

export class KlantGegevens {
    constructor(klant: Klant) {
        this.klant = klant;
    }

    klant: Klant;
    betrokkeneRoltype?: Roltype;
}
