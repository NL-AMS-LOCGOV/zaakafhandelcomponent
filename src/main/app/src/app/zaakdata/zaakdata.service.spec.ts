/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TestBed} from '@angular/core/testing';

import {ZaakdataService} from './zaakdata.service';

describe('ZaakdataService', () => {
    let service: ZaakdataService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(ZaakdataService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
