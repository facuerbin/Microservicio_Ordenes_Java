package events.schema;

import com.google.gson.annotations.SerializedName;
import utils.gson.Builder;
import utils.validator.MinLen;
import utils.validator.Required;

public class CancelData {
    @SerializedName("orderId")
    public String orderId;

    @SerializedName("userId")
    @MinLen(1)
    public String userId;

    @SerializedName("reason")
    @Required
    public CancelOrderEvent.Reason reason;

    public static CancelData fromJson(String json) {
        return Builder.gson().fromJson(json, CancelData.class);
    }
}
