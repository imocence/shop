<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="author" content="SHOP++ Team">
    <meta name="copyright" content="SHOP++">
[@seo type = "index"]
    <title>${seo.resolveTitle()}[#if showPowered] - Powered By SHOP++[/#if]</title>
    [#if seo.resolveKeywords()?has_content]
        <meta name="keywords" content="${seo.resolveKeywords()}">
    [/#if]
    [#if seo.resolveDescription()?has_content]
        <meta name="description" content="${seo.resolveDescription()}">
    [/#if]
[/@seo]
    <link href="${base}/favicon.ico" rel="icon">
    <link href="${base}/resources/mobile/shop/css/bootstrap.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/font-awesome.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/animate.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/common.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/index.css" rel="stylesheet">
    <!--[if lt IE 9]>
    <script src="${base}/resources/mobile/shop/js/html5shiv.js"></script>
    <script src="${base}/resources/mobile/shop/js/respond.js"></script>
    <![endif]-->
    <script src="${base}/resources/mobile/shop/js/jquery.js"></script>
    <script src="${base}/resources/mobile/shop/js/jquery.lazyload.js"></script>
    <script src="${base}/resources/mobile/shop/js/bootstrap.js"></script>
    <script src="${base}/resources/mobile/shop/js/velocity.js"></script>
    <script src="${base}/resources/mobile/shop/js/velocity.ui.js"></script>
    <script src="${base}/resources/mobile/shop/js/underscore.js"></script>
    <script src="${base}/resources/mobile/shop/js/hammer.js"></script>
    <script src="${base}/resources/mobile/shop/js/common.js"></script>
    <script type="text/javascript">
        $().ready(function() {

            var $searchIcon = $("#searchIcon");
            var $searchPlaceholder = $("#searchPlaceholder");
            var $search = $("#search");
            var $searchSlideUp = $("#searchSlideUp");
            var $searchForm = $("#searchForm");
            var $keyword = $("#keyword");
            var $login = $("#login");
            var $user = $("#user");
            var $masthead = $("#masthead");
            var $productImage = $("div.products img");
            var currentMemberUsername = getCookie("currentMemberUsername");

            if ($.trim(currentMemberUsername) != "") {
                $user.show();
            } else {
                $login.show();
            }

            $searchIcon.add($searchPlaceholder).click(function() {
                $search.velocity("transition.slideDownBigIn");
            });

            $searchSlideUp.click(function() {
                $search.velocity("transition.slideUpBigOut");
            });

            // 搜索
            $searchForm.submit(function() {
                if ($.trim($keyword.val()) == "") {
                    return false;
                }
            });

            new Hammer($masthead.get(0)).on("swipeleft", function() {
                $masthead.carousel("next");
            }).on("swiperight", function() {
                $masthead.carousel("prev");
            });

            $productImage.lazyload({
                threshold: 100,
                effect: "fadeIn"
            });

        });

        function changeLanguage(){
            var language = $("#language").val();
            $.ajax({
                url: "${base}/common/language/change",
                type: "POST",
                data: {code: language},
                dataType: "json",
                cache: false,
                success: function(message) {
                    window.location.reload();
                }
            });
        }
    </script>
</head>
<style>
    #language{
        margin-left: 12px;
        width: 54%;
        padding: 0px;
        color: #999999;
        border-radius: 3px;
    }
</style>
<body class="index">
<header>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-2 text-center">
                <span id="searchIcon" class="glyphicon glyphicon-th-large"></span>
            </div>
            <div class="col-xs-8">
                <div id="searchPlaceholder" class="search-placeholder">
                ${message("shop.header.keyword")}<span class="glyphicon glyphicon-search"></span>
                </div>
            </div>
            <div class="col-xs-2 text-center">
                <a id="login" class="login" href="${base}/member/login">${message("shop.header.login")}</a>
                <a id="user" class="user" href="${base}/member/index">
                    <span class="fa fa-user-o"></span>
                </a>
            </div>
        </div>
        <div id="search" class="search">
            <div class="row">
                <div class="col-xs-1 text-center">
                    <span id="searchSlideUp" class="glyphicon glyphicon-menu-up"></span>
                </div>
                <div class="col-xs-11">
                    <form id="searchForm" action="${base}/product/search" method="get">
                        <div class="input-group">
                            <input id="keyword" name="keyword" class="form-control" type="text" placeholder="${message("shop.header.keyword")}">
                            <span class="input-group-btn">
									<button class="btn btn-default" type="submit">
										<span class="glyphicon glyphicon-search"></span>
									</button>
								</span>
                        </div>
                    </form>
                </div>
            </div>
        [#if setting.hotSearches?has_content]
            <dl class="hot-search">
                <dt>
                    <span class="glyphicon glyphicon-star-empty"></span>${message("shop.header.hotSearch")}
                </dt>
                [#list setting.hotSearches as hotSearch]
                    <dd>
                        <a href="${base}/product/search?keyword=${hotSearch?url}">${hotSearch}</a>
                    </dd>
                [/#list]
                <dt>	${message("Setting.locale")}
                    <select id="language" name="language" onchange="changeLanguage();">
                        [@language]
                            [#list languages as language]
                                <option value="${language.code}"[#if language.code == languageCode] selected="selected"[/#if]>${message("${language.message}")}</option>
                            [/#list]
                        [/@language]
                    </select>
                </dt>
            </dl>
        [/#if]
        </div>
    </div>
</header>
<main>
    <div class="container-fluid">
        <div id="masthead" class="masthead carousel slide" data-ride="carousel">
            <ol class="carousel-indicators">
                <li class="active" data-target="#masthead" data-slide-to="0"></li>
                <li data-target="#masthead" data-slide-to="1"></li>
                <li data-target="#masthead" data-slide-to="2"></li>
            </ol>
            <ul class="carousel-inner">
            [@ad_position orders = "1"]
                [#list adPosition.ads as ad]
                    <li class="item [#if ad_index = 0] active[/#if]">
                        <a href="${ad.url}">
                            <img src="${ad.path}" alt="${ad.title}">
                        </a>
                    </li>
                [/#list]
            [/@ad_position]
                <!-- <li class="item active">
						<a href="#">
							<img src="${base}/upload/image/index_slider1.jpg" alt="荣耀8">
						</a>
					</li>
					<li class="item">
						<a href="#">
							<img src="${base}/upload/image/index_slider2.jpg" alt="百万豪礼">
						</a>
					</li>
					<li class="item">
						<a href="#">
							<img src="${base}/upload/image/index_slider3.jpg" alt="百万豪礼">
						</a>
					</li> -->
            </ul>
        </div>
        <nav>
            <div class="row">
                <div class="col-xs-3 text-center">
                    <a href="${base}/product_category/index">
                        <img src="${base}/upload/image/全部分类.png" alt="全部分类">
                    ${message('shop.mobile.alllist')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/product/list/1">
                        <img src="${base}/upload/image/品牌套餐.png" alt="品牌套餐">
                    ${message('shop.mobile.Suitebusiness')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/product/list/1">
                        <img src="${base}/upload/image/新品预告.png" alt="新品预告">
                    ${message('shop.mobile.newproduct')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/product/search?keyword=\"卡丽施\"">
                        <img src="${base}/upload/image/热门.png" alt="热门搜索">
                    ${message('shop.mobile.hotsearch')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/member/index">
                        <img src="${base}/upload/image/会员中心.png" alt="会员中心">
                    ${message('shop.mobile.mumber')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/member/order/list">
                        <img src="${base}/upload/image/我的订单.png" alt="我的订单">
                    ${message('shop.mobile.myorder')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/cart/list">
                        <img src="${base}/upload/image/购物车.png" alt="购物车">
                    ${message('shop.mobile.cart')}
                    </a>
                </div>
                <div class="col-xs-3 text-center">
                    <a href="${base}/member/product_favorite/list">
                        <img src="${base}/upload/image/tsdyf-我的收藏.png" alt="我的收藏">
                    ${message('shop.mobile.mywishlist')}
                    </a>
                </div>
            </div>
        </nav>
        <div class="promotion">
            [#--<div class="row">--]
                [#--<div class="col-xs-2 text-center">--]
                    [#--<span class="glyphicon glyphicon-gift red-dark"></span>--]
                [#--</div>--]
                [#--<div class="col-xs-10">--]
                    [#--<div class="carousel" data-ride="carousel">--]
                        [#--<ul class="carousel-inner">--]
                            [#--<li class="item active">--]
                                [#--<a href="${base}/product/list/1">--]
                                    [#--<em class="blue-dark">苹果产品促销专场，全部最低价</em>--]
                                    [#--以旧换新最高可抵2000元--]
                                [#--</a>--]
                            [#--</li>--]
                            [#--<li class="item">--]
                                [#--<a href="${base}/product/list/1">--]
                                    [#--<em class="blue-dark">Color Cube立体声蓝牙音箱</em>--]
                                    [#--精致生活，声色享受--]
                                [#--</a>--]
                            [#--</li>--]
                            [#--<li class="item">--]
                                [#--<a href="${base}/product/list/1">--]
                                    [#--<em class="blue-dark">四款让人任性的数码产品，达人分享</em>--]
                                    [#--忙碌1年，任性1把--]
                                [#--</a>--]
                            [#--</li>--]
                        [#--</ul>--]
                    [#--</div>--]
                [#--</div>--]
            [#--</div>--]
        </div>
        <div class="ad">
            <ul>
            [@ad_position orders = "2"]
                [#list adPosition.ads as ad]
                    <li>
                        <a href="${ad.url}">
                            <img src="${ad.path}" alt="${ad.title}">
                        </a>
                    </li>
                [/#list]
            [/@ad_position]
                <!--<li>
						<a href="${base}/product/list/1">
							<img src="${base}/upload/image/row3_slider_1.jpg" alt="音响">
						</a>
					</li>
					<li>
						<a href="${base}/product/list/1">
							<img src="${base}/upload/image/row3_slider_2.jpg" alt="音响">
						</a>
					</li>
					<li>
						<a href="${base}/product/list/1">
							<img src="${base}/upload/image/row3_slider_3.jpg" alt="音响">
						</a>
					</li>
					<li>
						<a href="${base}/product/list/1">
							<img src="${base}/upload/image/row3_slider_4.jpg" alt="音响">
						</a>
					</li>-->
            </ul>
        </div>
    [@product_category_root_list count = 3]
        [#list productCategories as productCategory]
            <div class="products panel panel-flat panel-condensed">
                <div class="panel-heading orange">${productCategory.name}</div>
                <div class="panel-body">
                    <div class="row">
                        [@product_list productCategoryId = productCategory.id count = 6]
                            [#list products as product]
                                <div class="col-xs-4">
                                    <div class="thumbnail thumbnail-flat thumbnail-condensed">
                                        <a href="${base}${product.path}">
                                            <img style="max-height: 135px" class="img-responsive center-block" src="${base}/upload/image/blank.gif" data-original="${product.image!setting.defaultThumbnailProductImage}">
                                            <h4 class="text-overflow">${product.name}</h4>
                                            [#if product.caption?has_content]
                                                <div class="text-overflow text-muted small">${product.caption}</div>
                                            [/#if]
                                            <strong style="font-size: 12px;color: red;">
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
                                            </strong>
                                            <!-- 券  -->
                                            [#if currentUser == null]

                                            [#else]
                                                [#list product.productGrades as pg]
                                                    [#if pg.grade.id == currentUser.memberRank.id]
                                                        <strong style="font-size: 10px;color: #ff7700;margin-left: 4px"> ${message("shop.index.coupon")}${pg.coupon}</strong>
                                                    [/#if]
                                                [/#list]
                                            [/#if]
                                        </a>
                                    </div>
                                </div>
                            [/#list]
                        [/@product_list]
                    </div>
                </div>
            </div>
        [/#list]
    [/@product_category_root_list]
    </div>
</main>
<footer class="footer-fixed">
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-3 text-center active">
                <a href="${base}/">
                    <span class="glyphicon glyphicon-home"></span>
                ${message("shop.index.index")}
                </a>
            </div>
            <div class="col-xs-3 text-center">
                <a href="${base}/product_category/index">
                    <span class="glyphicon glyphicon-th-list"></span>
                ${message("shop.index.productCategory")}
                </a>
            </div>
            <div class="col-xs-3 text-center">
                <a href="${base}/cart/list">
                    <span class="glyphicon glyphicon-shopping-cart"></span>
                ${message("shop.index.cart")}
                </a>
            </div>
            <div class="col-xs-3 text-center">
                <a href="${base}/member/index">
                    <span class="glyphicon glyphicon-user"></span>
                ${message("shop.index.member")}
                </a>
            </div>
        </div>
    </div>
</footer>
</body>
</html>