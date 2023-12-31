package com.example.Order.Repository;

import com.example.Member.entity.Member;
import com.example.Order.Entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("select o from Order o " +
            "where o.member.memId = :memId " +
            "order by o.orderDate desc")
    List<Order> findOrders(@Param("memId") String memId, Pageable pageable);

    @Query("select m " +
            "from Member m, Order o " +
            "where m.id = o.member.id and o.member.memId = :memId")
    Member findMemberByOrder(@Param("memId") String memId);

    @Query("select count(0) from Order o " +
            "where o.member.memId = :memId")
    Long countOrder(@Param("memId") String memId);

}
