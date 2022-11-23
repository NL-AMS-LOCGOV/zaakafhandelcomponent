package net.atos.zac.domein

domein_elk_zaaktype := {
    "rol": "domein_elk_zaaktype",
    "zaaktypen": { "-ALLE-ZAAKTYPEN-" }
}

domeinen := [
    domein_elk_zaaktype,
    {
        "rol": "domein_kampen",
        "zaaktypen": { "Aanvragen evenementenvergunning beoordelen" }
    },
    {
        "rol": "domein_dowr",
        "zaaktypen": { "Subsidie" }
    },
    {
        "rol": "domein_laarbeek",
        "zaaktypen": { "Aanleg uitweg" }
    },
    {
       "rol": "domein_oost_gelre",
       "zaaktypen": { "Toeristenbelastingen" }
    },
    {
       "rol": "domein_oldenzaal",
       "zaaktypen": { "Wegenbeheer en onderhoud" }
    },
]

