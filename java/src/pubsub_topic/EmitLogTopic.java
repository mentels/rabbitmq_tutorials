package pubsub_topic;

import java.util.Arrays;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {

    private final static String LOGS_EXCHANGE = "topic_logs";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {  
                channel.exchangeDeclare(LOGS_EXCHANGE, BuiltinExchangeType.TOPIC);
                String source = getSource(args);
                String severity = getSeverity(args);
                String message = getMessage(args); 
                String routingKey = source + "." + severity;
                channel.basicPublish(LOGS_EXCHANGE, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "' with rk '" + routingKey + "'");
        }   
    }

    private static String getSource(String[] args) {
        return args[0];
    }

    private static String getMessage(String[] args) {
        return String.join(" ", Arrays.copyOfRange(args, 2, args.length));
    }

    private static String getSeverity(String[] args) {
        return args[1];
    }
}