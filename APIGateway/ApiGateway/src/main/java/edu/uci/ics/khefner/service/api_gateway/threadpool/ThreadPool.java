package edu.uci.ics.khefner.service.api_gateway.threadpool;

public class ThreadPool {
    private int numWorkers;
    private Worker[] workers;
    private ClientRequestQueue queue;

    public ThreadPool(int numWorkers) {
        this.queue = new ClientRequestQueue();
        this.workers = new Worker[numWorkers];
        for(int i = 0; i < numWorkers; i++){
            Worker worker = Worker.CreateWorker(i, this);
            worker.start();
            workers[i] = worker;

        }
    }

    public void add(ClientRequest clientRequest) { queue.enqueue(clientRequest); }

    public ClientRequest remove() { return queue.dequeue(); }

    public ClientRequestQueue getQueue() { return queue; }
}
