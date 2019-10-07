/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.mmi.events;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * An MMI prepare request.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class PrepareRequest extends LifeCycleRequest
    implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -6159483209265868752L;

    /** The content URL. */
    private ContentURLType contentURL;

    /** Arbitrary content. */
    private AnyComplexType content;

    /**
     * Constructs a new object.
     */
    public PrepareRequest() {
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @param context the context
     * @since 0.7.9
     */
    public PrepareRequest(final String requestId, final String source,
            final String target, final String context) {
        super(requestId, source, target, context);
    }
    
    /**
     * Gets the value of the contentURL property.
     * 
     * @return possible object is {@link ContentURLType }
     * 
     */
    @XmlElement(name = "ContentURL", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public ContentURLType getContentURL() {
        return contentURL;
    }

    /**
     * Sets the value of the contentURL property.
     * 
     * @param value
     *            allowed object is {@link ContentURLType }
     * 
     */
    public void setContentURL(final ContentURLType value) {
        contentURL = value;
    }

    /**
     * Fills the content URL with the given string.
     * @param value the content URL
     */
    public void setContentURL(final String value) {
        final ContentURLType type = new ContentURLType();
        type.setHref(value);
        setContentURL(type);
    }
    
    /**
     * Sets the content URL.
     * @param value the content URL to set
     */
    public void setContentURL(final URL value) {
        final ContentURLType type = new ContentURLType();
        final String href = value.toString();
        type.setHref(href);
        setContentURL(type);
    }

    /**
     * Sets the content URL.
     * @param value the content URL to set
     */
    public void setContentURL(final URI value) {
        final ContentURLType type = new ContentURLType();
        final String href = value.toString();
        type.setHref(href);
        setContentURL(type);
    }

    /**
     * Gets the value of the content property.
     * 
     * @return possible object is {@link AnyComplexType }
     * 
     */
    @XmlElement(name = "Content", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public AnyComplexType getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *            allowed object is {@link AnyComplexType }
     * 
     */
    public void setContent(final AnyComplexType value) {
        content = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(content, contentURL);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PrepareRequest)) {
            return false;
        }
        PrepareRequest other = (PrepareRequest) obj;
        return Objects.equals(content, other.content)
                && Objects.equals(contentURL, other.contentURL);
    }
}

