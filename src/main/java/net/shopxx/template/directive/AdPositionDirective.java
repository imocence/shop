package net.shopxx.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import net.shopxx.entity.AdPosition;
import net.shopxx.entity.Country;
import net.shopxx.service.AdPositionService;
import net.shopxx.service.CountryService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

/**
 * 模板指令 - 广告位
 *
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Component
public class AdPositionDirective extends BaseDirective {

    /**
     * 变量名称
     */
    private static final String VARIABLE_NAME = "adPosition";

    @Inject
    private AdPositionService adPositionService;
    @Inject
    private CountryService countryService;

    /**
     * 执行
     *
     * @param env      环境变量
     * @param params   参数
     * @param loopVars 循环变量
     * @param body     模板内容
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String orders = params.get("orders").toString();
        boolean useCache = useCache(params);

        Country country = countryService.getDefaultCountry();
        AdPosition adPosition = adPositionService.find(orders, country, useCache);

        setLocalVariable(VARIABLE_NAME, adPosition, env, body);
    }
}