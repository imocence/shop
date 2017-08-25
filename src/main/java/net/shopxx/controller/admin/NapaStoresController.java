package net.shopxx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.shopxx.entity.NapaStores;
import net.shopxx.service.NapaStoresService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 区代
 * 
 * @author sihao
 * @version 5.0.3
 */
@Controller("adminNapaStoresController")
@RequestMapping("/admin/store")
public class NapaStoresController extends BaseController{
	@Inject
	NapaStoresService napaStoresService;
	/**
	 * 跟新区代信息
	 */
	@PostMapping("/setStore")
	public @ResponseBody JSONObject setStore(NapaStores napaStores,
										String store_id,//区代编码
										String userCode,
										String type,//类型id
										String store_mobile,//电话
										String user_code,HttpServletRequest request, RedirectAttributes redirectAttributes){
		Map<String,Object> map = new HashMap<String, Object>();
		String errCode = "\"0000\"";		
		//区代
		napaStores = napaStoresService.findByUserCode(user_code);	
		if(napaStores != null){
			if(Integer.parseInt(type) == 0){
				napaStores.setNapaCode(null);
			}else{
				napaStores.setNapaCode(store_id);
			}
			napaStores.setType(Integer.parseInt(type));
			napaStoresService.update(napaStores);
		}else{
			napaStores = new NapaStores();
			napaStores.setType(Integer.parseInt(type));
			napaStores.setNapaCode(store_id);
			napaStores.setUserCode(user_code);
			napaStores.setMobile(store_mobile);
			try {
				napaStoresService.save(napaStores);
			} catch (Exception e) {
				errCode = "2001";
			}
		}
		
		map.put("errCode", errCode);
		JSONObject jsonObject = JSONObject.fromObject(map.toString());
		return jsonObject;
	}
}
