package com.mycorp;

import com.mycorp.services.ClienteService;

import portalclientesweb.ejb.interfaces.PortalClientesWebEJBRemote;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import util.datos.DatosPersonales;
import util.datos.DetallePoliza;
import util.datos.PolizaBasico;
import util.datos.UsuarioAlta;

/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClienteServiceTest {

	@Mock
	RestTemplate restTemplate;
	@Mock
	private PortalClientesWebEJBRemote portalclientesWebEJBRemote;

	@InjectMocks
	@Spy
	ClienteService clienteService;

	UsuarioAlta usuarioAlta;

	@Before
	public void prepareTests() {

		clienteService.TARJETAS_GETDATOS = "test.TARJETAS_GETDATOS";

		usuarioAlta = new UsuarioAlta();
		usuarioAlta.setNumTarjeta("123456789");

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testClienteService() throws Exception {

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Matchers.any(Class.class)))
				.thenReturn(new ResponseEntity("testClienteServiceResponse", HttpStatus.OK));
		String altaTicketZendesk = clienteService.getIdCliente(usuarioAlta, new StringBuilder().append("test"),
				new StringBuilder());
		Assert.assertEquals(altaTicketZendesk, "testClienteServiceResponse");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testClienteService2() throws Exception {

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Matchers.any(Class.class)))
				.thenReturn(new ResponseEntity("testClienteServiceResponse", HttpStatus.OK));

		util.datos.DetallePoliza detallePolizaResponse = new DetallePoliza();
		DatosPersonales tomador = new DatosPersonales();
		tomador.setNombre("Tomas");
		tomador.setApellido1("Tomadorez");
		tomador.setApellido2("Tomador");
		tomador.setIdentificador("00000001");
		detallePolizaResponse.setTomador(tomador);
		Mockito.when(portalclientesWebEJBRemote.recuperarDatosPoliza((PolizaBasico) Mockito.anyObject()))
				.thenReturn(detallePolizaResponse);

		usuarioAlta.setNumTarjeta("");
		usuarioAlta.setNumPoliza("1234567890");
		usuarioAlta.setNumDocAcreditativo("123456789");
		String id = clienteService.getIdCliente(usuarioAlta, new StringBuilder().append("test"), new StringBuilder());
		Assert.assertEquals(id, "00000001");
	}

}
