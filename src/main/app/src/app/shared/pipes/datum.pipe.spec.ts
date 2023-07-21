/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { DatumPipe } from "./datum.pipe";

describe("DatumPipe", () => {
  it("create an instance", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe).toBeTruthy();
  });

  it("2021-06-23T00:00:00Z -> 23-06-2021 02:00", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-23T00:00:00Z", "short")).toEqual(
      "23-06-2021 02:00",
    );
  });

  it("2021-06-22T22:00:00Z -> 23-06-2021 00:00", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "short")).toEqual(
      "23-06-2021 00:00",
    );
  });

  it("2021-06-23T02:00:00Z -> 23-06-2021 04:00", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-23T02:00:00Z", "short")).toEqual(
      "23-06-2021 04:00",
    );
  });

  it("1939-03-31T23:40:00Z -> 01-04-1939 00:00", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("1939-03-31T23:40:00Z", "short")).toEqual(
      "01-04-1939 00:00",
    );
  });

  it("default format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z")).toEqual("23-06-2021");
  });

  it("short format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "short")).toEqual(
      "23-06-2021 00:00",
    );
  });

  it("medium format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "medium")).toEqual(
      "23 jun. 2021 00:00",
    );
  });

  it("long format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "long")).toEqual(
      "23 juni 2021 00:00",
    );
  });

  it("full format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "full")).toEqual(
      "woensdag 23 juni 2021 00:00",
    );
  });

  it("shortDate format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "shortDate")).toEqual(
      "23-06-2021",
    );
  });

  it("mediumDate format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "mediumDate")).toEqual(
      "23 jun. 2021",
    );
  });

  it("longDate format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "longDate")).toEqual(
      "23 juni 2021",
    );
  });

  it("fullDate format", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-06-22T22:00:00Z", "fullDate")).toEqual(
      "woensdag, 23 juni 2021",
    );
  });

  it("1939-03-31T23:40:00Z -> 01-04-1939 00:00", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("1939-03-31T23:40:00Z", "short")).toEqual(
      "01-04-1939 00:00",
    );
  });

  it("iso date 1939-04-01 -> 1939-04-01", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("1939-04-01", "short")).toEqual("01-04-1939 00:00");
  });

  it("iso date 2021-05-24 -> 24-05-2021", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-05-24", "short")).toEqual("24-05-2021 00:00");
  });

  it("24-05-2021 -> invalid", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("24-05-2021")).toEqual("Invalid date");
  });

  it("05-24-2021 -> invalid", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("24-05-2021")).toEqual("Invalid date");
  });

  it("2021-24-05 -> invalid", () => {
    const pipe = new DatumPipe("nl");
    expect(pipe.transform("2021-24-05")).toEqual("Invalid date");
  });
});
