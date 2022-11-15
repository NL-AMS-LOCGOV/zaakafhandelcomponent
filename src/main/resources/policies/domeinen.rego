package net.atos.zac.domein

domein_elk_zaaktype := {
    "rol": "domein_elk_zaaktype",
    "zaaktypen": { "-ALLE-ZAAKTYPEN-" }
}

domeinen := [
    domein_elk_zaaktype,
    {
        "rol": "domein_voorzieningen_verstrekken",
        "zaaktypen": { "subsidie" }
    },
    {
        "rol": "domein_toestemming_verlenen",
        "zaaktypen": { "melding klein evenement", "aanleg uitweg" }
    }
]

