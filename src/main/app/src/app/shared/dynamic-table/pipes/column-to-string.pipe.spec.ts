/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ColumnToStringPipe} from './column-to-string.pipe';

describe('ColumnToStringPipe', () => {
    it('create an instance', () => {
        const pipe = new ColumnToStringPipe();
        expect(pipe).toBeTruthy();
    });
});
