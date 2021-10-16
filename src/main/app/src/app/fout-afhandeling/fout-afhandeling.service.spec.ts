/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FoutAfhandelingService} from './fout-afhandeling.service';

describe('FoutAfhandelingService', () => {
    let service: FoutAfhandelingService;
    let mockRouter;
    let mockSnackbar;

    beforeEach(() => {
        mockRouter = jasmine.createSpyObj(['navigate']);
        mockSnackbar = jasmine.createSpyObj(['open']);

        service = new FoutAfhandelingService(mockRouter, mockSnackbar);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
