package net.atos.zac.webdav;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections4.map.LRUMap;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.zac.authentication.LoggedInUser;

@Singleton
public class WebdavHelper {

    public static final String FOLDER = "folder";

    private static final String WEBDAV_CONTEXT_PATH = "/webdav";

    /**
     * De mapping naar applicaties met WebDAV support
     */
    private static final Set<String> WEBDAV_WORD = Set.of("application/msword",
                                                          "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private static final Set<String> WEBDAV_POWERPOINT = Set.of("application/vnd.ms-powerpoint",
                                                                "application/vnd.openxmlformats-officedocument.presentationml.presentation");

    private static final Set<String> WEBDAV_EXCEL = Set.of("application/vnd.ms-excel",
                                                           "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    private final Map<String, Gegevens> tokenMap = Collections.synchronizedMap(new LRUMap<>(1000));

    public URI createRedirectURL(final UUID enkelvoudigInformatieobjectUUID, final UriInfo uriInfo) {
        final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
        final String scheme = format("%s:%s", getWebDAVApp(enkelvoudigInformatieobject.getFormaat()), uriInfo.getBaseUri().getScheme());
        final String filename = format("%s.%s", createToken(enkelvoudigInformatieobjectUUID), getExtension(enkelvoudigInformatieobject.getBestandsnaam()));
        return uriInfo.getBaseUriBuilder().scheme(scheme).replacePath("webdav/folder/{filename}").build(filename);
    }

    public Gegevens readGegevens(final String token) {
        if (tokenMap.containsKey(token)) {
            return tokenMap.get(token);
        } else {
            throw new RuntimeException("WebDAV token does not exist (anymore).");
        }
    }

    private String createToken(final UUID enkelvoudigInformatieobjectUUID) {
        final String token = UUID.randomUUID().toString();
        tokenMap.put(token, new Gegevens(enkelvoudigInformatieobjectUUID, loggedInUserInstance.get().getId()));
        return token;
    }

    private String getWebDAVApp(final String formaat) {
        if (WEBDAV_WORD.contains(formaat)) {
            return "ms-word";
        }
        if (WEBDAV_EXCEL.contains(formaat)) {
            return "ms-excel";
        }
        if (WEBDAV_POWERPOINT.contains(formaat)) {
            return "ms-powerpoint";
        }
        return null;
    }

    public record Gegevens(UUID enkelvoudigInformatieibjectUUID, String loggedInUserId) {}
}
