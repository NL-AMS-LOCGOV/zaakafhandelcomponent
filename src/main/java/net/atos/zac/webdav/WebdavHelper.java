package net.atos.zac.webdav;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.microprofile.config.ConfigProvider;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class WebdavHelper {

    private static final String FOLDER = "folder";

    private static final String WEBDAV_CONTEXT_PATH = "/webdav";

    /**
     * De mapping naar applicaties met WebDAV support
     */
    private static final Set<String> WEBDAV_WORD = new HashSet<>() {{
        add("application/msword");
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }};

    private static final Set<String> WEBDAV_POWERPOINT = new HashSet<>() {{
        add("application/vnd.ms-powerpoint");
        add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }};

    private static final Set<String> WEBDAV_EXCEL = new HashSet<>() {{
        add("application/vnd.ms-excel");
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }};

    public static final String CONTEXT_URL = ConfigProvider.getConfig().getValue("context.url", String.class);

    public URI maakToken(final EnkelvoudigInformatieobject document) {
        return URI.create(format("%s:%s/%s/%s.%s", getWebDAVApp(document.getFormaat()), getWebdavBaseUrl(),
                                 FOLDER, document.getUUID().toString(),
                                 FilenameUtils.getExtension(document.getBestandsnaam())));
    }

    private String getWebdavBaseUrl() {
        return CONTEXT_URL + WEBDAV_CONTEXT_PATH;
    }

    public String getWebDAVApp(final String mimeType) {
        if (WEBDAV_WORD.contains(mimeType)) {
            return "ms-word";
        }
        if (WEBDAV_EXCEL.contains(mimeType)) {
            return "ms-excel";
        }
        if (WEBDAV_POWERPOINT.contains(mimeType)) {
            return "ms-powerpoint";
        }
        return null;
    }
}
