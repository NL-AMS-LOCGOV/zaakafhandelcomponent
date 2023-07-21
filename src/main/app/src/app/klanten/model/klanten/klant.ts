import { IdentificatieType } from "./identificatieType";

export interface Klant {
  identificatieType: IdentificatieType;
  identificatie: string;
  naam: string;
  emailadres: string;
  telefoonnummer: string;
}
