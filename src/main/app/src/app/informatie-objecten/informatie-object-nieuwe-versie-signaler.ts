import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable()
export class InformatieObjectNieuweVersieSignaler {
    public isNieuweVersieBeschikbaar: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
}
