package com.veersa.training.smartcontactmanager.repositories;

import com.veersa.training.smartcontactmanager.entities.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {
    public MyOrder findByOrderId(String orderId);
}
