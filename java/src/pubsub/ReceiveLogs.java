package pubsub;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * Worker
 */
public class ReceiveLogs {

    private final static String LOGS_EXCHANGE = "logs";
    private final static int PREFETCH_COUNT = 1;

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(PREFETCH_COUNT);

        channel.exchangeDeclare(LOGS_EXCHANGE, BuiltinExchangeType.FANOUT);

        String queue = channel.queueDeclare("", false, true, false, null).getQueue();
        channel.queueBind(queue, LOGS_EXCHANGE, "", null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received: '" + message + "'");
            try {
                doWork(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); // 2nd argument is for multiple
            }
        };

        boolean autoAck = false;
        channel.basicConsume(queue, autoAck, deliverCallback, consumerTag -> {});
    }

    private static void doWork(String message) throws InterruptedException {
        for (char ch : message.toCharArray()) {
            if (ch == '.') 
                Thread.sleep(3000);
        }
  }
}
