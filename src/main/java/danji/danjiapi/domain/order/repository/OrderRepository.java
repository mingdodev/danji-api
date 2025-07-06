package danji.danjiapi.domain.order.repository;

import danji.danjiapi.domain.order.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems WHERE o.customer.id = :id")
    List<Order> findByCustomerIdWithOrderItems(@Param("id") Long id);
}
