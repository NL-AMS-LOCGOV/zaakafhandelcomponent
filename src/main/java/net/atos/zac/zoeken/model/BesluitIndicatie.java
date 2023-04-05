/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

/* This enum represents the possible indicaties for a besluit.

   The order of the enum values determines the sort priority of the indicators (the highest priority indicator first)
   Nota bene: When the order of the enum values changes, the Solr index for besluiten (we don't have that yet ;-)
   MUST be rebuilt. Oh, and no more than 63 indicaties in this enum please (it needs to fit in a signed plong in Solr)
 */
public enum BesluitIndicatie {
    INGETROKKEN;
}
