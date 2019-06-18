package edu.uci.ics.khefner.service.movies.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeRequestModel;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class CheckPrivilege {
    public static boolean hasPrivilege(String email, int plevel) {
        ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = MovieService.getMovieConfigs().getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = MovieService.getMovieConfigs().getIdmConfigs().getPrivilegePath();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        VerifyPrivilegeRequestModel requestModel2 = new VerifyPrivilegeRequestModel(email, plevel);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel2, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");
        ServiceLogger.LOGGER.info("response content: " + response.toString());
        // Check that status code of the response
        if (response.getStatus() == 200) {
            ServiceLogger.LOGGER.info("Received Status 200");
            // Success! Map the response to a ResponseModel

            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("Json text: " + jsonText);
            VerifyPrivilegeResponseModel responseModel;
            ObjectMapper mapper = new ObjectMapper();
            try {
                responseModel = mapper.readValue(jsonText, VerifyPrivilegeResponseModel.class);
                if(responseModel.getResultCode() == 140){
                    ServiceLogger.LOGGER.info("User has sufficient privilege");
                    return true;}
                else{
                    ServiceLogger.LOGGER.info("User has insufficient privilege");
                    return false;}
            }catch (IOException e) {
                e.printStackTrace();
            }
            ServiceLogger.LOGGER.info("Error when verifying privilege. Verify privilege returned response other than Sufficient or Insufficient");
            return false;

        } else {
            ServiceLogger.LOGGER.info("Received Status " + response.getStatus() + " -- you lose.");
            return false;
        }
    }
}
