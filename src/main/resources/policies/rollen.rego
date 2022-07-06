package net.atos.zac

alle_zaaktypen := { "-ALLE-ZAAKTYPEN-" }

rollen := {
    "zaakbehandelaar": {
        "id": "zaakbehandelaar",
        "zaaktypen": {"melding-klein-evenement"}
    },
    "werkverdeler": {
        "id": "werkverdeler",
        "zaaktypen": {"melding-klein-evenement", "mor_anponiem"}
    },
    "recordmanager": {
        "id": "recordmanager",
        "zaaktypen": alle_zaaktypen
    },
    "beheerder": {
        "id": "beheerder",
        "zaaktypen": alle_zaaktypen
    }
}
