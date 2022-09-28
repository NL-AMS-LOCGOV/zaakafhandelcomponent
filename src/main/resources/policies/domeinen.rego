package net.atos.zac.domein

domein_elk_zaaktype := {
    "rol": "elk_zaaktype",
    "zaaktypen": { "-ALLE-ZAAKTYPEN-" }
}

domeinen := [
    domein_elk_zaaktype,
    {
        "rol": "domeina",
        "zaaktypen": { "zaaktype1", "zaaktype2" }
    },
    {
        "rol": "domeinb",
        "zaaktypen": { "zaaktype1", "zaaktype3" }
    }
]

