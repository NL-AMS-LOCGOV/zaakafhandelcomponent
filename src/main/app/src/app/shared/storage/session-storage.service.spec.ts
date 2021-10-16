/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TestBed} from '@angular/core/testing';

import {SessionStorageService} from './session-storage.service';

describe('SessionStorageService', () => {
    let service: SessionStorageService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                SessionStorageService
            ]
        });
        service = TestBed.inject(SessionStorageService);
    });

    // afterEach(() => {
    //     service.clearSessionStorage();
    // });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should return a gebruikersnaam with naam Jaap', () => {
        service.setSessionStorage('gebruikersnaam', 'Jaap');

        const gebruiker = {gebruikersnaam: 'Jaap'};
        const gebruikerJSON = JSON.stringify(gebruiker);

        spyOn(sessionStorage, 'getItem').and.returnValue(gebruikerJSON);

        expect(service.getSessionStorage('gebruikersnaam')).toEqual(JSON.parse(gebruikerJSON));
        expect(sessionStorage.getItem).toHaveBeenCalled();
    });

    it('key value v should be equal to v', () => {
        expect(service.getSessionStorage('k', 'v')).toEqual('v');
    });

    it('should return Jaap', () => {
        spyOn(sessionStorage, 'setItem');

        const naam = service.setSessionStorage('gebruikersnaam', 'Jaap');

        expect(sessionStorage.setItem).toHaveBeenCalled();
        expect(naam).toBe('Jaap');
    });

    it('should break the reference', () => {
        spyOn(JSON, 'parse');
        spyOn(JSON, 'stringify');
        spyOn(sessionStorage, 'setItem');

        const gebruiker = service.getSessionStorage('', 'Jaap');

        expect(JSON.parse).toHaveBeenCalled();
        expect(JSON.stringify).toHaveBeenCalled();
        expect(sessionStorage.setItem).toHaveBeenCalled();

        const jaap = {gebruikersnaam: 'Jaap'};
        const gebruikerJSON = JSON.stringify(jaap);

        expect(gebruikerJSON).toEqual(gebruiker);
    });

    // TODO: ESUITEDEV-25310
    // session storage word momenteel niet leeggegooid. Echter, in de afterAll() gebeurt dit wel
    xit('should call clear', () => {
        spyOn(sessionStorage, 'clear');

        service.setSessionStorage('testWaarde', 'waarde');

        service.clearSessionStorage();

        expect(sessionStorage.clear).toHaveBeenCalled();

        expect(service.getSessionStorage('testWaarde')).toBe(undefined);
    });
});
