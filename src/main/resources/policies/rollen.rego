package net.atos.zac.rol

behandelaar := {
    "rol": "behandelaar"
}

coordinator := {
    "rol": "coordinator"
}

recordmanager := {
    "rol": "recordmanager"
}

beheerder := {
    "rol": "beheerder"
}

functioneel := {
    "rol": "functionele_gebruiker"
}

rollen[rol]{
    rol := [behandelaar
           ,coordinator
           ,recordmanager
           ,beheerder
           ,functioneel
           ][i].rol
}
