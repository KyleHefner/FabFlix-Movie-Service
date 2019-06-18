package edu.uci.ics.khefner.service.api_gateway.threadpool;

import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;

public class ClientRequestQueue {
    private ListNode head;
    private ListNode tail;
    private int size;

    public ClientRequestQueue() {
        head = tail = null;
        size = 0;
    }

    public synchronized void enqueue(ClientRequest clientRequest) {

        ServiceLogger.LOGGER.info("enqueue: " + clientRequest.toString());
        //create a listNode...
        ListNode temp = new ListNode(clientRequest);
        //if the queue is empty...
        if(isEmpty()){
            head = tail = temp;

        }
        else{
            // Add new node at the end of the queue and change tail...
            tail.setNext(temp);
            tail = temp;

        }
        //should call notify() here to notify other threads that the queue is not empty anymore...
        notifyAll();
        return;
    }

    public synchronized ClientRequest dequeue()  {

        //if queue is empty, wait until another thread notifies that it has added to the queue
        try {
            ListNode temp = head;
            while (isEmpty()) {
                wait();
            }
            ClientRequest clientRequest = head.getClientRequest();
            head = head.getNext();

            if (isEmpty()) {
                tail = null;
            }
            ServiceLogger.LOGGER.info("dequeue: " + clientRequest.toString());
            return clientRequest;
        } catch (InterruptedException e){
            ServiceLogger.LOGGER.info("ERROR using wait() in dequeue()...");
            e.printStackTrace();

        }

        return new ClientRequest();

    }

    boolean isEmpty() {
        return head == null;

    }
}
