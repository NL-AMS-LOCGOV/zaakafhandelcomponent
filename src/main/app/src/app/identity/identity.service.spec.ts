/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {IdentityService} from './identity.service';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Groep} from './model/groep';
import {of} from 'rxjs';
import {Medewerker} from './model/medewerker';
import {TestBed} from '@angular/core/testing';
import {HttpClient, HttpHandler} from '@angular/common/http';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';

describe('IdentityService', () => {
    let service: IdentityService;
    let mockHttpClient;
    let mockFoutAfhandelingService;
    let mockRouter;
    let mockSnackbar;
    let mockTranslate;

    const groepenJaap: Groep[] = [{id: 'groepa', naam: 'GroepA'}, {id: 'groepb', naam: 'GroepB'}];
    const jaap: Medewerker = {gebruikersnaam: 'jaap@mail.com', naam: 'Jaap Doe', groepen: groepenJaap};

    beforeEach(() => {
        mockRouter = jasmine.createSpyObj(['navigate']);
        mockSnackbar = jasmine.createSpyObj(['open']);
        mockFoutAfhandelingService = new FoutAfhandelingService(mockRouter, mockSnackbar, mockTranslate);

        TestBed.configureTestingModule({
            providers: [
                HttpClient, HttpHandler,
                {provide: FoutAfhandelingService, useValue: mockFoutAfhandelingService}
            ]
        }).compileComponents();
        service = TestBed.inject(IdentityService);
        mockHttpClient = TestBed.inject(HttpClient);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should return groups groepa and groepb', () => {
        const groepen: Groep[] = [{id: 'groepa', naam: 'GroepA'}, {id: 'groepb', naam: 'GroepB'}];

        spyOn(mockHttpClient, 'get').and.returnValue(of(groepen));

        service.listGroepen().subscribe(response => {
            expect(response).toEqual(groepen);
        });
    });
    it('should return users of groepa', () => {
        const groepenKees: Groep[] = [{id: 'admin', naam: 'Admin'}, {id: 'groepb', naam: 'GroepB'}];
        const kees: Medewerker = {gebruikersnaam: 'kees@mail.com', naam: 'Kees Seek', groepen: groepenKees};
        const medewerkers: Medewerker[] = [];
        medewerkers.push(jaap);
        medewerkers.push(kees);

        spyOn(mockHttpClient, 'get').and.returnValue(of(medewerkers));

        service.listMedewerkersInGroep('groepb').subscribe(response => {
            expect(response).toEqual(medewerkers);
        });
    });

    it('should return user jaap@mail.com', () => {
        spyOn(SessionStorageUtil, 'getSessionStorage').and.returnValue(jaap);

        service.readIngelogdeMedewerker().subscribe(response => {
            expect(response).toEqual(jaap);
        });
        expect(SessionStorageUtil.getItem).toHaveBeenCalled();
    });

    it('should return user jaap@mail.com', () => {
        spyOn(SessionStorageUtil, 'getSessionStorage').and.returnValue(null);
        spyOn(mockHttpClient, 'get').and.returnValue(of(jaap));
        spyOn(SessionStorageUtil, 'setSessionStorage');

        service.readIngelogdeMedewerker().subscribe(response => {
            expect(response).toEqual(jaap);
        });
        expect(mockHttpClient.get).toHaveBeenCalled();
        expect(SessionStorageUtil.setItem).toHaveBeenCalled();
    });
});
