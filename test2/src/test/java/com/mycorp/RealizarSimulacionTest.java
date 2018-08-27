package com.mycorp;

import com.mycorp.services.BravoService;
import com.mycorp.services.ClienteService;
import com.mycorp.services.ZendeskService;
import com.mycorp.support.DatosCliente;
import com.mycorp.Zendesk;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import util.datos.UsuarioAlta;


/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealizarSimulacionTest {


    @Mock
    RestTemplate restTemplate;

    @Mock
    Zendesk zendesk;

    @Mock
    ClienteService clienteService;

    @Mock
    BravoService bravoService;

    @InjectMocks
    @Spy
    ZendeskService zendeskService;

    DatosCliente datosClienteMock;

    UsuarioAlta usuarioAlta;


    @Before
    public void intTests() {

        zendeskService.CLIENTE_GETDATOS = "test.CLIENTE_GETDATOS";
        zendeskService.PETICION_ZENDESK = "%s %s %s";

        zendeskService.ZENDESK_ERROR_DESTINATARIO = "test.ZENDESK_ERROR_DESTINATARIO";
        zendeskService.ZENDESK_ERROR_MAIL_FUNCIONALIDAD = "ZENDESK_ERROR_MAIL_FUNCIONALIDAD";

        zendesk = new Zendesk(null, "http://localhost", "admin", "admin");

        clienteService.TARJETAS_GETDATOS = "test.TARJETAS_GETDATOS";

        datosClienteMock = new DatosCliente();
        datosClienteMock.setGenTTipoCliente(1);
        datosClienteMock.setFechaNacimiento("21/06/1981");
        datosClienteMock.setGenCTipoDocumento(1);


        usuarioAlta = new UsuarioAlta();
        usuarioAlta.setNumTarjeta("123456789");

    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testZendeskService() throws Exception {
        Mockito.when(clienteService.getIdCliente(Matchers.any(UsuarioAlta.class), Matchers.any(StringBuilder.class), Matchers.any(StringBuilder.class)))
                .thenReturn("testClienteServiceResponse");

        Mockito.when(bravoService.getDatosBravo(Matchers.any(UsuarioAlta.class), Matchers.any(StringBuilder.class), Matchers.any(StringBuilder.class)))
                .thenReturn(new StringBuilder().append("\\nDatos recuperados de BRAVO:\\n\\nTeléfono: null\\nFeha de nacimiento: 04/05/1984\\nNúmero documento: null\\nTipo cliente: POTENCIAL\\nID estado del cliente: null\\nID motivo de alta cliente: null\\nRegistrado: Sí\\n\\n"));

        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Matchers.any(Class.class), Mockito.anyString())
        ).thenReturn(datosClienteMock);
        String altaTicketZendesk = zendeskService.altaTicketZendesk(usuarioAlta, "test");
        Assert.assertEquals(altaTicketZendesk.toString(), "Nº tarjeta Sanitas o Identificador: 123456789\\nTipo documento: 0\\nNº documento: null\\nEmail personal: null\\nNº móvil: null\\nUser Agent: test\\n\\nDatos recuperados de BRAVO:\\n\\nTeléfono: null\\nFeha de nacimiento: 04/05/1984\\nNúmero documento: null\\nTipo cliente: POTENCIAL\\nID estado del cliente: null\\nID motivo de alta cliente: null\\nRegistrado: Sí\\n\\n");
    }


}
