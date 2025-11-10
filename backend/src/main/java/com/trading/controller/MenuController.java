package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Menu;
import com.trading.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    @PreAuthorize("hasAuthority('menu:list')")
    public Result<List<Menu>> getAllMenus() {
        return Result.success(menuService.getAllMenus());
    }

    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        return Result.success(menuService.getMenuTree());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:view')")
    public Result<Menu> getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id)
                .map(Result::success)
                .orElse(Result.error("菜单不存在"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('menu:add')")
    public Result<Menu> createMenu(@RequestBody Menu menu) {
        return Result.success(menuService.createMenu(menu));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:edit')")
    public Result<Menu> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        return Result.success(menuService.updateMenu(menu));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:delete')")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success(null);
    }
}

