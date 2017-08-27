package net.shopxx.util;

import java.util.Set;

import net.shopxx.entity.Admin;
import net.shopxx.entity.Role;

/**
 * 按钮权限控制
 * 
 * @author gaoxiang
 * @version 1.0.0
 *
 */
public class PermissionUitl {
	/**
	 * 充值核实按钮
	 */
	public static String JOURNAL_TEMP_CONFIRM = "admin:fiBankbookJournalTempConfirm";
	
	public static boolean isPermission(Admin currentUser, String permission){
		boolean isPermission = false;
		Set<Role> roles = currentUser.getRoles();
		for (Role role : roles) {
			if(role.getPermissions().contains(permission)){
				isPermission = true;
				break;
			}
		}
		return isPermission;
	}
}
