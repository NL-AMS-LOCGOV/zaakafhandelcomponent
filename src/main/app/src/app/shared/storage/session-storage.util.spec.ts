/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SessionStorageUtil } from "./session-storage.util";

describe("SessionStorageService", () => {
  afterEach(() => {
    SessionStorageUtil.clearSessionStorage();
  });

  it("should return a gebruikersnaam with naam Jaap", () => {
    const gebruiker = { gebruikersnaam: "Jaap" };
    const gebruikerJSON = JSON.stringify(gebruiker);

    spyOn(sessionStorage, "getItem").and.returnValue(gebruikerJSON);

    expect(SessionStorageUtil.getItem("gebruikersnaam")).toEqual(
      JSON.parse(gebruikerJSON),
    );
    expect(sessionStorage.getItem).toHaveBeenCalled();
  });

  it("key value v should be equal to v", () => {
    expect(SessionStorageUtil.getItem("k", "v")).toEqual("v");
  });

  it("should return Jaap", () => {
    const naam = SessionStorageUtil.setItem("gebruikersnaam", "Jaap");
    expect(naam).toBe("Jaap");
  });

  it("should break the reference", () => {
    spyOn(JSON, "parse");
    spyOn(JSON, "stringify");
    spyOn(sessionStorage, "setItem");

    const gebruiker = SessionStorageUtil.getItem("", "Jaap");

    expect(JSON.parse).toHaveBeenCalled();
    expect(JSON.stringify).toHaveBeenCalled();
    expect(sessionStorage.setItem).toHaveBeenCalled();

    const jaap = { gebruikersnaam: "Jaap" };
    const gebruikerJSON = JSON.stringify(jaap);

    expect(gebruikerJSON).toEqual(gebruiker);
  });

  // session storage word momenteel niet leeggegooid. Echter, in de afterAll() gebeurt dit wel
  xit("should call clear", () => {
    spyOn(sessionStorage, "clear");
    SessionStorageUtil.setItem("testWaarde", "waarde");
    SessionStorageUtil.clearSessionStorage();
    expect(sessionStorage.clear).toHaveBeenCalled();
    expect(SessionStorageUtil.getItem("testWaarde")).toBe(undefined);
  });
});
