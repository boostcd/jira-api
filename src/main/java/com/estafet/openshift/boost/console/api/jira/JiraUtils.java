package com.estafet.openshift.boost.console.api.jira;

import javax.naming.AuthenticationException;

import com.sun.jersey.core.util.Base64;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JiraUtils 
{
	private static final Logger logger = LogManager.getLogger(JiraUtils.class);

	public static String invokeGetMethod(String auth, String url)
			throws AuthenticationException, ClientHandlerException {
		String cred = new String(Base64.encode(auth));
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.header("Authorization", "Basic " + cred).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		if(response == null){
			logger.error("no any Jira's response");
			return null;
		}
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		if(statusCode == 403) {
			throw new AuthenticationException("Basic auth with password is not allowed on this instance");
		}
		return response.getEntity(String.class);
	}

}
