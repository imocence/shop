package net.shopxx.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import net.shopxx.entity.Seo;
import net.shopxx.service.SeoService;
import net.shopxx.util.FreeMarkerUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

/**
 * 模板指令 - SEO设置
 *
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Component
public class SeoDirective extends BaseDirective {

    /**
     * "类型"参数名称
     */
    private static final String TYPE_PARAMETER_NAME = "type";

    /**
     * 变量名称
     */
    private static final String VARIABLE_NAME = "seo";

    @Inject
    private SeoService seoService;

    /**
     * 执行
     *
     * @param env      环境变量
     * @param params   参数
     * @param loopVars 循环变量
     * @param body     模板内容
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        Seo.Type type = FreeMarkerUtils.getParameter(TYPE_PARAMETER_NAME, Seo.Type.class, params);
        boolean useCache = useCache(params);
        Seo seo = seoService.find(type, useCache);
        setLocalVariable(VARIABLE_NAME, seo, env, body);
    }
}