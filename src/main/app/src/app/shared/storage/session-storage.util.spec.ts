/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SessionStorageUtil} from './session-storage.util';

describe('SessionStorageService', () => {

    afterEach(() => {
        SessionStorageUtil.clearSessionStorage();
    });

    it('should return a gebruikersnaam with naam Jaap', () => {

        const gebruiker = {gebruikersnaam: 'Jaap'};
        const gebruikerJSON = JSON.stringify(gebruiker);

        spyOn(sessionStorage, 'getItem').and.returnValue(gebruikerJSON);

        expect(SessionStorageUtil.getSessionStorage('gebruikersnaam')).toEqual(JSON.parse(gebruikerJSON));
        expect(sessionStorage.getItem).toHaveBeenCalled();
    });

    it('key value v should be equal to v', () => {
        expect(SessionStorageUtil.getSessionStorage('k', 'v')).toEqual('v');
    });

    it('should return Jaap', () => {
        const naam = SessionStorageUtil.setSessionStorage('gebruikersnaam', 'Jaap');
        expect(naam).toBe('Jaap');
    });

    it('should break the reference', () => {
        spyOn(JSON, 'parse');
        spyOn(JSON, 'stringify');
        spyOn(sessionStorage, 'setItem');

        const gebruiker = SessionStorageUtil.getSessionStorage('', 'Jaap');

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
        SessionStorageUtil.setSessionStorage('testWaarde', 'waarde');
        SessionStorageUtil.clearSessionStorage();
        expect(sessionStorage.clear).toHaveBeenCalled();
        expect(SessionStorageUtil.getSessionStorage('testWaarde')).toBe(undefined);
    });
});
