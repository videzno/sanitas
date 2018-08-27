package com.mycorp.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mycorp.Zendesk;
import com.mycorp.exceptions.ZendeskException;
import com.mycorp.support.CorreoElectronico;
import com.mycorp.support.MensajeriaService;
import com.mycorp.support.Ticket;

import util.datos.UsuarioAlta;

@Service
public class ZendeskService {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger( ZendeskService.class );

    private static final String ESCAPED_LINE_SEPARATOR = "\\n";
    private static final String ESCAPE_ER = "\\";
    private static final String HTML_BR = "<br/>";
    @Value("#{envPC['zendesk.ticket']}")
    public String PETICION_ZENDESK= "";

    @Value("#{envPC['zendesk.token']}")
    public String TOKEN_ZENDESK= "";

    @Value("#{envPC['zendesk.url']}")
    public String URL_ZENDESK= "";

    @Value("#{envPC['zendesk.user']}")
    public String ZENDESK_USER= "";

    @Value("#{envPC['tarjetas.getDatos']}")
    public String TARJETAS_GETDATOS = "";

    @Value("#{envPC['cliente.getDatos']}")
    public String CLIENTE_GETDATOS = "";

    @Value("#{envPC['zendesk.error.mail.funcionalidad']}")
    public String ZENDESK_ERROR_MAIL_FUNCIONALIDAD = "";

    @Value("#{envPC['zendesk.error.destinatario']}")
    public String ZENDESK_ERROR_DESTINATARIO = "";

 

    /** The rest template. */
    @Autowired
    @Qualifier("restTemplateUTF8")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier( "emailService" )
    MensajeriaService emailService;
    
    @Autowired
    BravoService bravoService;
    
    @Autowired
    Zendesk zendesk;
    
    private ObjectMapper mapper = new ObjectMapper();
    

    /**
     * Crea un ticket en Zendesk. Si se ha informado el nº de tarjeta, obtiene los datos asociados a dicha tarjeta de un servicio externo.
     * @param usuarioAlta
     * @param userAgent
     */
    public String altaTicketZendesk(UsuarioAlta usuarioAlta, String userAgent){

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        
        StringBuilder clientName = new StringBuilder();
      
        
        StringBuilder datosUsuario = getDatosUsuario(usuarioAlta, userAgent);

       
        StringBuilder datosServicio = new StringBuilder();
        
        StringBuilder datosBravo = bravoService.getDatosBravo(usuarioAlta, clientName, datosServicio);

        String ticket = String.format(PETICION_ZENDESK, clientName.toString(), usuarioAlta.getEmail(), datosUsuario.toString()+datosBravo.toString()+
                parseJsonBravo(datosServicio));
        ticket = ticket.replaceAll("["+ESCAPED_LINE_SEPARATOR+"]", " ");

        try{
            //Ticket
            Ticket petiZendesk = mapper.readValue(ticket, Ticket.class);
            zendesk.createTicket(petiZendesk);

        }catch(Exception e){
            LOG.error("Error al crear ticket ZENDESK", e);
            // Send email

            CorreoElectronico correo = new CorreoElectronico( Long.parseLong(ZENDESK_ERROR_MAIL_FUNCIONALIDAD), "es" )
                    .addParam(datosUsuario.toString().replaceAll(ESCAPE_ER+ESCAPED_LINE_SEPARATOR, HTML_BR))
                    .addParam(datosBravo.toString().replaceAll(ESCAPE_ER+ESCAPED_LINE_SEPARATOR, HTML_BR));
            correo.setEmailA( ZENDESK_ERROR_DESTINATARIO );
            try
            {
                emailService.enviar( correo );
            }catch(Exception ex){
                LOG.error("Error al enviar mail", ex);
                throw new ZendeskException("Error al enviar el mail");
            }

        }

        datosUsuario.append(datosBravo);

        return datosUsuario.toString();
    }
   
	
	
	
	
	
    /***
     * Añade los datos del formulario
     * @param usuarioAlta
     * @param userAgent
     * @return
     */
	private StringBuilder getDatosUsuario(UsuarioAlta usuarioAlta, String userAgent) {
		 StringBuilder datosUsuario = new StringBuilder();
		if(StringUtils.isNotBlank(usuarioAlta.getNumPoliza())){
            datosUsuario.append("Nº de poliza/colectivo: ").append(usuarioAlta.getNumPoliza()).append("/").append(usuarioAlta.getNumDocAcreditativo()).append(ESCAPED_LINE_SEPARATOR);
        }else{
            datosUsuario.append("Nº tarjeta Sanitas o Identificador: ").append(usuarioAlta.getNumTarjeta()).append(ESCAPED_LINE_SEPARATOR);
        }
        datosUsuario.append("Tipo documento: ").append(usuarioAlta.getTipoDocAcreditativo()).append(ESCAPED_LINE_SEPARATOR);
        datosUsuario.append("Nº documento: ").append(usuarioAlta.getNumDocAcreditativo()).append(ESCAPED_LINE_SEPARATOR);
        datosUsuario.append("Email personal: ").append(usuarioAlta.getEmail()).append(ESCAPED_LINE_SEPARATOR);
        datosUsuario.append("Nº móvil: ").append(usuarioAlta.getNumeroTelefono()).append(ESCAPED_LINE_SEPARATOR);
        datosUsuario.append("User Agent: ").append(userAgent).append(ESCAPED_LINE_SEPARATOR);
        return datosUsuario;
	}

    

    /**
     * MÃ©todo para parsear el JSON de respuesta de los servicios de tarjeta/pÃ³liza
     *
     * @param resBravo
     * @return
     */
    private String parseJsonBravo(StringBuilder resBravo)
    {
        return resBravo.toString().replaceAll("[\\[\\]\\{\\}\\\"\\r]", "").replaceAll(ESCAPED_LINE_SEPARATOR, ESCAPE_ER + ESCAPED_LINE_SEPARATOR);
    }
}