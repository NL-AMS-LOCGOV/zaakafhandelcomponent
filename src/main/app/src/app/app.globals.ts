/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FileIcon} from './informatie-objecten/model/file-icon';

export const AppGlobals = Object.freeze({
    FILE_MAX_SIZE: 80, // MB
    ALLOWED_FILETYPES: FileIcon.fileIconList.map(fileIcon => fileIcon.getBestandsextensie()).join(', ')
});
