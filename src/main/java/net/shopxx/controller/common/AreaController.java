/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Controller("commonAreaController")
@RequestMapping("/common/area")
public class AreaController {

	@Inject
	private AreaService areaService;
	
    @Inject
    private CountryService countryService;
    
	/**
	 * 地区
	 */
	@GetMapping
	public @ResponseBody List<Map<String, Object>> index(Long parentId, Long countryId) {
		if (parentId == null && countryId != null){
			List<Map<String, Object>> data = new ArrayList<>();
			Country c = countryService.find(countryId);
			Collection<Area> areas = c.getAreas();
			for (Area area : areas) {
				Map<String, Object> item = new HashMap<>();
				item.put("name", area.getName());
				item.put("value", area.getId());
				data.add(item);
			}
			return data;
		}
		List<Map<String, Object>> data = new ArrayList<>();
		Area parent = areaService.find(parentId);
		Collection<Area> areas = parent != null ? parent.getChildren() : areaService.findRoots();
		for (Area area : areas) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", area.getName());
			item.put("value", area.getId());
			data.add(item);
		}
		return data;
	}
}