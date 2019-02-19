package pubsub;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {

    private final static String LOGS_EXCHANGE = "logs";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {  
                channel.exchangeDeclare(LOGS_EXCHANGE, BuiltinExchangeType.FANOUT);
                String message = String.join(" ", args);
                channel.basicPublish(LOGS_EXCHANGE, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent' " + message + "'");
        }   
    }   
}