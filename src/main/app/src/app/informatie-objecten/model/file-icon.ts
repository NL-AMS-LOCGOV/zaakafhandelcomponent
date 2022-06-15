/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FileIcon {

    public static readonly xlsx = new FileIcon('xlsx', 'fa-file-excel', 'green');
    public static readonly xls = new FileIcon('xls', 'fa-file-excel', 'green');
    public static readonly pdf = new FileIcon('pdf', 'fa-file-pdf', 'red');
    public static readonly jpg = new FileIcon('jpg', 'fa-file-image');
    public static readonly png = new FileIcon('png', 'fa-file-image');
    public static readonly jpeg = new FileIcon('jpeg', 'fa-file-image');
    public static readonly gif = new FileIcon('gif', 'fa-file-image');
    public static readonly rtf = new FileIcon('rtf', 'fa-file-image');
    public static readonly vsd = new FileIcon('vsd', 'fa-file-image');
    public static readonly bmp = new FileIcon('bmp', 'fa-file-image');
    public static readonly doc = new FileIcon('doc', 'fa-file-word', 'blue');
    public static readonly docx = new FileIcon('docx', 'fa-file-word', 'blue');
    public static readonly odt = new FileIcon('odt', 'fa-file-word', 'blue');
    public static readonly pptx = new FileIcon('pptx', 'fa-file-powerpoint', 'red');
    public static readonly ppt = new FileIcon('ppt', 'fa-file-powerpoint', 'red');
    public static readonly txt = new FileIcon('txt', 'fa-file-lines');

    public static readonly fileIconList = [FileIcon.xlsx, FileIcon.xls, FileIcon.pdf, FileIcon.jpg, FileIcon.jpeg,
        FileIcon.gif, FileIcon.rtf, FileIcon.vsd, FileIcon.bmp, FileIcon.doc, FileIcon.docx, FileIcon.odt,
        FileIcon.pptx, FileIcon.ppt, FileIcon.txt, FileIcon.png];

    private constructor(public readonly type: string, public readonly icon: string, public readonly color?: string) {}
}
