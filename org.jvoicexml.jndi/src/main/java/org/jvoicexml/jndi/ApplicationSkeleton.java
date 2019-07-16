/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.jndi;

import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.jvoicexml.Application;
import org.jvoicexml.LastResult;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.client.jndi.RemoteApplication;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Skeleton for {@link org.jvoicexml.Application}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public class ApplicationSkeleton extends UnicastRemoteObject
        implements RemoteApplication, Skeleton {

    /** The serial version UID. */
    private static final long serialVersionUID = 6976197627560263588L;

    /** The session ID. */
    private SessionIdentifier sessionIdentifier;

    /** The encapsulated application object. */
    private Application application;

    /**
     * Constructs a new object.
     * 
     * @throws RemoteException
     *             Error creating the remote object.
     */
    protected ApplicationSkeleton() throws RemoteException {
    }

    /**
     * Constructs a new object.
     * 
     * @param id
     *            The session ID.
     * @param app
     *            the application
     * @throws RemoteException
     *             Error creating the skeleton.
     */
    public ApplicationSkeleton(final SessionIdentifier id,
            final Application app)
            throws RemoteException {
        sessionIdentifier = id;
        application = app;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSkeletonName() throws RemoteException {
        return RemoteApplication.class.getSimpleName() + "." 
                + sessionIdentifier.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDocument(final URI uri, final VoiceXmlDocument doc)
            throws RemoteException, BadFetchError {
        if (application == null) {
            return;
        }
        application.addDocument(uri, doc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceXmlDocument getCurrentDocument() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getCurrentDocument();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getApplication() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getApplication();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootDocument(final VoiceXmlDocument document)
            throws RemoteException, BadFetchError {
        if (application == null) {
            return;
        }
        application.setRootDocument(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded(final URI uri) throws RemoteException {
        if (application == null) {
            return false;
        }
        return application.isLoaded(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getXmlBase() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getXmlBase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(final URI uri) throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.resolve(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(final URI baseUri, final URI uri)
            throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.resolve(baseUri, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastResult(final List<LastResult> lastresult)
            throws RemoteException {
        if (application == null) {
            return;
        }
        application.setLastResult(lastresult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LastResult> getLastResult() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getLastResult();
    }
}
