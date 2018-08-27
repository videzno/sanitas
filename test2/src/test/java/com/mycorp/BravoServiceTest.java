package com.mycorp;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import com.mycorp.services.BravoService;
import com.mycorp.services.ClienteService;
import com.mycorp.services.ZendeskService;
import com.mycorp.support.DatosCliente;
import com.mycorp.support.ValueCode;

import util.datos.UsuarioAlta;

/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class BravoServiceTest {

	@Mock
	RestTemplate restTemplate;

	@Mock
	Zendesk zendesk;

	@Mock
	ClienteService clienteService;

	@InjectMocks
	@Spy
	BravoService bravoService;

	@Mock
	ZendeskService zendeskService;

	DatosCliente datosClienteMock;

	UsuarioAlta usuarioAlta;

	@Before
	public void prepareTests() {

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

	@SuppressWarnings("unchecked")
	@Test
	public void testBravoService() throws Exception {
		ValueCode valueCode = new ValueCode();
		valueCode.setCode("testCode");
		valueCode.setValue("testValue");
		Mockito.when(bravoService.getTiposDocumentosRegistro()).thenReturn(Arrays.asList(valueCode, valueCode));
		Mockito.when(clienteService.getIdCliente(Matchers.any(UsuarioAlta.class), Matchers.any(StringBuilder.class),
				Matchers.any(StringBuilder.class))).thenReturn("testClienteServiceResponse");

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Matchers.any(Class.class), Mockito.anyString()))
				.thenReturn(datosClienteMock);
		StringBuilder altaTicketZendesk = bravoService.getDatosBravo(usuarioAlta, new StringBuilder().append("test"),
				new StringBuilder());
		String resultadoEsperado = "\\nDatos recuperados de BRAVO:\\n\\nTeléfono: null\\nFeha de nacimiento: 21/06/1981\\nNúmero documento: null\\nTipo cliente: POTENCIAL\\nID estado del cliente: null\\nID motivo de alta cliente: null\\nRegistrado: Sí­\\n\\n";
		String resultadoObtenido = altaTicketZendesk.toString();
		Assert.assertEquals(resultadoEsperado, resultadoObtenido);
	}

}
