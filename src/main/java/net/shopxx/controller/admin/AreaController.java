/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.ArrayList;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.entity.Area;
import net.shopxx.entity.Country;
import net.shopxx.service.AreaService;
import net.shopxx.service.CountryService;

/**
 * Controller - 地区
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminAreaController")
@RequestMapping("/admin/area")
public class AreaController extends BaseController {

    @Inject
    private AreaService    areaService;

    @Inject
    private CountryService countryService;

    /**
     * 添加
     */
    @GetMapping("/add")
    public String add(Long parentId, Long countryId, ModelMap model) {
        //添加国家
        if (parentId == null && countryId == null) {
            return "admin/area/addCountry";
        }
        if (countryId != null) {
            Country c = countryService.find(countryId);
            model.addAttribute("country", c);
            return "admin/area/add";
        }
        model.addAttribute("parent", areaService.find(parentId));
        return "admin/area/add";
    }

    /**
     * 保存国家
     */
    @PostMapping("/saveCountry")
    public String saveCountry(Country country, Long parentId, RedirectAttributes redirectAttributes) {
        country.setState(1);
        countryService.save(country);
        return "redirect:list";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public String save(Area area, Long parentId, Long countryId, RedirectAttributes redirectAttributes) {
        if (parentId != null) {
            area.setParent(areaService.find(parentId));
        }
        if (countryId != null) {
            area.setCountry(countryService.find(countryId));
        }
        if (!isValid(area)) {
            return ERROR_VIEW;
        }
        area.setFullName(null);
        area.setTreePath(null);
        area.setGrade(null);
        area.setChildren(null);
        area.setMembers(null);
        area.setReceivers(null);
        area.setOrders(null);
        area.setDeliveryCenters(null);
        area.setFreightConfigs(null);
        areaService.save(area);
        addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
        return "redirect:list";
    }

    /**
     * 编辑
     */
    @GetMapping("/editCountry")
    public String editCountry(Long id, ModelMap model) {
        model.addAttribute("country", countryService.find(id));
        return "admin/area/editCountry";
    }

    /**
    * 编辑地区
    */
    @GetMapping("/edit")
    public String edit(Long id, ModelMap model) {
        model.addAttribute("area", areaService.find(id));
        return "admin/area/edit";
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public String update(Area area, RedirectAttributes redirectAttributes) {
        if (!isValid(area)) {
            return ERROR_VIEW;
        }
        areaService.update(area, "fullName", "treePath", "grade", "parent", "children", "members", "receivers", "orders", "deliveryCenters", "freightConfigs", "country");
        addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
        return "redirect:list";
    }

    /**
     * 更新
     */
    @PostMapping("/updateCountry")
    public String update(Country country, RedirectAttributes redirectAttributes) {
        if (!isValid(country)) {
            return ERROR_VIEW;
        }
        countryService.update(country, "state", "version");
        addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
        return "redirect:list";
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public String list(Long parentId, Long countryId, ModelMap model) {
        //国家
        if (countryId == null && parentId == null) {
            model.addAttribute("countries", countryService.findRoots());
            return "admin/area/list";
        }
        model.addAttribute("countries", null);

        Area parent = areaService.find(parentId);
        if (parent != null) {
            model.addAttribute("parent", parent);
            model.addAttribute("areas", new ArrayList<>(parent.getChildren()));
        } else {
            Country c = countryService.find(countryId);
            model.addAttribute("country", c);
            model.addAttribute("areas", new ArrayList<>(c.getAreas()));
        }
        return "admin/area/list";
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public @ResponseBody Message delete(Long id, Long country) {
        if (country != null && country == 1) {
            Country c = countryService.find(id);
            c.setState(0);
            countryService.update(c);
        } else {
            areaService.delete(id);
        }
        return Message.success(SUCCESS_MESSAGE);
    }

}