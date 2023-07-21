export class DocumentVerplaatsGegevens {
  public nieuweZaakID;

  constructor(
    public documentUUID: string,
    public documentTitel: string,
    public documentTypeUUID: string,
    public bron: string,
  ) {}
}
