/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object.model;

import java.time.LocalDate;
import java.util.Map;

/**
 *
 */
public class ObjectRecord {

    /**
     * Incremental index number of the object record
     * - required
     * - readOnly
     */
    private Integer index;

    /**
     * Version of the OBJECTTYPE for data in the object record
     * - required
     * - minimum 0
     * - maximum: 32767
     */
    private Integer typeVersion;

    /**
     * Object data, based on OBJECTTYPE
     */
    private Map<String, Object> data;

    // ToDo geometry;

    /**
     * Legal start date of the object record
     * - required
     */
    private LocalDate startAt;

    /**
     * Legal end date of the object record
     * - required
     * - readOnly
     */
    private LocalDate endAt;

    /**
     * The date when the record was registered in the system
     * - required
     * - readOnly
     */
    private LocalDate registrationAt;

    /**
     * Index of the record corrected by the current record
     */
    private Integer correctionFor;

    /**
     * Index of the record, which corrects the current record
     * - required
     * - readOnly
     */
    private Integer correctedBy;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(final Integer index) {
        this.index = index;
    }

    public Integer getTypeVersion() {
        return typeVersion;
    }

    public void setTypeVersion(final Integer typeVersion) {
        this.typeVersion = typeVersion;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(final Map<String, Object> data) {
        this.data = data;
    }

    public LocalDate getStartAt() {
        return startAt;
    }

    public void setStartAt(final LocalDate startAt) {
        this.startAt = startAt;
    }

    public LocalDate getEndAt() {
        return endAt;
    }

    public void setEndAt(final LocalDate endAt) {
        this.endAt = endAt;
    }

    public LocalDate getRegistrationAt() {
        return registrationAt;
    }

    public void setRegistrationAt(final LocalDate registrationAt) {
        this.registrationAt = registrationAt;
    }

    public Integer getCorrectionFor() {
        return correctionFor;
    }

    public void setCorrectionFor(final Integer correctionFor) {
        this.correctionFor = correctionFor;
    }

    public Integer getCorrectedBy() {
        return correctedBy;
    }

    public void setCorrectedBy(final Integer correctedBy) {
        this.correctedBy = correctedBy;
    }
}
