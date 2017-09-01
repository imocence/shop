package net.shopxx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.shopxx.entity.Member;
import net.shopxx.entity.NapaStores;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.util.TimeUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
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
	@Inject
	MemberService memberService;
	@Inject
	MemberRankService memberRankService;
	@Value("${url.signature}")
	private String urlSignature;
	private static String FORATNOWTIME = TimeUtil.getFormatNowTime("yyyyMMdd");
	/**
	 * 跟新区代信息
	 */
	@PostMapping("/setStore")
	public @ResponseBody JSONObject setStore(NapaStores napaStores,
										String store_id,//区代编码
										String userCode,
										String type,//类型id
										String store_mobile,//电话
										String store_address,//电话
										String signature,//约定验证码
										HttpServletRequest request, RedirectAttributes redirectAttributes){
		Map<String,Object> map = new HashMap<String, Object>();
		String errCode = "\"0000\"";	
		String signature0 = DigestUtils.md5Hex(FORATNOWTIME+urlSignature);
		if (!signature0.equals(signature)) {			
			errCode = "1001";
		}else{
			//区代
			Member member = memberService.findByUsercode(userCode);	
			napaStores = member.getNapaStores();
			if(Integer.parseInt(type) == 0){
				napaStores.setNapaCode(null);
			}else{
				napaStores.setNapaCode(store_id);
			}
			napaStores.setMobile(store_mobile);
			napaStores.setNapaAddress(store_address);
			napaStores.setType(Integer.parseInt(type));
			try {
				napaStoresService.update(napaStores);
				member.setNapaStores(napaStores);
				memberService.update(member);
			} catch (Exception e) {
				errCode = "2001";
			}			
		}
		map.put("errCode", errCode);
		JSONObject jsonObject = JSONObject.fromObject(map.toString());
		return jsonObject;
	}
}
