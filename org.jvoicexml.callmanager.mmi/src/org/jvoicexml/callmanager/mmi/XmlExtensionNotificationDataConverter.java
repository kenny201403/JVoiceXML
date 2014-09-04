/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.callmanager.mmi/src/org/jvoicexml/callmanager/mmi/DecoratedMMIEvent.java $
 * Version: $LastChangedRevision: 3950 $
 * Date:    $Date: 2013-11-23 21:32:07 +0100 (Sat, 23 Nov 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jvoicexml.LastResult;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converts the extension notifications into the XML format. Standards like
 * <a href="http://www.w3.org/TR/emma/">EMMA</a> are used where possible.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class XmlExtensionNotificationDataConverter
        implements ExtensionNotificationDataConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertApplicationLastResult(
            final List<LastResult> lastresults) throws ConversionException {
        if (lastresults == null || lastresults.isEmpty()) {
            return null;
        }
        final LastResult lastresult = lastresults.get(0);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            final Document document = builder.newDocument();
            final Element emma = document.createElementNS(
                    "http://www.w3.org/2003/04/emma", "emma:emma");
            emma.setAttribute("version", "1.0");
            document.appendChild(emma);
            final Element interpretation = document.createElementNS(
                    "http://www.w3.org/2003/04/emma", "emma:interpretation");
            interpretation.setAttribute("id", "lastresult");
            interpretation.setAttributeNS("http://www.w3.org/2003/04/emma",
                    "emma:medium", "acoustic");
            interpretation.setAttributeNS("http://www.w3.org/2003/04/emma",
                    "emma:mode", lastresult.getInputmode());
            interpretation.setAttributeNS("http://www.w3.org/2003/04/emma",
                    "emma:confidence",
                    Float.toString(lastresult.getConfidence()));
            interpretation.setAttributeNS("http://www.w3.org/2003/04/emma",
                    "emma:tokens", lastresult.getUtterance());
            final Object semanticInterpretation = lastresult
                    .getInterpretation();
            addSemanticInterpretation(document, interpretation,
                    semanticInterpretation);
            emma.appendChild(interpretation);
            return emma;
        } catch (ParserConfigurationException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

    /**
     * Possibly add semantic interpretation to the given document
     * 
     * @param document
     *            the document
     * @param parent
     *            the parent node
     * @param object
     *            the sematnic interpretation to add
     */
    private void addSemanticInterpretation(final Document document,
            final Element parent, final Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof ScriptableObject) {
            final ScriptableObject scriptable = (ScriptableObject) object;
            addSemanticInterpretation(document, parent, scriptable);
        } else {
            final Element literal = document.createElementNS(
                    "http://www.w3.org/2003/04/emma", "emma:literal");
            final Text text = document.createTextNode(object.toString());
            literal.appendChild(text);
            parent.appendChild(literal);
        }
    }

    /**
     * Possibly add semantic interpretation as a compund object to the given
     * document
     * 
     * @param document
     *            the document
     * @param parent
     *            the parent node
     * @param object
     *            the semantic interpretation to add
     */
    private void addSemanticInterpretation(final Document document,
            final Element parent, final ScriptableObject object) {
        if (object == null) {
            return;
        }
        final Object[] ids = ScriptableObject.getPropertyIds(object);
        for (Object id : ids) {
            final String key = id.toString();
            Object value = object.get(key, object);
            if (value instanceof ScriptableObject) {
                final Element element = document.createElement(key);
                parent.appendChild(element);
                final ScriptableObject scriptable = (ScriptableObject) value;
                addSemanticInterpretation(document, element, scriptable);
            } else {
                final Element element = document.createElement(key);
                final Text text = document.createTextNode(value.toString());
                element.appendChild(text);
                parent.appendChild(element);
            }
        }
    }

    public String toString(final Document document) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        final TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        try {
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    "yes");
            final Source source = new DOMSource(document);
            transformer.transform(source, result);
            return out.toString();
        } catch (TransformerException e) {
            return super.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertSynthesizedOutputEvent(SynthesizedOutputEvent output)
            throws ConversionException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            final Document document = builder.newDocument();
            final Element data = document.createElementNS(
                    "http://www.nowhere.org/tkmmi", "tkmmi:data");
            final String eventType = toEventType(output);
            data.setAttributeNS("http://www.nowhere.org/tkmmi", "tkmmi:event",
                    eventType);
            final SpeakableText speakable = getSpeakable(output);
            if (speakable != null) {
                final Document ssml = toDocument(speakable);
                final Node speak = ssml.getFirstChild();
                if (speak != null) {
                    document.adoptNode(speak);
                    data.appendChild(speak);
                }
            }
            document.appendChild(data);
            return data;
        } catch (ParserConfigurationException e) {
            throw new ConversionException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ConversionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves a {@link SpeakableText} from the received event, if present.
     * 
     * @param output
     *            the received event
     * @return the speakble if the events knows about it, {@code null} else
     */
    private SpeakableText getSpeakable(final SynthesizedOutputEvent output) {
        if (output instanceof OutputStartedEvent) {
            final OutputStartedEvent started = (OutputStartedEvent) output;
            return started.getSpeakable();
        } else if (output instanceof OutputEndedEvent) {
            final OutputEndedEvent ended = (OutputEndedEvent) output;
            return ended.getSpeakable();
        }
        return null;
    }

    /**
     * Obtains the event type to send out externally for the received
     * internally.
     * 
     * @param event
     *            the received event
     * @return the event type
     */
    private String toEventType(final JVoiceXMLEvent event) {
        if (event instanceof OutputStartedEvent) {
            return "vxml.output.start";
        } else if (event instanceof OutputEndedEvent) {
            return "vxml.output.end";
        } else if (event instanceof QueueEmptyEvent) {
            return "vxml.output.emptyqueue";
        }
        return event.getEventType();
    }

    /**
     * Converts the speakable to an XML document that can be included into the
     * data attribute.
     * 
     * @param speakable
     *            the received speakable
     * @return the converted document
     * @throws ParserConfigurationException
     *             error parsing
     * @throws SAXException
     *             error parsing
     * @throws IOException
     *             error parsing
     */
    private Document toDocument(final SpeakableText speakable)
            throws ParserConfigurationException, SAXException, IOException {
        final String text = speakable.getSpeakableText();
        final StringReader reader = new StringReader(text);
        final InputSource source = new InputSource(reader);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(source);
    }
}