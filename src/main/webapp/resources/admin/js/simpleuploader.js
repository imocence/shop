/*
 * 
 * @author gaoxiang
 * 
 * JavaScript - simpleupload
 * Version: 1.0
 */
$().ready( function() {

	function getCookie(Name) {
	   var search = Name + "="; //查询检索的值
	   var returnvalue = "";//返回值
	   if (document.cookie.length > 0) {
	     sd = document.cookie.indexOf(search);
	     if (sd!= -1) {
	        sd += search.length;
	        end = document.cookie.indexOf(";", sd);
	        if (end == -1)
	         end = document.cookie.length;
	         //unescape() 函数可对通过 escape() 编码的字符串进行解码。
	        returnvalue=unescape(document.cookie.substring(sd, end));
	      }
	   } 
	   return returnvalue;
	}
	
	$.fn.extend({
		"initUploader":function(urlId){
			var uploader = WebUploader.create({
				auto: true,
			    // swf文件路径
			    swf: shopxx.base + '/resources/admin/flash/webuploader.swf',
			
			    // 文件接收服务端。
			    server: shopxx.base + '/admin/file/upload?fileType=image&csrfToken='+ getCookie('csrfToken'),
			
			    // 选择文件的按钮。可选。
			    // 内部根据当前运行是创建，可能是input元素，也可能是flash.
			    pick: {
					id: this,
					multiple: false
				},
			    accept: {
			        title: 'Images',
			        extensions: 'gif,jpg,jpeg,bmp,png',
			        mimeTypes: 'image/jpg,image/jpeg,image/png'
			    },
				fileNumLimit: 1,
				auto: true
			});
			$(this).mouseover(function() {
				uploader.refresh();
			});
			uploader.on( 'uploadSuccess', function( file, response) {   
				$("#" + urlId).val(response.url);
			}); 
		}
	});
});