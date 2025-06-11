package com.store.api.order.model;

import java.util.List;
import lombok.Getter;

@Getter
public class OrderRequest {
    private List<FinalizedOrderDetailRequest> orderDetailRequests;
    private boolean applyMembershipDiscount;
}
