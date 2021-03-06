package org.codehaus.plexus.mailsender;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public interface MailSender
{
    String ROLE = MailSender.class.getName();

    /**
     * Send a mail.
     *
     * @throws MailSenderException
     */
    void send( MailMessage message )
        throws MailSenderException;

    /**
     * Send a mail message.
     *
     * @throws MailSenderException
     */
    void send( String subject, String content, String toMailbox, String toName, String fromMailbox, String fromName )
        throws MailSenderException;

    /**
     * Send a mail message.
     *
     * @throws MailSenderException
     */
    void send( String subject, String content, String toMailbox, String toName, String fromMailbox, String fromName,
               Map extraHeaders )
        throws MailSenderException;

    /**
     * Verify the content of a mail message.
     *
     * @throws MailSenderException
     */
    void verify( MailMessage message )
        throws MailSenderException;
}
