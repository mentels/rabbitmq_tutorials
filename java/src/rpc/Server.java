package rpc;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Worker
 */
public class Server {

    private final static String TASK_QUEUE_NAME = "rpc_task_queue";
    private final static int PREFETCH_COUNT = 1;

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(PREFETCH_COUNT);

        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for tasks at queue: '" + TASK_QUEUE_NAME + "'. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            int num = Integer.parseInt(message);
            System.out.println(" [x] Computing fib(" + num + ")");
            try {
                int result = fibonacci(num);
                byte[] response = Integer.toString(result).getBytes("UTF-8");
                String replyQueue = delivery.getProperties().getReplyTo();
                BasicProperties props = new BasicProperties().builder()
                        .correlationId(delivery.getProperties().getCorrelationId()).build();
                channel.basicPublish("", replyQueue, props, response);
                System.out.println(" [x] Sent response fbi(" + num + ")=" + result);
            } catch (Exception e) {
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true); // 2nd argument is for multiple; 3rd for redeliver
            } finally {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); // 2nd argument is for multiple
            }
        };

        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
        });
    }

    private static int fibonacci(int num) throws Exception {
        if (num == 0 || num == 1) {
            return 1;
        } else if (num > 1) {
            return fibonacci(num - 2) + fibonacci(num - 1);
        }
        throw (new IllegalArgumentException("Cannot compute fib for negative numbers"));
    }
}
