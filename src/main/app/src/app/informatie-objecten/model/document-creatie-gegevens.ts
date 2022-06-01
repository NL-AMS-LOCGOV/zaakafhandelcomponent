/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class DocumentCreatieGegevens {

    public titel: string;

    constructor(public zaakUUID: string, public informatieobjecttypeUUID: string, public taskId: string) {}
}
