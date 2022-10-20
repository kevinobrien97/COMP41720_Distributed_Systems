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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpEntity;


/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */
@RestController
public class LocalBrokerService {
	// initialise variable to identify applications for specific clients
	static long SEED_ID = 0;

	// hashmap to associate an ID with the ClientApplication
	static Map<Long, ClientApplication> cache = new HashMap<>();

	// set up applications URI to post quotation resources
	// it was an option to post ClientApplications instead of ClientInfo to this endpoint
	// however this would involve assigning IDs in the client, which if there were multiple clients would not make sense
	@RequestMapping(value="/applications",method=RequestMethod.POST)
		public ResponseEntity<ClientApplication> postQuotations(@RequestBody ClientInfo info) {
			// create a new application with the current seed value and the client info
			ClientApplication application = new ClientApplication(SEED_ID++, info);
			
			// send the getQuotations method an application and store the return in a ClientApplication object
			ClientApplication returnedApp = getQuotations(application);
			// add application to brokers cache to be used in GET requests
			cache.put(returnedApp.id, returnedApp);
			// build URI path
			String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/applications/";
			HttpHeaders headers = new HttpHeaders();
			try {
				headers.setLocation(new URI(path));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			// set up response to post request, returning the completed application
			return new ResponseEntity<>(returnedApp, headers, HttpStatus.CREATED);
		}
	
	// method to reach out to the quotation services with client info to request 
	public ClientApplication getQuotations(ClientApplication application) {
		// extract the info
		ClientInfo info = application.info;
		// set up rest template
		RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ClientInfo> request = new HttpEntity<>(info);
		// retreive quotation responses from each of the quotation services and add the quote to the ClientApplication object
		Quotation quotation =
		restTemplate.postForObject("http://auldfellas:8081/quotations",
			request, Quotation.class);
		application.quotations.add(quotation);
		Quotation quotation1 =
			restTemplate.postForObject("http://dodgydrivers:8082/quotations",
				request, Quotation.class);
		application.quotations.add(quotation1);
		Quotation quotation2 =
			restTemplate.postForObject("http://girlpower:8083/quotations",
				request, Quotation.class);
		application.quotations.add(quotation2);
		

		return application;
	}

	// set up applications URI to GET quotation resources by application number
	@RequestMapping(value="/applications/{application_number}",method=RequestMethod.GET)
		public ResponseEntity<ClientApplication> getQuotationByID(@PathVariable String application_number) throws URISyntaxException {
			// parse the app number as a long
			long id = Long.parseLong(application_number);

			// if an incorrect ID has been passed
			if (!cache.containsKey(id)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application does not exist for this ID.");
			}

			// get the relevant application from the cache to be returned
			ClientApplication application = cache.get(id);
						
			// build URI path
			String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/applications/";
			HttpHeaders headers = new HttpHeaders();
			try {
				headers.setLocation(new URI(path));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			// set up response to GET request with the specified application
			return new ResponseEntity<>(application, headers, HttpStatus.CREATED);
		}

	// set up applications URI to GET all quotation resources 
	@RequestMapping(value="/applications",method=RequestMethod.GET)
	public ResponseEntity<List<ClientApplication>> getAllQuotations() throws URISyntaxException {
		// if no applications
		if (cache.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applications have been received.");
		}

		// set up list to be returned
		List<ClientApplication> apps = new ArrayList<>();
		// add all applications to the list
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

		// set up response to GET request with the list of applications
		return new ResponseEntity<>(apps, headers, HttpStatus.CREATED);
	}
}
