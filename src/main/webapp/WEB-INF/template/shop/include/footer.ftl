<div class="footer">
	<div class="service clearfix" style="text-align: center">
		[#--<dl>--]
			[#--<dt class="icon1">新手指南</dt>--]
			[#--<dd>--]
				[#--<a href="${base}/article/detail/1_1">购物流程</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">会员注册</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">购买宝贝</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">支付货款</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">用户协议</a>--]
			[#--</dd>--]
		[#--</dl>--]
		[#--<dl>--]
			[#--<dt class="icon2">特色服务</dt>--]
			[#--<dd>--]
				[#--<a href="#">购物流程</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">会员注册</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">购买宝贝</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">支付货款</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">用户协议</a>--]
			[#--</dd>--]
		[#--</dl>--]
		[#--<dl>--]
			[#--<dt class="icon3">支付方式</dt>--]
			[#--<dd>--]
				[#--<a href="#">购物流程</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">会员注册</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">购买宝贝</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">支付货款</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">用户协议</a>--]
			[#--</dd>--]
		[#--</dl>--]
		[#--<dl>--]
			[#--<dt class="icon4">配送方式</dt>--]
			[#--<dd>--]
				[#--<a href="#">购物流程</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">会员注册</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">购买宝贝</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">支付货款</a>--]
			[#--</dd>--]
			[#--<dd>--]
				[#--<a href="#">用户协议</a>--]
			[#--</dd>--]
		[#--</dl>--]
			[#--<dvi style="float: left">--]
			<div  class="flist">
				<div class="ftop"><img style="width: 50px" src="${base}/upload/image/正品保障.png" alt=""></div>
				<div  style="display: inline-block;vertical-align: middle;line-height: 22px;">
					<p class="fp"><strong>正品保障</strong></p>
					<p style="font-size: 12px">正品保障、提供发票</p>
				</div>
			</div>
            <div class="flist">
                <div class="ftop"><img style="width: 50px" src="${base}/upload/image/售后.png" alt=""></div>
                <div  style="display: inline-block;vertical-align: middle;line-height: 22px;">
                    <p class="fp"><strong>售后无忧</strong></p>
                    <p style="font-size: 12px">退换无忧、维修无忧</p>
                </div>
            </div>
            <div class="flist">
                <div class="ftop"><img style="width: 50px" src="${base}/upload/image/帮助中心.png" alt=""></div>
                <div  style="display: inline-block;vertical-align: middle;line-height: 22px;">
                    <p class="fp"><strong><a href="/article/list/3">帮助中心</a></strong></p>
                    <p style="font-size: 12px">您的购物指南</p>
                </div>
            </div>

			[#--</dvi>--]



		<div class="qrCode">
			<img src="${base}/resources/shop/images/greenleafld.jpg" alt="${message("shop.footer.weixin")}" />
			${message("shop.footer.weixin")}
		</div>
	</div>
	<div class="bottom">
		<div class="bottomNav">
			<ul>
				[@navigation_list position = "bottom"]
					[#list navigations as navigation]
						<li>
							<a href="${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>
							[#if navigation_has_next]|[/#if]
						</li>
					[/#list]
				[/@navigation_list]
			</ul>
		</div>
		<div class="info">
			<p>${setting.certtext}</p>
			<p>${message("shop.footer.copyright", '${message("shop.header.siteName")}')}</p>
			[@friend_link_list type="image" count = 8]
				<ul>
					[#list friendLinks as friendLink]
						<li>
							<a href="${friendLink.url}" target="_blank">
								<img src="${friendLink.logo}" alt="${friendLink.name}" />
							</a>
						</li>
					[/#list]
				</ul>
			[/@friend_link_list]
		</div>
	</div>
	[#include "/shop/include/statistics.ftl" /]
</div>