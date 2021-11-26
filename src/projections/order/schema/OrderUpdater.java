package projections.order.schema;

import events.schema.Event;
import events.schema.EventType;
import projections.common.Status;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public interface OrderUpdater {
    void update(Order order, Event event);

    static OrderUpdater getUpdaterForEvent(EventType type) {
        switch (type) {
            case PLACE_ORDER:
                return new PlaceEventUpdater();
            case ARTICLE_VALIDATION:
                return new ArticleValidationUpdater();
            case PAYMENT:
                return new PaymentUpdater();
            case CANCEL_ORDER:
                return new CancelUpdater();
        }
        return new VoidEventUpdater();
    }

    class PlaceEventUpdater implements OrderUpdater {
        @Override
        public void update(Order order, Event event) {
            order.id = event.getOrderId();
            order.userId = event.getPlaceEvent().getUserId();
            order.cartId = event.getPlaceEvent().getCartId();
            order.status = Status.PLACED;

            order.articles = Arrays.stream(event.getPlaceEvent().getArticles()) //
                    .map(article -> new Order.Article(article.getArticleId(), article.getQuantity())) //
                    .collect(Collectors.toList()) //
                    .toArray(new Order.Article[]{});

            order.updated = new Date();
        }
    }

    class ArticleValidationUpdater implements OrderUpdater {
        @Override
        public void update(Order order, Event event) {
            Arrays.stream(order.articles) //
                    .filter(a -> a.id.equals(event.getArticleValidationEvent().getArticleId())) //
                    .forEach(a -> {
                        a.valid = event.getArticleValidationEvent().isValid();
                        a.unitaryPrice = event.getArticleValidationEvent().getPrice();
                        a.validated = true;
                    });

            if (!event.getArticleValidationEvent().isValid()) {
                order.status = Status.INVALID;
            }

            if (Arrays.stream(order.articles).filter(a -> a.isValid()).count() == order.articles.length) {
                // Todos validos cambiamos el estado de la orden
                order.status = Status.VALIDATED;
            }

            order.updated = new Date();
        }
    }

    class PaymentUpdater implements OrderUpdater {
        @Override
        public void update(Order order, Event event) {
            Order.Payment[] payments = new Order.Payment[order.payment.length + 1];

            for (int i = 0; i < order.payment.length; i++) {
                payments[i] = order.payment[i];
            }
            payments[payments.length - 1] = new Order.Payment(event.getPayment().getMethod(),
                    event.getPayment().getAmount());

            order.payment = payments;

            if (order.getTotalPayment() >= order.getTotalPrice()) {
                order.status = Status.PAYMENT_DEFINED;
            }
        }
    }

    class CancelUpdater implements OrderUpdater {
        @Override
        public void update(Order order, Event event) {
            order.status = Status.CANCELED;
        }
    }


    class VoidEventUpdater implements OrderUpdater {
        @Override
        public void update(Order order, Event event) {

        }
    }

}
