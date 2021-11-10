/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FormConfig {

    private readonly _saveButtonText: string;
    private readonly _cancelButtonText: string;

    constructor(saveButtonText: string, cancelButtonText?: string) {
        this._saveButtonText = saveButtonText;
        this._cancelButtonText = cancelButtonText;
    }

    get saveButtonText(): string {
        return this._saveButtonText;
    }

    get cancelButtonText(): string {
        return this._cancelButtonText;
    }

}
