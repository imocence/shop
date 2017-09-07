<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.order.add")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
      .noneinput{border: none;}
</style>
<script type="text/javascript">
$().ready(function() {
	var $inputForm = $("#inputForm");
	var $memberSelect = $("#memberSelect");
	var $country = $("#country");
	var $usercode = $("#usercode");
	var $usercodeSpan = $("#usercodeSpan");
	var $username = $("#username");
	var $userBalance = $("#userBalance");
	var $userCouponBalance = $("#userCouponBalance");
	var $consignee = $("#consignee");
	var $phone = $("#phone");
	var $address = $("#address");
	var $paymentMethod = $("#paymentMethod");
	var $shippingMethod = $("#shippingMethod");
	var $isInvoice = $("#isInvoice");
	var $invoiceTitle = $("#invoiceTitle");
	var $addProduct = $("#addProduct");
	var $productTable = $("#productTable");
	var $freight = $("#freight");
	
	var productIndex = -1;
	var productCategoryTree='';
	// 用户选择
	$memberSelect.autocomplete("member_select", {
		dataType: "json",
		max: 20,
		width: 218,
		scrollHeight: 300,
		extraParams: { 
			country: function() {
				 return $country.val();
			}
		}, 
		parse: function(data) {
			return $.map(data, function(item) {
				return {
					data: item,
					value: item.usercode
				}
			});
		},
		formatItem: function(item) {
			return '<span title="' + escapeHtml(item.username) + '">' + escapeHtml(abbreviate(item.username, 50, "...")) + '<\/span>' + (item.usercode.length > 0 ? ' <span class="silver">[' + escapeHtml(item.usercode) + ']<\/span>' : '');
		}
	}).result(function(event, item) {
		$usercode.val(item.usercode);
		$usercodeSpan.html(item.usercode);
		$username.html(item.username);
		$userBalance.html(item.balance);
		$userCouponBalance.html(item.couponbalance);
		$consignee.val(item.username);
		$phone.val(item.phone);
		$address.val(item.address);
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			money: {
				required: true,
				decimal: {
					integer: 21,
					fraction: ${setting.priceScale}
				}
			}
		},
		submitHandler: function(form) {
			if ($usercode.val() == "") {
				$.message("warn", "${message("admin.fiBankbookJournalTemp.usercodeRequired")}");
				return false;
			}
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});
	$country.change(function(){
		$memberSelect.flushCache();
	  	$usercode.val('');
	  	$usercodeSpan.html('');
		$username.html('');
		$userBalance.html('');
		$userCouponBalance.html('');
		// ajax获取paymentMethod
		$.ajax({
			url: "${base}/admin/payment_method/listByCountry",
			type: "GET",
			data: {countryName: $country.val()},
			dataType: "json",
			cache: false,
			success: function(message) {
				$paymentMethod.empty();
			 	var obj = eval('(' + message + ')');
				$.each(obj, function (index,item){
					$paymentMethod.append('<option value=' + item.id + '>' + item.name + '</option>');
				});
				$paymentMethod.change();
			}
		});
		// 获取商品分类
		$.ajax({
			url: "${base}/admin/product_category/findTree",
			type: "GET",
			data: {country: $country.val()},
			dataType: "json",
			cache: false,
			success: function(message) {
				productCategoryTree = message;
			}
		});
	});
	
	// 支付方式修改
	$paymentMethod.change(function(){
		$.ajax({
			url: "${base}/admin/payment_method/getShippingMethodListByPaymentMethod",
			type: "GET",
			data: {paymentMethodId: $paymentMethod.val()},
			dataType: "json",
			cache: false,
			success: function(message) {
				$shippingMethod.empty();
				$.each(message, function (index,item){
					$shippingMethod.append('<option value=' + item.id + '>' + item.name + '</option>');
				});
				getTotal();
			}
		});
	});
	
	// 配送方式修改
	$shippingMethod.change(function(){
		getTotal();
	});
	
	// 开具发票
	$isInvoice.click(function() {
		if ($(this).prop("checked")) {
			$invoiceTitle.prop("disabled", false).closest("tr").show();
		} else {
			$invoiceTitle.prop("disabled", true).closest("tr").hide();
		}
	});
	
	// 发票抬头
	$invoiceTitle.focus(function() {
		if ($.trim($invoiceTitle.val()) == "${message("shop.order.defaultInvoiceTitle")}") {
			$invoiceTitle.val("");
		}
	});
	
	// 发票抬头
	$invoiceTitle.blur(function() {
		if ($.trim($invoiceTitle.val()) == "") {
			$invoiceTitle.val("${message("shop.order.defaultInvoiceTitle")}");
		}
	});
	
	// 运费修改
	$freight.change(function(){
		var freight = $("#freight").val();
		if (freight < 0 ){
			freight = 0;
			$("#freight").val(freight);
		}
		var totalPrice = $("#totalPriceSpan").html();
		$("#balance").val(freight*1 + totalPrice*1);
	});
	
	// 增加商品图片
	$addProduct.click(function() {
		productIndex ++;
		$productTable.append(
			[@compress single_line = true]
				'<tr class="product" data="' + productIndex + '">
					<td>
						<select id="productCategoryId' + productIndex + '"><\/select>
					<\/td>
					<td>
						<select id="productId' + productIndex + '" style="width:100px;" data="' + productIndex + '"><\/select>
					<\/td>
					<td id="product' + productIndex + 'image">
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'sn" name="product[' + productIndex + '].sn" maxlength="20" class="noneinput" readonly="readonly" style="width: 80px;" \/>
						<input type="hidden" id="product' + productIndex + 'weight" style="width: 80px;" \/>
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'name" name="product[' + productIndex + '].name" class="noneinput" readonly="readonly" style="width: 140px;" \/>
					<\/td>
					<td>
						<input id="product' + productIndex + 'price" name="orderItem[' + productIndex + '].price" maxlength="60" style="width: 80px;" \/>
					<\/td>
					<td>
						<input id="product' + productIndex + 'couponPrice" name="orderItem[' + productIndex + '].couponPrice" maxlength="60" style="width: 80px;"\/>
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'quantity" name="orderItem[' + productIndex + '].quantity" data="' + productIndex + '" maxlength="20" class="text" style="width: 40px;" \/>
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'sku" name="product[' + productIndex + '].sku" maxlength="20" class="noneinput" readonly="readonly" style="width: 40px;" \/>
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'total" name="product[' + productIndex + '].total" maxlength="20" class="noneinput" readonly="readonly" style="width: 120px;" \/>
					<\/td>
					<td>
						<a href="javascript:;" class="remove">[${message("admin.common.remove")}]<\/a>
					<\/td>
				<\/tr>'
			[/@compress]
		);
		// 获取分类树列表
		var $selectProductCategoryId = $("#productCategoryId" + productIndex);
		var $selectProductId = $("#productId" + productIndex);
		var $productQuantity = $("#product" + productIndex + "quantity");
		$selectProductCategoryId.append('<option value=-1>${message("admin.common.choose")}</option>');
		$.each(productCategoryTree, function (index,item){
			var space = '';
			for(var i=0;i<item.grade;i++){
				space += '&nbsp;&nbsp';
			}
			$selectProductCategoryId.append('<option value=' + item.id + '>' + space + item.name + '</option>');
		});
		// 获取分类下的商品列表
		$selectProductCategoryId.change(function(){
			$selectProductId.empty();
			$selectProductId.change();
			var productCategoryId = $(this).val();
			if (productCategoryId != '-1' && productCategoryId != null){
				$.ajax({
					url: "${base}/admin/order/getProducts",
					type: "GET",
					data: {productCategoryId: $(this).val()},
					dataType: "json",
					cache: false,
					success: function(message) {
						$selectProductId.append('<option value=-1>${message("admin.common.choose")}</option>');
						$.each(message, function (index,item){
							$selectProductId.append('<option value=' + item.id + '>' + item.name + '</option>');
						});
					}
				});
			}
		});
		// 获取商品信息
		$selectProductId.change(function(){
			var index = $(this).attr("data");
			$("#product" + index + "sn").val('');
			if ($(this).val() != '-1' && $(this).val() != null){
				$.ajax({
					url: "${base}/admin/order/getProduct",
					type: "GET",
					data: {productId: $(this).val()},
					dataType: "json",
					cache: false,
					success: function(item) {
						$("#product" + index + "image").append("<image style='width:50px;height:50px;' src='" + item.image + "'style=width:80px;'></image>");
						$("#product" + index + "sn").val(item.sn);
						$("#product" + index + "name").val(item.name);
						$("#product" + index + "price").val(item.price);
						$("#product" + index + "couponPrice").val(item.couponPrice);
						$("#product" + index + "sku").val(item.sku);
						$("#product" + index + "weight").val(item.weight);
					}
				});
			}
		});
		// 获取商品信息
		$productQuantity.change(function(){
			var index = $(this).attr("data");
			var $productPrice = $("#product" + index + "price");
			var $productCouponPrice = $("#product" + index + "couponPrice");
			var $productTotal = $("#product" + index + "total");
			var $productSku = $("#product" + index + "sku");
			var quantity = $(this).val();
			var productSku = $productSku.val();
			if (productSku<quantity){
				quantity = productSku;
				$productQuantity.val(quantity);
			}
			if($.trim(quantity) == ''){
				return;
			}
			$productTotal.val($productPrice.val()*quantity + "+" + $productCouponPrice.val()*quantity);
			getTotal();
		});
	});
	
	function getTotal(){
		// 计算总重量
		// 计算总运费
		// 计算总金额
		var totalPrice = 0;
		var totalCouponPrice = 0;
		var totalFreight = 0;
		var totalWeight = 0;
		var indexArray = new Array();
		$("tr.product").each(function(index, item){
			indexArray[index] = $(item).attr("data");
		});
		var size = indexArray.length;
		for(var i=0;i<size;i++){
			var index = indexArray[i];
			var quantity = $("#product" + index + "quantity").val();
			if($.trim(quantity) == ''){
				continue;
			}
			var price = $("#product" + index + "price").val();
			var couponPrice = $("#product" + index + "couponPrice").val();
			var weight = $("#product" + index + "weight").val();
			totalPrice += price * quantity;
			totalCouponPrice += couponPrice * quantity;
			totalWeight += weight * quantity;
		}
		// 计算运费
		$.ajax({
			url: "${base}/admin/order/getFreight",
			type: "GET",
			data: {shippingMethodId: $("#shippingMethod").val(), weight:totalWeight},
			dataType: "json",
			async: false,
			cache: false,
			success: function(msg) {
				totalFreight = msg;
			}
		});
		totalPrice += totalFreight;
		$("#totalPriceSpan").html(totalPrice);
		$("#balance").val(totalPrice);
		$("#totalCouponPriceSpan").html(totalCouponPrice);
		$("#couponbalance").val(totalCouponPrice);
		$("#weight").html(totalWeight);
		$("#freight").val(totalFreight);
	}
	
	// 删除商品图片
	$productTable.on("click", "a.remove", function() {
		$(this).closest("tr").remove();
		getTotal();
	});
	
	$country.change();
});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.order.add")}
	</div>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.order.orderInfo")}" />
		</li>
		<li class="product">
			<input type="button" value="${message("admin.order.productInfo")}" />
		</li>
	</ul>
	<form id="inputForm" action="save" method="post">
		<input type="hidden" id="usercode" name="usercode" />
		<table id="order" class="input tabContent">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("common.country")}:
				</th>
				<td>
					<select id="country" name="countryName">
						[@country_list]
							[#list countrys as country]
								<option value="${country.name}">${message("${country.nameLocal}")}</option>
							[/#list]
						[/@country_list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.fiBankbookJournalTemp.memberSelect")}:
				</th>
				<td>
					<input type="text" id="memberSelect" name="memberSelect" class="text" maxlength="200" title="${message("admin.fiBankbookJournalTemp.memberSelectTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("common.member.code")}:
				</th>
				<td>
					<span id="usercodeSpan" />
				</td>
			</tr>
			<tr>
				<th>
					${message("common.member.name")}:
				</th>
				<td>
					<span id="username" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.fiBankbookBalance.type.balance")}:
				</th>
				<td>
					<span id="userBalance" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.fiBankbookBalance.type.coupon")}:
				</th>
				<td>
					<span id="userCouponBalance" />
				</td>
			</tr>
			<!-- 收货人 -->
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Receiver.consignee")}:
				</th>
				<td>
					<input type="text" id="consignee" name="consignee" class="text" maxlength="200" />
				</td>
			</tr>
			<!-- 电话 -->
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Receiver.phone")}
				</th>
				<td>
					<input type="text" id="phone" name="phone" class="text" maxlength="200" />
				</td>
			</tr>
			<!-- 地址 -->
			<tr>
				<th>
					<span class="requiredField">*</span>${message("shop.order.receiver")}
				</th>
				<td>
					<input type="text" id="address" name="address" class="text" maxlength="200" />
				</td>
			</tr>
			<!-- 支付方式 -->
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Order.paymentMethod")}
				</th>
				<td>
					<select id="paymentMethod" name="paymentMethod">
					</select>
				</td>
			</tr>
			<!-- 配送方式 -->
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Order.shippingMethod")}
				</th>
				<td>
					<select id="shippingMethod" name="shippingMethod">
					</select>
				</td>
			</tr>
			<!-- 发票 -->
			<tr>
				<th>
					${message("shop.order.isInvoice")}:
				</th>
				<td>
					<label for="isInvoice">
						<input type="checkbox" id="isInvoice" name="isInvoice" value="true" />
						[#if setting.isTaxPriceEnabled](${message("Order.tax")}: ${setting.taxRate * 100}%)[/#if]
					</label>
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("shop.order.invoiceTitle")}:
				</th>
				<td>
					<input type="text" id="invoiceTitle" name="invoiceTitle" class="text" value="${message("shop.order.defaultInvoiceTitle")}" maxlength="200" disabled="disabled" />
				</td>
			</tr>
			<!-- 附言 -->
			<tr>
				<th>
					${message("Order.memo")}:
				</th>
				<td>
					<input type="text" name="memo" class="text" maxlength="200" />
				</td>
			</tr>
			<!-- 优惠卷 -->
			<tr>
				<th>
					${message("shop.order.coupon")}:
				</th>
				<td>
					<input type="hidden" id="code" name="code" maxlength="200" />
					<input type="text" id="couponCode" class="text" maxlength="200" />
					<span id="couponName">&nbsp;</span>
					<input type="button" class="button" id="couponButton" value="${message("shop.order.codeConfirm")}"></input>
				</td>
			</tr>
			<!-- 优惠金额 -->
			<tr>
				<th>
					${message("Order.couponDiscount")}:
				</th>
				<td>
					<em id="couponDiscount">${currency(order.couponDiscount, true)}</em>
				</td>
			</tr>
			<!-- 重量 -->
			<tr>
				<th>
					重量:
				</th>
				<td>
					<em id="weight"></em>
				</td>
			</tr>
			<!-- 运费 -->
			<tr>
				<th>
					${message("Order.freight")}:
				</th>
				<td>
					<input type="text" style='border:none;' id="freight" name="freight" class="text" maxlength="200" />
				</td>
			</tr>
			<!-- 订单总金额-->
			<tr>
				<th>
					订单电子币总额:
				</th>
				<td>
					<input type="text" style='border:none;' id="balance" name="balance" class="text" readonly="readonly" maxlength="200" />
				</td>
			</tr>
			<!-- 订单总购物券金额-->
			<tr>
				<th>
					订单购物券总额:
				</th>
				<td>
					<input type="text" style='border:none;' id="couponbalance" name="couponbalance" class="text" readonly="readonly" maxlength="200" />
				</td>
			</tr>
		</table>
		<table id="productTable" class="list tabContent">
			<!-- 订单总金额-->
			<!-- 订单总购物券金额-->
			<tr>
				<td>商品电子币总额:</td>
				<td>
					<span id="totalPriceSpan" />
				</td>
				<td>商品购物券总额:</td>
				<td>
					<span id="totalCouponPriceSpan" />
				</td>
				<td colspan="6">
				</td>
			</tr>
			<tr>
				<td colspan="10">
					<a href="javascript:;" id="addProduct" class="button">${message("admin.product.addProduct")}</a>
				</td>
			</tr>
			<tr>
				<th>
					商品分类
				</th>
				<th>
					商品选择
				</th>
				<th>
					商品图片
				</th>
				<th>
					商品编号
				</th>
				<th>
					商品名称
				</th>
				<th>
					电子币价格
				</th>
				<th>
					购物券价格
				</th>
				<th>
					商品数量
				</th>
				<th>
					商品库存
				</th>
				<th>
					小计
				</th>
				<th>
					${message("admin.common.action")}
				</th>
			</tr>
		</table>
		<table class="input">
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>