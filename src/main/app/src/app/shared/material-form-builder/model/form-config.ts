/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FormConfig {

    private readonly _saveButtonText: string;
    private readonly _cancelButtonText: string;

    private _saveButtonIcon: string;
    private _cancelButtonIcon: string;

    constructor(saveButtonText?: string, cancelButtonText?: string) {
        this._saveButtonText = saveButtonText;
        this._cancelButtonText = cancelButtonText;
    }

    get saveButtonText(): string {
        return this._saveButtonText;
    }

    get cancelButtonText(): string {
        return this._cancelButtonText;
    }

    set saveButtonIcon(value: string) {
        this._saveButtonIcon = value;
    }

    set cancelButtonIcon(value: string) {
        this._cancelButtonIcon = value;
    }

    get saveButtonIcon(): string {
        return this._saveButtonIcon;
    }

    get cancelButtonIcon(): string {
        return this._cancelButtonIcon;
    }
}
