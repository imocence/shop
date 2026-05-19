package net.shopxx.service;

import net.shopxx.entity.MessageConfig;

/**
 * Service - 消息配置
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface MessageConfigService extends BaseService<MessageConfig, Long> {

	/**
	 * 查找消息配置
	 * 
	 * @param type
	 *            类型
	 * @return 消息配置
	 */
	MessageConfig find(MessageConfig.Type type);

}