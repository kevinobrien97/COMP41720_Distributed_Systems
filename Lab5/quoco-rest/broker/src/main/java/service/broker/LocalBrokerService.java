package service.broker;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import service.core.ClientInfo;
import service.core.Quotation;
import service.core.ClientApplication;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import java.net.URISyntaxException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;


/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */
@RestController
public class LocalBrokerService {
	static long SEED_ID = 0;

	// hashmap to associate an ID with the ClientApplication
	static Map<Long, ClientApplication> cache = new HashMap<>();

	// set up applications URI to post quotation resources 
	@RequestMapping(value="/applications",method=RequestMethod.POST)
		public ResponseEntity<ClientApplication> postQuotations(@RequestBody ClientInfo info) {
			ClientApplication application = new ClientApplication(SEED_ID++, info);
			// long SEED_ID = application.getID();
			// ClientInfo info = application.getClientInfo();
			// ClientApplication applicationReturned = getQuotations(application);
			
			ClientApplication returnedApp = getQuotations(application);
			
			// build URI path
			String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/applications/";
			HttpHeaders headers = new HttpHeaders();
			try {
				headers.setLocation(new URI(path));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			// set up response to post request
			return new ResponseEntity<>(returnedApp, headers, HttpStatus.CREATED);
		}

	public ClientApplication getQuotations(ClientApplication application) {
		ClientInfo info = application.info;

		RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ClientInfo> request = new HttpEntity<>(info);
		Quotation quotation =
		restTemplate.postForObject("http://localhost:8081/quotations",
			request, Quotation.class);
		application.quotations.add(quotation);
		Quotation quotation1 =
			restTemplate.postForObject("http://localhost:8082/quotations",
				request, Quotation.class);
		application.quotations.add(quotation1);
		Quotation quotation2 =
			restTemplate.postForObject("http://localhost:8083/quotations",
				request, Quotation.class);
		application.quotations.add(quotation2);
		

		return application;
	}

	// set up applications URI to post quotation resources 
	@RequestMapping(value="/applications/{application_number}",method=RequestMethod.GET)
		public ResponseEntity<ClientApplication> getQuotationByID(@PathVariable String application_number) throws URISyntaxException {
			long id = Long.parseLong(application_number);
			ClientApplication application = cache.get(id);
						
			// build URI path
			String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/applications/";
			HttpHeaders headers = new HttpHeaders();
			try {
				headers.setLocation(new URI(path));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			// set up response to post request
			return new ResponseEntity<>(application, headers, HttpStatus.CREATED);
		}

			// set up applications URI to post quotation resources 
	@RequestMapping(value="/applications/",method=RequestMethod.GET)
	public ResponseEntity<List<ClientApplication>> getQuotations() throws URISyntaxException {
		List<ClientApplication> apps = new ArrayList<>(); 
		for (ClientApplication app : cache.values()) {
			apps.add(app);
		}
					
		// build URI path
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/applications/";
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation(new URI(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// set up response to post request
		return new ResponseEntity<>(apps, headers, HttpStatus.CREATED);
	}
}
