package net.atos.zac.webdav;

import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoudAndLock;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;

public class WebdavStore implements IWebdavStore {

    private static final StoredObject folderStoredObject;

    private static final String TOELICHTING = "Document bewerkt";

    static {
        folderStoredObject = new StoredObject();
        folderStoredObject.setFolder(true);
    }

    /*
     * Tijdens het ophalen van het document via WebDAV wordt er vaak een File StoredObject aangemaakt.
     * Om te voorkomen dat er vaak een Document moet worden opgehaald wordt er een map bijgeouden van token naar File StoredObject.
     */
    private static final Map<String, StoredObject> fileStoredObjectMap = Collections.synchronizedMap(new LRUMap<>(100));

    private WebdavHelper webdavHelper;

    private DRCClientService drcClientService;

    // De dummy parameter is nodig omdat de constructie waarmee deze class wordt geinstantieerd deze parameter verwacht
    public WebdavStore(final File dummy) {
        webdavHelper = CDI.current().select(WebdavHelper.class).get();
        drcClientService = CDI.current().select(DRCClientService.class).get();
    }

    @Override
    public ITransaction begin(final Principal principal) {
        return null;
    }

    @Override
    public void checkAuthentication(final ITransaction transaction) {}

    @Override
    public void commit(final ITransaction transaction) {}

    @Override
    public void rollback(final ITransaction transaction) {}

    @Override
    public void createFolder(final ITransaction transaction, final String folderUri) {}

    @Override
    public void createResource(final ITransaction transaction, final String resourceUri) {}

    @Override
    public InputStream getResourceContent(final ITransaction transaction, final String resourceUri) {
        final String token = extraheerToken(resourceUri);
        if (StringUtils.isNotEmpty(token)) {
            final UUID enkelvoudigInformatieobjectUUID = webdavHelper.readGegevens(token).enkelvoudigInformatieibjectUUID();
            return drcClientService.downloadEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
        } else {
            return null;
        }
    }

    @Override
    public long setResourceContent(final ITransaction transaction, final String resourceUri, final InputStream content, final String contentType,
            final String characterEncoding) {
        final String token = extraheerToken(resourceUri);
        if (StringUtils.isNotEmpty(token)) {
            final WebdavHelper.Gegevens webdavGegevens = webdavHelper.readGegevens(token);
            try {
                final String lock = drcClientService.lockEnkelvoudigInformatieobject(webdavGegevens.enkelvoudigInformatieibjectUUID(),
                                                                                     webdavGegevens.loggedInUserId());
                final EnkelvoudigInformatieobjectWithInhoudAndLock enkelvoudigInformatieobjectWithInhoudAndLock = new EnkelvoudigInformatieobjectWithInhoudAndLock();
                enkelvoudigInformatieobjectWithInhoudAndLock.setLock(lock);
                enkelvoudigInformatieobjectWithInhoudAndLock.setInhoud(IOUtils.toByteArray(content));
                return drcClientService.partialUpdateEnkelvoudigInformatieobject(
                        webdavGegevens.enkelvoudigInformatieibjectUUID(), TOELICHTING,
                        enkelvoudigInformatieobjectWithInhoudAndLock).getBestandsomvang();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            } finally {
                drcClientService.unlockEnkelvoudigInformatieobject(webdavGegevens.enkelvoudigInformatieibjectUUID(), webdavGegevens.loggedInUserId());
                fileStoredObjectMap.remove(token);
            }
        } else {
            return 0;
        }
    }

    @Override
    public String[] getChildrenNames(final ITransaction transaction, final String folderUri) {
        return null;
    }

    @Override
    public long getResourceLength(final ITransaction transaction, final String resourceUri) {
        return 0;
    }

    @Override
    public void removeObject(final ITransaction transaction, final String uri) {}

    @Override
    public StoredObject getStoredObject(final ITransaction transaction, final String uri) {
        final String token = extraheerToken(uri);
        if (StringUtils.isEmpty(token)) {
            return null;
        } else if (StringUtils.equals(token, WebdavHelper.FOLDER)) {
            return folderStoredObject;
        } else {
            return getFileStoredObject(token);
        }
    }

    private String extraheerToken(final String uri) {
        if (uri != null) {
            final File url = new File(uri);
            return FilenameUtils.getBaseName(url.getName());
        } else {
            return null;
        }
    }

    private StoredObject getFileStoredObject(final String token) {
        return fileStoredObjectMap.computeIfAbsent(token, key -> {
            final UUID enkelvoudigInformatieobjectUUID = webdavHelper.readGegevens(token).enkelvoudigInformatieibjectUUID();
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject = drcClientService.readEnkelvoudigInformatieobject(enkelvoudigInformatieobjectUUID);
            final StoredObject storedObject = new StoredObject();
            storedObject.setFolder(false);
            storedObject.setCreationDate(convertToDate(enkelvoudigInformatieobject.getCreatiedatum()));
            storedObject.setLastModified(convertToDate(enkelvoudigInformatieobject.getBeginRegistratie()));
            storedObject.setResourceLength(enkelvoudigInformatieobject.getBestandsomvang());
            return storedObject;
        });
    }
}
