/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.time.LocalDateTime;

import net.atos.zac.healthcheck.model.BuildInformatie;

public class RESTBuildInformatie {

    public final String commit;

    public final String buildId;

    public final LocalDateTime buildDatumTijd;

    public final String versienummer;

    public RESTBuildInformatie(final BuildInformatie buildInformatie) {
        this.commit = buildInformatie.getCommit();
        this.buildId = buildInformatie.getBuildId();
        this.buildDatumTijd = buildInformatie.getBuildDatumTijd();
        this.versienummer = buildInformatie.getVersienummer();
    }
}
