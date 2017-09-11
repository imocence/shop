[@product_list productCategoryId = productCategory.id count = 3 orderBy = "monthSales desc"]
	[#if products?has_content]
		<div class="hotProduct">
			<dl>
				<dt>${message("shop.product.hotProduct")}</dt>
				[#list products as product]
					<dd>
						<a href="${base}${product.path}">
							<img src="${product.thumbnail!setting.defaultThumbnailProductImage}" alt="${product.name}" />
							<span title="${product.name}">${abbreviate(product.name, 52)}</span>
						</a>
						<strong>
							 [#if currentUser == null] 
								   [#list product.productGrades as pg]
										[#if pg.grade.isDefault == true]
										 ${currency(pg.price, true)}
										[/#if]
								   [/#list]
							[#else]
									[#list product.productGrades as pg]
										[#if pg.grade.id == currentUser.memberRank.id]
										 ${currency(pg.price, true)}
										[/#if]
								    [/#list]
							[/#if]
							[#if setting.isShowMarketPrice]
								<del>${currency(product.marketPrice, true)}</del>
							[/#if]
						</strong>
						<!-- 券  -->
						[#if currentUser == null] 
							
						[#else]
								[#list product.productGrades as pg]
									[#if pg.grade.id == currentUser.memberRank.id]
										<em> ${message("shop.index.coupon")}${pg.coupon}</em>
									[/#if]
							    [/#list]
						[/#if]
					</dd>
				[/#list]
			</dl>
		</div>
	[/#if]
[/@product_list]