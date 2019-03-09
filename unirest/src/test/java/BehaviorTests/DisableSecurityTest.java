/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package BehaviorTests;

import kong.unirest.TestUtil;
import kong.unirest.Unirest;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import static org.junit.Assert.assertEquals;

@Ignore // dont normally run these because they depend on badssl.com
public class DisableSecurityTest extends BddTest {
    @Test
    public void badName() {
        fails("https://wrong.host.badssl.com/",
                SSLPeerUnverifiedException.class,
                "javax.net.ssl.SSLPeerUnverifiedException: " +
                        "Certificate for <wrong.host.badssl.com> doesn't match any of the subject alternative names: " +
                        "[*.badssl.com, badssl.com]");
        disableSsl();
        canCall("https://wrong.host.badssl.com/");
    }

    @Test
    public void expired() {
        fails("https://expired.badssl.com/",
                SSLHandshakeException.class,
                "javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: " +
                        "PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed");
        disableSsl();
        canCall("https://expired.badssl.com/");
    }

    @Test
    public void selfSigned() {
        fails("https://self-signed.badssl.com/",
                SSLHandshakeException.class,
                "javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: " +
                        "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: " +
                        "unable to find valid certification path to requested target");
        disableSsl();
        canCall("https://self-signed.badssl.com/");
    }

    private void disableSsl() {
        Unirest.config().reset().verifySsl(false);
    }

    private void fails(String url, Class<? extends Throwable> exClass, String error) {
        TestUtil.assertExceptionUnwrapped(() -> Unirest.get(url).asEmpty(),
                exClass,
                error);
    }

    private void canCall(String url) {
        assertEquals(200, Unirest.get(url).asEmpty().getStatus());
    }
}
