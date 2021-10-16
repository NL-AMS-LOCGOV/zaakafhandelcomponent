/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import javax.ws.rs.FormParam;

public class RESTFileUpload {

    @FormParam("file")
    public byte[] file;

    @FormParam("filesize")
    public long fileSize;

    @FormParam("filename")
    public String filename;

    @FormParam("type")
    public String type;
}
