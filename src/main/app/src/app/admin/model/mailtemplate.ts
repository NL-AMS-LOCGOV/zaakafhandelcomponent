/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class Mailtemplate {
    id: number;
    mailTemplateNaam: string;
    onderwerp: string;
    body: string;
    mailTemplateEnum: string;
    parent: number;
}
