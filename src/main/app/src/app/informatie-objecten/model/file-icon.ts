/*
 * SPDX-FileCopyrightText: 2022 - 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FileIcon {
  // Generic files
  private static readonly pdf = new FileIcon("pdf", "fa-file-pdf", "darkred");
  private static readonly txt = new FileIcon("txt", "fa-file-lines");
  // Office documents
  private static readonly doc = new FileIcon("doc", "fa-file-word", "blue");
  private static readonly docx = new FileIcon("docx", "fa-file-word", "blue");
  private static readonly ods = new FileIcon("ods", "fa-file-word", "blue");
  private static readonly odt = new FileIcon("odt", "fa-file-word", "blue");
  private static readonly ppt = new FileIcon(
    "ppt",
    "fa-file-powerpoint",
    "red",
  );
  private static readonly pptx = new FileIcon(
    "pptx",
    "fa-file-powerpoint",
    "red",
  );
  private static readonly rtf = new FileIcon("rtf", "fa-file-word");
  private static readonly vsd = new FileIcon("vsd", "fa-file-image", "orange");
  private static readonly xls = new FileIcon("xls", "fa-file-excel", "green");
  private static readonly xlsx = new FileIcon("xlsx", "fa-file-excel", "green");
  // Image files
  private static readonly bmp = new FileIcon("bmp", "fa-file-image");
  private static readonly gif = new FileIcon("gif", "fa-file-image");
  private static readonly jpeg = new FileIcon("jpeg", "fa-file-image");
  private static readonly jpg = new FileIcon("jpg", "fa-file-image");
  private static readonly png = new FileIcon("png", "fa-file-image");
  // Video files
  private static readonly avi = new FileIcon("avi", "fa-file-video");
  private static readonly flv = new FileIcon("flv", "fa-file-video");
  private static readonly mkv = new FileIcon("mkv", "fa-file-video");
  private static readonly mov = new FileIcon("mov", "fa-file-video");
  private static readonly mp4 = new FileIcon("mp4", "fa-file-video");
  private static readonly mpeg = new FileIcon("mpeg", "fa-file-video");
  private static readonly wmv = new FileIcon("wmv", "fa-file-video");
  private static readonly msg = new FileIcon("msg", "fa-file-lines");

  public static readonly fileIcons = [
    FileIcon.pdf,
    FileIcon.txt,
    FileIcon.doc,
    FileIcon.docx,
    FileIcon.ods,
    FileIcon.odt,
    FileIcon.ppt,
    FileIcon.pptx,
    FileIcon.rtf,
    FileIcon.vsd,
    FileIcon.xls,
    FileIcon.xlsx,
    FileIcon.bmp,
    FileIcon.gif,
    FileIcon.jpeg,
    FileIcon.jpg,
    FileIcon.png,
    FileIcon.avi,
    FileIcon.flv,
    FileIcon.mkv,
    FileIcon.mov,
    FileIcon.mp4,
    FileIcon.mpeg,
    FileIcon.wmv,
    FileIcon.msg,
  ].sort((fileIconA, fileIconB) => fileIconA.compare(fileIconB));

  public constructor(
    public readonly type: string,
    public readonly icon: string,
    public readonly color?: string,
  ) {}

  getBestandsextensie(): string {
    return "." + this.type.toLowerCase();
  }

  compare(other: FileIcon) {
    return this.type.localeCompare(other.type);
  }

  static getIconByBestandsnaam(bestandsnaam) {
    const extension = bestandsnaam.split(".").pop();
    const obj = FileIcon.fileIcons.filter((row) => {
      if (row.type === extension) {
        return true;
      }
    });
    if (obj.length > 0) {
      return obj[0];
    } else {
      return { type: "unknown", icon: "fa-file-circle-question", color: "" };
    }
  }
}
