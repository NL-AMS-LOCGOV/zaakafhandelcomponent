import {TestBed} from '@angular/core/testing';

import {SignaleringenService} from './signaleringen.service';

describe('SignaleringenService', () => {
    let service: SignaleringenService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(SignaleringenService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
