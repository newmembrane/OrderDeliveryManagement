package pizza;

import org.springframework.beans.BeanUtils;
import pizza.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Service
public class PolicyHandler{

    @Autowired OrderDeliveryRepository orderDeliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentApproved_OrderPlace(@Payload PaymentApproved paymentApproved){

        if(paymentApproved.isMe()){
            System.out.println("##### listener OrderPlace : " + paymentApproved.toJson());
            OrderDelivery orderDelivery = new OrderDelivery();
            BeanUtils.copyProperties(paymentApproved, orderDelivery);
            orderDelivery.setId(paymentApproved.getOrderId());
            orderDelivery.setOrderState("PizzaProductionReady");
            orderDelivery.setPaymentDate(paymentApproved.getTimestamp());
            orderDelivery.setLastEventDate(paymentApproved.getTimestamp());
            System.out.println("##### Command [StartPizzaProduction] activated by PolicyHandler");
            System.out.println(MessageFormat.format("###### /{0}/{1}/{2}/{3}/"
                    , orderDelivery.getId(), orderDelivery.getCustomerId(), orderDelivery.getAddress(), orderDelivery.getLastEventDate()));
            orderDeliveryRepository.save(orderDelivery);
        }
    }

}
