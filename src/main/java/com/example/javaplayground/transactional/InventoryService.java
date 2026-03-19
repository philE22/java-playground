package com.example.javaplayground.transactional;

import com.example.javaplayground.transactional.domain.Inventory;
import com.example.javaplayground.transactional.domain.InventoryRepository;
import com.example.javaplayground.transactional.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional
    public void reserve(String sku, int qty, Order order, FailFlag flag) {
        Inventory inventory = repository.findBySku(sku).orElseThrow();
        inventory.decrease(qty);

        if (flag == FailFlag.INVENTORY) throw new RuntimeException("inventory error");
    }
}
