export enum FileFormat {
    PDF = 'application/pdf',
    JPEG = 'image/jpeg',
    BMP = 'image/bmp',
    GIF = 'image/gif',
    PNG = 'image/png',
    TEXT = 'text/plain',
    XLSX = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    XLS = 'application/vnd.ms-excel',
    POWERPOINT = 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    DOCX = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    DOC = 'application/msword',
    ODT = 'application/vnd.oasis.opendocument.text',
    VSD = 'application/vnd.visio',
    RTF = 'application/rtf'
}

export class FileFormatUtil {
    static isPreviewAvailable(format: FileFormat): boolean {
        return format === FileFormat.PDF || format === FileFormat.JPEG || format === FileFormat.PNG || format === FileFormat.GIF;
    }

    static isImage(format: FileFormat): boolean {
        return format === FileFormat.JPEG || format === FileFormat.PNG || format === FileFormat.GIF || format === FileFormat.BMP;
    }
}
