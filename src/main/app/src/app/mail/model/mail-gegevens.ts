/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class MailGegevens {
  verzender: string;
  ontvanger: string;
  replyTo: string;
  onderwerp: string;
  body: string;
  bijlagen: string;
  createDocumentFromMail: boolean;
}
