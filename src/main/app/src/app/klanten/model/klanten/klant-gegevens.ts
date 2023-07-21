import { Klant } from "./klant";
import { Roltype } from "./roltype";

export class KlantGegevens {
  constructor(public klant: Klant) {}

  betrokkeneRoltype: Roltype;
  betrokkeneToelichting: string;
}
