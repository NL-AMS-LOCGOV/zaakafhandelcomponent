/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class TableColumnFilter<FILTER> {
    id: string;
    values: FILTER[];
    value: FILTER;
    viewValue: string;
    selectValue: string;

    /**
     * @param id filter id
     * @param values list of values for the select filter
     * @param viewValue if the values are an object this field will be used as a label
     * @param value the selected value
     */
    constructor(id: string, values: FILTER[], viewValue?: string, selectValue?: string, value?: FILTER) {
        this.id = id;
        this.values = values;
        this.value = value;
        this.viewValue = viewValue;
        this.selectValue = selectValue;
    }
}
