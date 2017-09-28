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
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<style type="text/css">
      .noneinput{border: none;}
</style>
<script type="text/javascript">
$().ready(function() {
	var $inputForm = $("#inputForm");
	var $country = $("#country");
	var $addProduct = $("#addProduct");
	var $productTable = $("#productTable");
	var $areaId = $("#areaId");
	var productIndex = -1;
	var productCategoryTree='';
	var totalQuantity = 0;
	[#if flashMessage?has_content]
		$.alert("${flashMessage}");
	[/#if]


	// 表单验证
	$inputForm.validate({
		submitHandler: function(form) {
			var indexArray = new Array();
			$("tr.product").each(function(index, item){
				indexArray[index] = $(item).attr("data");
			});
			var size = indexArray.length;
			if(size < 1){
				$.message("warn", "${message("admin.sheet.noProduct")}");
				return false;
			}
			for(var i=0;i<size;i++){
				var index = indexArray[i];
				var quantity = $("#product" + index + "quantity").val();
				if($.trim(quantity) == '' || quantity == 0){
					$.message("warn", "${message("admin.order.product.zero")}");
					return false;
				}
			}
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});
	
	$(function(){
		//$("tr.product").remove();
		// 获取商品分类
		alert(100);
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

	
	// 增加商品
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
						<input type="text" id="product' + productIndex + 'sn" name="sheetItems[' + productIndex + '].sn" maxlength="20" class="noneinput" readonly="readonly" style="width: 80px;" \/>
						<input type="hidden" id="product' + productIndex + 'weight" style="width: 80px;" \/>
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'name" class="noneinput" readonly="readonly" style="width: 140px;" \/>
					<\/td>
					
					<td>
						<input type="text" id="product' + productIndex + 'quantity" name="sheetItems[' + productIndex + '].quantity" data="' + productIndex + '" maxlength="20" class="text" style="width: 40px;" \/>
					<\/td>
					<td>
						<input type="text" id="product' + productIndex + 'sku" maxlength="20" class="noneinput" readonly="readonly" style="width: 40px;" \/>
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
					url: "${base}/admin/sheet/getProducts",
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
			// 同一个商品只能添加一次
			if (checkProduct()){
				$.message("warn", "${message("admin.order.product.repeat.add")}");
				return;
			}
			var index = $(this).attr("data");
			$("#product" + index + "image").empty();
			$("#product" + index + "sn").val('');
			$("#product" + index + "name").val('');
			$("#product" + index + "sku").val('');
			$("#product" + index + "quantity").val('');
			if ($(this).val() != '-1' && $(this).val() != null){
				$.ajax({
					url: "${base}/admin/sheet/getProduct",
					type: "GET",
					data: {productId: $(this).val()},
					dataType: "json",
					cache: false,
					success: function(item) {
						if (item.image == null){
							$("#product" + index + "image").append("<image style='width:50px;height:50px;' src='${base}/upload/image/defaultProduct.jpg' style=width:80px;'></image>");
						}else{
							$("#product" + index + "image").append("<image style='width:50px;height:50px;' src='" + item.image + "' style=width:80px;'></image>");
						}
						$("#product" + index + "sn").val(item.sn);
						$("#product" + index + "name").val(item.name);
						$("#product" + index + "sku").val(item.sku);
						$("#product" + index + "quantity").val(0);
					}
				});
			}
		});
		// 获取商品信息
		$productQuantity.change(function(){
			var index = $(this).attr("data");
			var $productSku = $("#product" + index + "sku");
			var quantity = $(this).val();
			if (!isPInt(quantity)){
				quantity = 0;
				$productQuantity.val(quantity);
			}
			if($.trim(quantity) == ''){
				return;
			}
		});
	});

	// 校验正整数
	function isPInt(str) {
	    var g = /^[1-9]*[1-9][0-9]*$/;
	    return g.test(str);
	}
	// 校验正数
	function getPNum(str){
	   var g = /^(\d+)$|^(\d+\.\d+)$/;
	   return g.test(str);
	}
	
	function checkProduct(){
		var productIds = '';
		var indexArray = new Array();
		$("tr.product").each(function(index, item){
			indexArray[index] = $(item).attr("data");
		});
		var size = indexArray.length;
		for(var i=0;i<size;i++){
			var index = indexArray[i];
			var productId = $("#productId" + i).val();
			if (productId == null){
				continue;
			}
			productId += ",";
			if (productIds.indexOf(productId)>=0){
				return true;
			}
			productIds += productId;
		}
		return false;
	}
	
	// 删除商品
	$productTable.on("click", "a.remove", function() {
		$(this).closest("tr").remove();
	});
});

</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.sheet.edit")}
	</div>
	<form id="inputForm" action="update" method="post">	
		<table id="productTable" class="list tabContent">
			<tr>
				<td>
					<a href="javascript:;" id="addProduct" class="button">${message("admin.order.product.add")}</a>
				</td>
				<td style="text-align: center;">
					<span class="requiredField">*</span>${message("common.country")}:
				</td>
				<td colspan="3">
					<input type="hidden" id="country" name="countryName" value="${country.id}"></input>
					<input type="text" readonly  unselectable="on" value="${message("${country.nameLocal}")}" ></input>
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.order.product.category")}
				</th>
				<th>
					${message("admin.order.product.select")}
				</th>
				<th>
					${message("admin.order.product.image")}
				</th>
				<th>
					${message("admin.order.product.sn")}
				</th>
				<th>
					${message("admin.order.product.name")}
				</th>

				<th>
					${message("admin.order.product.quantity")}
				</th>
				<th>
					${message("admin.order.product.stock")}
				</th>
				<th>
					${message("admin.common.action")}
				</th>
			</tr>
			[#list sheet.sheetItems as sheetItem]
				<tr class="product" data="${sheetItem.id}">
					<td></td>
					<td></td>
					<td></td>
					<td>
						${sheetItem.sn}
					</td>
					<td>
						${sheetItem.name}
					</td>
					
					<td>
						<input type="text" id="${sheetItem.id}quantity" name="quantity" value="${sheetItem.quantity}" maxlength="20" class="text" style="width: 40px;" />
					</td>
					<td>
						${sheetItem.sku.stock}
					</td>
					<td></td>
				</tr>
			[/#list]
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