/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten;

import static net.atos.zac.app.klanten.KlantenUtil.ONBEKEND;
import static net.atos.zac.app.klanten.converter.RESTPersoonConverter.FIELDS_PERSOON;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.brp.BRPClientService;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.IngeschrevenPersoonHalCollectie;
import net.atos.client.brp.model.ListPersonenParameters;
import net.atos.client.klanten.KlantenClientService;
import net.atos.client.klanten.model.Klant;
import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.zoeken.model.Resultaat;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.klanten.converter.RESTBedrijfConverter;
import net.atos.zac.app.klanten.converter.RESTPersoonConverter;
import net.atos.zac.app.klanten.converter.RESTRoltypeConverter;
import net.atos.zac.app.klanten.model.bedrijven.RESTBedrijf;
import net.atos.zac.app.klanten.model.bedrijven.RESTListBedrijvenParameters;
import net.atos.zac.app.klanten.model.klant.IdentificatieType;
import net.atos.zac.app.klanten.model.klant.RESTContactGegevens;
import net.atos.zac.app.klanten.model.klant.RESTKlant;
import net.atos.zac.app.klanten.model.klant.RESTRoltype;
import net.atos.zac.app.klanten.model.personen.RESTListPersonenParameters;
import net.atos.zac.app.klanten.model.personen.RESTPersoon;
import net.atos.zac.app.shared.RESTResultaat;

@Path("klanten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class KlantenRESTService {

    public static final Set<AardVanRol> betrokkenen;

    private static final RESTPersoon ONBEKEND_PERSOON = new RESTPersoon(ONBEKEND, ONBEKEND, ONBEKEND);

    static {
        betrokkenen = EnumSet.allOf(AardVanRol.class);
        betrokkenen.remove(AardVanRol.INITIATOR);
        betrokkenen.remove(AardVanRol.BEHANDELAAR);
    }

    @Inject
    private BRPClientService brpClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private RESTPersoonConverter persoonConverter;

    @Inject
    private RESTBedrijfConverter bedrijfConverter;

    @Inject
    private RESTRoltypeConverter roltypeConverter;

    @Inject
    private KlantenClientService klantenClientService;

    @GET
    @Path("persoon/{bsn}")
    public RESTPersoon readPersoon(@PathParam("bsn") final String bsn) throws ExecutionException, InterruptedException {
        return brpClientService.findPersoonAsync(bsn, FIELDS_PERSOON)
                .thenCombine(klantenClientService.findPersoonAsync(bsn),
                             (persoon, klant) -> convertToRESTPersoon(persoon, klant))
                .toCompletableFuture()
                .get();
    }

    private RESTPersoon convertToRESTPersoon(final Optional<IngeschrevenPersoonHal> persoon,
            final Optional<Klant> klant) {
        return persoon
                .map(persoonConverter::convert)
                .map(restPersoon -> (RESTPersoon) addKlantData(restPersoon, klant))
                .orElse(ONBEKEND_PERSOON);
    }

    private RESTKlant addKlantData(final RESTKlant restKlant, final Optional<Klant> klantOptional) {
        klantOptional.ifPresent(klant -> {
            restKlant.telefoonnummer = klant.getTelefoonnummer();
            restKlant.emailadres = klant.getEmailadres();
        });
        return restKlant;
    }

    @GET
    @Path("vestiging/{vestigingsnummer}")
    public RESTBedrijf readVestiging(@PathParam("vestigingsnummer") final String vestigingsnummer)
            throws ExecutionException, InterruptedException {
        return kvkClientService.findVestigingAsync(vestigingsnummer)
                .thenCombine(klantenClientService.findVestigingAsync(vestigingsnummer),
                             (vestiging, klant) -> convertToRESTBedrijf(vestiging, klant))
                .toCompletableFuture()
                .get();
    }

    private RESTBedrijf convertToRESTBedrijf(final Optional<ResultaatItem> vestiging, final Optional<Klant> klant) {
        return vestiging
                .map(bedrijfConverter::convert)
                .map(restBedrijf -> (RESTBedrijf) addKlantData(restBedrijf, klant))
                .orElseGet(() -> new RESTBedrijf());
    }

    @GET
    @Path("rechtspersoon/{rsin}")
    public RESTBedrijf readRechtspersoon(@PathParam("rsin") final String rsin) {
        return kvkClientService.findRechtspersoon(rsin)
                .map(bedrijfConverter::convert)
                .orElseGet(() -> new RESTBedrijf());
    }

    @PUT
    @Path("personen")
    public RESTResultaat<RESTPersoon> listPersonen(final RESTListPersonenParameters restListPersonenParameters) {
        final ListPersonenParameters listPersonenParameters = persoonConverter.convert(restListPersonenParameters);
        final IngeschrevenPersoonHalCollectie ingeschrevenPersoonHalCollectie = brpClientService.listPersonen(
                listPersonenParameters);
        return new RESTResultaat<>(
                persoonConverter.convert(ingeschrevenPersoonHalCollectie.getEmbedded().getIngeschrevenpersonen()));
    }

    @PUT
    @Path("bedrijven")
    public RESTResultaat<RESTBedrijf> listBedrijven(final RESTListBedrijvenParameters restParameters) {
        final KVKZoekenParameters zoekenParameters = bedrijfConverter.convert(restParameters);
        final Resultaat resultaat = kvkClientService.list(zoekenParameters);
        return new RESTResultaat<>(resultaat.getResultaten().stream()
                                           .filter(KlantenRESTService::isKoppelbaar)
                                           .map(bedrijfConverter::convert)
                                           .toList());
    }

    private static boolean isKoppelbaar(final ResultaatItem item) {
        return item.getVestigingsnummer() != null || item.getRsin() != null;
    }

    @GET
    @Path("roltype/{zaaktypeUuid}/betrokkene")
    public List<RESTRoltype> listBetrokkeneRoltypen(@PathParam("zaaktypeUuid") final UUID zaaktype) {
        return roltypeConverter.convert(
                ztcClientService.listRoltypen(ztcClientService.readZaaktype(zaaktype).getUrl()).stream()
                        .filter(roltype -> betrokkenen.contains(roltype.getOmschrijvingGeneriek()))
                        .sorted(Comparator.comparing(Roltype::getOmschrijving)));
    }

    @GET
    @Path("contactgegevens/{identificatieType}/{initiatorIdentificatie}")
    public RESTContactGegevens ophalenContactGegevens(@PathParam("identificatieType") final IdentificatieType identificatieType,
            @PathParam("initiatorIdentificatie") final String initiatorIdentificatie) {
        final RESTContactGegevens restContactGegevens = new RESTContactGegevens();
        if (identificatieType == null) {
            return restContactGegevens;
        }

        final Optional<Klant> klantOptional;
        switch (identificatieType) {
            case VN -> klantOptional = klantenClientService.findVestiging(initiatorIdentificatie);
            case BSN -> klantOptional = klantenClientService.findPersoon(initiatorIdentificatie);
            default -> klantOptional = Optional.empty();
        }

        klantOptional.ifPresent(klant -> {
            restContactGegevens.telefoonnummer = klant.getTelefoonnummer();
            restContactGegevens.emailadres = klant.getEmailadres();
        });

         return restContactGegevens;
    }
}
