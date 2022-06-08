package net.atos.zac.webdav;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.atos.client.zgw.drc.DRCClientService;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;

import org.apache.commons.io.FilenameUtils;

import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;

import javax.inject.Inject;

/**
 * e-Suite implementatie van de {@link IWebdavStore}
 */
public class WebdavStore implements IWebdavStore {

    private static final Logger LOG = Logger.getLogger(WebdavStore.class.getName());

    private static final StoredObject folderStoredObject;

    @Inject
    private DRCClientService drcClientService;

    static {
        folderStoredObject = new StoredObject();
        folderStoredObject.setFolder(true);
    }

    // De dummy parameter is nodig omdat de constructie waarmee deze class wordt geinstantieerd deze parameter verwacht
    public WebdavStore(final File dummy) {

    }

    public WebdavStore() {

    }

    @Override
    public ITransaction begin(final Principal principal) {
        LOG.log(Level.FINE, () -> "begin()");
        return null;
    }

    @Override
    public void checkAuthentication(final ITransaction transaction) {
        LOG.log(Level.FINE, () -> "checkAuthentication()");
    }

    @Override
    public void commit(final ITransaction transaction) {
        LOG.log(Level.FINE, () -> "commit()");
    }

    @Override
    public void rollback(final ITransaction transaction) {
        LOG.log(Level.FINE, () -> "rollback()");
    }

    @Override
    public void createFolder(final ITransaction transaction, final String folderUri) {
        LOG.log(Level.FINE, () -> "createFolder()");
    }

    @Override
    public void createResource(final ITransaction transaction, final String resourceUri) {
        LOG.log(Level.FINE, () -> "createResource()");
    }

    @Override
    public InputStream getResourceContent(final ITransaction transaction, final String resourceUri) {
        LOG.log(Level.FINE, () -> String.format("getResourceContent(%s)", resourceUri));
        final String token = extraheerToken(resourceUri);
        if (token != null) {
            //ophalen document ahv token
            final EnkelvoudigInformatieobject document = drcClientService.readEnkelvoudigInformatieobject(UUID.fromString(token));
            return drcClientService.downloadEnkelvoudigInformatieobject(document.getUUID(), document.getVersie());
        }
        return null;
    }

    @Override
    public long setResourceContent(final ITransaction transaction, final String resourceUri, final InputStream content, final String contentType,
                                   final String characterEncoding) {
        LOG.log(Level.FINE, () -> String.format("setResourceContent(%s,%s,%s)", resourceUri, contentType, characterEncoding));
        final String token = extraheerToken(resourceUri);
        if (token != null) {
            // omvang in bytes
            final EnkelvoudigInformatieobject document =
                    drcClientService.readEnkelvoudigInformatieobject(UUID.fromString(token));
            return document.getBestandsomvang();
        } else {
            return 0;
        }
    }

    @Override
    public String[] getChildrenNames(final ITransaction transaction, final String folderUri) {
        LOG.log(Level.FINE, () -> String.format("getChildrenNames(%s)", folderUri));
        return null;
    }

    @Override
    public long getResourceLength(final ITransaction transaction, final String resourceUri) {
        LOG.log(Level.FINE, () -> "getResourceLength()");
        return 0;
    }

    @Override
    public void removeObject(final ITransaction transaction, final String uri) {
        LOG.log(Level.FINE, () -> "removeObject()");
    }

    @Override
    public StoredObject getStoredObject(final ITransaction transaction, final String uri) {
        LOG.log(Level.FINE, () -> String.format("getStoredObject(%s)", uri));
        return null;
    }

    private String extraheerToken(final String uri) {
        if (uri != null) {
            final File url = new File(uri);
            return FilenameUtils.getBaseName(url.getName());
        }
        return null;
    }
}
