package info.order.services;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import info.order.domain.Address;
import info.order.domain.Customer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;


@Stateless
@Path("/customers")
public class CustomerResource {
	
	@PersistenceContext(unitName="orderModel")
	EntityManager entityManager;
	

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response createCustomer(InputStream is) {
		Customer customer = readCustomer(is);
		System.out.println("entityManager: " + entityManager);
		entityManager.persist(customer);
		
		
		System.out.println("Created customer " + customer.getId());

		return Response.created(URI.create("/customers/" + customer.getId())).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("{id}")
	public StreamingOutput getCustomer(@PathParam("id") int id) {
		long  key = id;
		final Customer customer = entityManager.find(Customer.class, key);
		if (customer == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return new StreamingOutput() {
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				outputCustomer(outputStream, customer);
			}
		};
	}
	
//	@PUT
//	@Consumes(MediaType.APPLICATION_XML)
//	@Path("{id}")
//	public void updateCustomer(@PathParam("id") int id, InputStream is) {
//		Customer update = readCustomer(is);
//		Customer current = customerDB.get(id);
//		if (current == null)
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		current.setFirstName(update.getFirstName());
//		current.setLastName(update.getLastName());
//		current.setStreet(update.getStreet());
//		current.setState(update.getState());
//		current.setZip(update.getZip());
//		current.setCountry(update.getCountry());
//	}

	protected void outputCustomer(OutputStream os, Customer cust) throws IOException {
	      PrintStream writer = new PrintStream(os);
	      writer.println("<customer id=\"" + cust.getId() + "\">");
	      writer.println("   <first-name>" + cust.getFirstName() + "</first-name>");
	      writer.println("   <last-name>" + cust.getLastName() + "</last-name>");
	      writer.println("   <street>" + cust.getAddress().getStreet() + "</street>");
	      writer.println("   <city>" + cust.getAddress().getCity() + "</city>");
	      writer.println("   <zip>" + cust.getAddress().getZipCode() + "</zip>");
	      writer.println("</customer>");
	   }

	protected Customer readCustomer(InputStream is) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(is);
			Element root = doc.getDocumentElement();
			Customer cust = new Customer();
			Address address = new Address();
			if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals(""))
				cust.setId(Integer.valueOf(root.getAttribute("id")));
			NodeList nodes = root.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nodes.item(i);
					if (element.getTagName().equals("first-name")) {
						cust.setFirstName(element.getTextContent());
					} else if (element.getTagName().equals("last-name")) {
						cust.setLastName(element.getTextContent());
					} else if (element.getTagName().equals("street")) {
						address.setStreet(element.getTextContent());
					} else if (element.getTagName().equals("city")) {
						address.setCity(element.getTextContent());
					} else if (element.getTagName().equals("zip")) {
						address.setZipCode(element.getTextContent());
					} 
				}
			}
			cust.setAddress(address);
			return cust;
		} catch (Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}
}
