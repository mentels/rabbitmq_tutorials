package rpc;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Client {

    private final static String TASK_QUEUE_NAME = "rpc_task_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
        String replyQueue = channel.queueDeclare("", false, true, false, null).getQueue();
        String message = Integer.toString(Integer.parseInt(args[0]));
        BasicProperties props = new BasicProperties().builder().replyTo(replyQueue).build();
        channel.basicPublish("", TASK_QUEUE_NAME, props, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        channel.basicConsume(replyQueue, true, deliverCallback(message, props.getCorrelationId()), (consumerTag) -> {
        });
    }

    public static DeliverCallback deliverCallback(String message, String correlationId) {
        return (consumerTag, delivery) -> {
            String response = new String(delivery.getBody(), "UTF-8");
            if (delivery.getProperties().getCorrelationId() == correlationId) {
                System.out.println(" [x] Got response for message: '" + message + "': '" + response + "'");
                System.exit(1);
            } else {
                System.out.println(" [x] Unexpected response: '" + response + "'");
            }
        };
    }
}