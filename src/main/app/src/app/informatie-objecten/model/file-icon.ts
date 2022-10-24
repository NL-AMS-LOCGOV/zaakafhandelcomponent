/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FileIcon {

    // Generic files
    public static readonly pdf = new FileIcon('pdf', 'fa-file-pdf', 'darkred');
    public static readonly txt = new FileIcon('txt', 'fa-file-lines');
    // Office documents
    public static readonly doc = new FileIcon('doc', 'fa-file-word', 'blue');
    public static readonly docx = new FileIcon('docx', 'fa-file-word', 'blue');
    public static readonly ods = new FileIcon('ods', 'fa-file-word', 'blue');
    public static readonly odt = new FileIcon('odt', 'fa-file-word', 'blue');
    public static readonly ppt = new FileIcon('ppt', 'fa-file-powerpoint', 'red');
    public static readonly pptx = new FileIcon('pptx', 'fa-file-powerpoint', 'red');
    public static readonly rtf = new FileIcon('rtf', 'fa-file-word');
    public static readonly vsd = new FileIcon('vsd', 'fa-file-image', 'orange');
    public static readonly xls = new FileIcon('xls', 'fa-file-excel', 'green');
    public static readonly xlsx = new FileIcon('xlsx', 'fa-file-excel', 'green');
    // Image files
    public static readonly bmp = new FileIcon('bmp', 'fa-file-image');
    public static readonly gif = new FileIcon('gif', 'fa-file-image');
    public static readonly jpeg = new FileIcon('jpeg', 'fa-file-image');
    public static readonly jpg = new FileIcon('jpg', 'fa-file-image');
    public static readonly png = new FileIcon('png', 'fa-file-image');
    // Video files
    public static readonly avi = new FileIcon('avi', 'fa-file-video');
    public static readonly flv = new FileIcon('flv', 'fa-file-video');
    public static readonly mkv = new FileIcon('mkv', 'fa-file-video');
    public static readonly mov = new FileIcon('mov', 'fa-file-video');
    public static readonly mp4 = new FileIcon('mp4', 'fa-file-video');
    public static readonly mpeg = new FileIcon('mpeg', 'fa-file-video');
    public static readonly wmv = new FileIcon('wmv', 'fa-file-video');

    public static readonly fileIconList = [FileIcon.pdf, FileIcon.txt,
        FileIcon.doc, FileIcon.docx, FileIcon.ods, FileIcon.odt, FileIcon.ppt, FileIcon.pptx, FileIcon.rtf, FileIcon.vsd, FileIcon.xls, FileIcon.xlsx,
        FileIcon.bmp, FileIcon.gif, FileIcon.jpeg, FileIcon.jpg, FileIcon.png,
        FileIcon.avi, FileIcon.flv, FileIcon.mkv, FileIcon.mov, FileIcon.mp4, FileIcon.mpeg, FileIcon.wmv];

    private constructor(public readonly type: string, public readonly icon: string, public readonly color?: string) {}

    getBestandsextensie(): string {
        return '.' + this.type.toLowerCase();
    }
}
