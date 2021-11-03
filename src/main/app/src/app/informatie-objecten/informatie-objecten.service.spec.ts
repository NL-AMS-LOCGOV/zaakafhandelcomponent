/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {InformatieObjectenService} from './informatie-objecten.service';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';

describe('InformatieObjectService', () => {
    let service: InformatieObjectenService;
    let mockHttpClient;
    let mockFoutAfhandelingService;
    let mockRouter;
    let mockSnackbar;
    let mockTranslate;

    beforeEach(() => {
        mockHttpClient = jasmine.createSpyObj(['get', 'put']);
        mockRouter = jasmine.createSpyObj(['navigate']);
        mockSnackbar = jasmine.createSpyObj(['open']);
        mockFoutAfhandelingService = new FoutAfhandelingService(mockRouter, mockSnackbar, mockTranslate);

        service = new InformatieObjectenService(mockHttpClient, mockFoutAfhandelingService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
