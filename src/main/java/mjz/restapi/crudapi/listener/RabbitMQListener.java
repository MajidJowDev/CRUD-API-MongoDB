package mjz.restapi.crudapi.listener;

import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    /*
    @RabbitListener(queues = RabbitMQConfig.MESSAGE_QUEUE)
    public void listen(UserDTO receivedMessage) {
        System.out.println("Message read from myQueue : " + receivedMessage);
    }

     */

    @RabbitListener(queues = RabbitMQConfig.MESSAGE_QUEUE)
    public void listen(String receivedMessage) {
        System.out.println("Message read from myQueue : " + receivedMessage);
    }




}
