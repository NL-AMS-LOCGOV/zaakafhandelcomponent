/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {EmptyPipe} from './empty.pipe';
import {Medewerker} from '../../identity/model/medewerker';

describe('EmptyPipe', () => {
    let pipe;

    beforeEach(() => {
        pipe = new EmptyPipe();
    });

    it('create an instance', () => {
        expect(pipe).toBeTruthy();
    });

    it('should return -', () => {
        let medewerker = new Medewerker();

        expect(pipe.transform(medewerker.naam)).toBe('-');
    });

    it('should return Jaap', () => {
        let medewerker = {naam: 'Jaap'};

        expect(pipe.transform(medewerker.naam)).toBe('Jaap');
    });

    it('should return -', () => {
        expect(pipe.transform('')).toBe('-');
    });

    it('should return - because value does not contain existing args', () => {
        expect(pipe.transform('naam', 'Jaap')).toBe('-');
    });
});
