package projections.orderStatus.schema;

import events.schema.Event;
import events.schema.EventType;
import projections.common.Status;

import java.util.Date;

public interface OrderStatusUpdater {
    void update(OrderStatus order, Event event);

    static OrderStatusUpdater getUpdaterForEvent(EventType type) {
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

    class PlaceEventUpdater implements OrderStatusUpdater {
        @Override
        public void update(OrderStatus order, Event event) {
            order.id = event.getOrderId();
            order.userId = event.getPlaceEvent().getUserId();
            order.cartId = event.getPlaceEvent().getCartId();
            order.status = Status.PLACED;
            order.articles = event.getPlaceEvent().getArticles().length;
            order.updated = new Date();
        }
    }

    class ArticleValidationUpdater implements OrderStatusUpdater {
        @Override
        public void update(OrderStatus order, Event event) {
            if (!event.getArticleValidationEvent().isValid()) {
                order.status = Status.INVALID;
            }

            order.updated = new Date();
        }
    }

    class PaymentUpdater implements OrderStatusUpdater {
        @Override
        public void update(OrderStatus order, Event event) {
            order.payment += event.getPayment().getAmount();
        }
    }

    class CancelUpdater implements OrderStatusUpdater {
        @Override
        public void update(OrderStatus order, Event event) {
            order.status = Status.CANCELED;
            order.updated = new Date();
        }
    }

    class VoidEventUpdater implements OrderStatusUpdater {
        @Override
        public void update(OrderStatus order, Event event) {

        }
    }

}
