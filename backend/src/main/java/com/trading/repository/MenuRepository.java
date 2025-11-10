package com.trading.repository;

import com.trading.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByParentIdAndStatusOrderBySortAsc(Integer parentId, Integer status);
    List<Menu> findByStatusOrderBySortAsc(Integer status);
}

