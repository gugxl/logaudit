package com.gugu.log.test;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;

/**
 * @author gugu
 * @Classname AvroClient
 * @Description TODO
 * @Date 2019/12/31 10:50
 */
public class AvroClient {
    public static void main(String[] args) {

    }


}
class MyRpcClientFacade{
    private RpcClient client;
    private String hostname;
    private int port;

    public void init(String hostname,int port){
        // Setup the RPC connection
        this.hostname = hostname;
        this.port = port;
        // Use the following method to create a thrift client (instead of the
        // above line):
        this.client = RpcClientFactory.getDefaultInstance(hostname,port);
    }
    public void sendDataToFlume(String data){
        // Create a Flume Event object that encapsulates the sample data
        Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
        // Send the event
        try {
            client.append(event);
        } catch (EventDeliveryException e) {
            // clean up and recreate the client
            client.close();
            client = null;
            // Use the following method to create a thrift client (instead of
            // the above line):
            client = RpcClientFactory.getDefaultInstance(hostname, port);
        }
    }
    public void cleanUp(){
        // Close the RPC connection
        client.close();
    }

}