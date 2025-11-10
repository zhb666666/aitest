package com.trading.service;

import com.trading.entity.Menu;
import com.trading.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public Menu createMenu(Menu menu) {
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0);
        }
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public List<Menu> getMenuTree() {
        List<Menu> allMenus = menuRepository.findByStatusOrderBySortAsc(1);
        return buildMenuTree(allMenus, 0);
    }

    private List<Menu> buildMenuTree(List<Menu> menus, Integer parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    menu.setChildren(buildMenuTree(menus, menu.getId().intValue()));
                    return menu;
                })
                .collect(Collectors.toList());
    }

    public Optional<Menu> getMenuById(Long id) {
        return menuRepository.findById(id);
    }
}

