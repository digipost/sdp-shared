package no.digipost.api.handlers;

import no.digipost.api.exceptions.MessageSenderIOException;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.transport.http.HttpComponentsConnection;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EbmsContextAwareWebServiceTemplateTest {

    @Mock
    private WebServiceMessage requestMock;

    @Mock
    private HttpComponentsConnection connectionMock;

    @Mock
    private HttpResponse mockResponse;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test(expected = MessageSenderIOException.class)
    public void skal_ikke_feile_selv_om_entity_mangler() throws IOException {
        EbmsContextAwareWebServiceTemplate template = new EbmsContextAwareWebServiceTemplate(null, null);
        when(connectionMock.getHttpResponse()).thenReturn(mockResponse);
        template.handleError(connectionMock, requestMock);
    }

}
