package no.digipost.api.handlers;

import no.digipost.api.exceptions.MessageSenderIOException;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.transport.http.HttpComponentsConnection;

import java.io.IOException;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EbmsContextAwareWebServiceTemplateTest {

    @Mock
    private WebServiceMessage requestMock;

    @Mock
    private HttpComponentsConnection connectionMock;

    @Mock
    private HttpResponse mockResponse;

    @Test
    public void skal_ikke_feile_selv_om_entity_mangler() throws IOException {
        EbmsContextAwareWebServiceTemplate template = new EbmsContextAwareWebServiceTemplate(null, null);
        when(connectionMock.getHttpResponse()).thenReturn(mockResponse);
        assertThrows(MessageSenderIOException.class, () -> template.handleError(connectionMock, requestMock));
    }

}
