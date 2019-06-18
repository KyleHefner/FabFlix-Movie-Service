package edu.uci.ics.khefner.service.api_gateway.threadpool;

import edu.uci.ics.khefner.service.api_gateway.DatabaseQueries.InsertResponse;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.utilities.SendRequest;

import javax.ws.rs.core.Response;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;

    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        ServiceLogger.LOGGER.info("Creating Worker id#" + id);
        Worker worker = new Worker(id, threadPool);
        return worker;
    }

    public void process() {

        ClientRequest clientRequest = threadPool.remove();
        ServiceLogger.LOGGER.info("Thread with id  #" + id + " got client request: " + clientRequest.toString());

        //need to fire off REST call to other service...
        Response response = SendRequest.SendRequest(clientRequest);

        //need to store response data

        InsertResponse.InsertResponse(clientRequest, response);
    }

    @Override
    public void run() {
        while (true) {
            process();
        }
    }
}
