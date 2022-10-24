/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {CsvService} from '../csv.service';
import {UtilService} from '../../core/service/util.service';

@Component({
    selector: 'zac-csv-export-button[zoekParameters][filename]',
    templateUrl: './csv-export-button.component.html',
    styleUrls: ['./csv-export-button.component.less']
})
export class CsvExportButtonComponent {

    @Input() zoekParameters: ZoekParameters;
    @Input() filename: string;

    constructor(private csvService: CsvService, private utilService: UtilService) {}

    downloadCsv() {
        this.csvService.exportToCSV(this.zoekParameters).subscribe(response => {
            this.utilService.downloadBlobResponse(response, this.filename);
        });
    }
}
