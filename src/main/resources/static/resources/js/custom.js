/* Theme Name: The Project - Responsive Website Template
 * Author:HtmlCoder
 * Author URI:http://www.htmlcoder.me
 * Author e-mail:htmlcoder.me@gmail.com
 * Version:1.4.0
 * Created:March 2015
 * License URI:http://support.wrapbootstrap.com/
 * File Description: Place here your custom scripts
 */

(function($){
	$(document).ready(function(){

		// Notify Plugin - The below code (until line 42) is used for demonstration purposes only
		//-----------------------------------------------
		if (($(".main-navigation.onclick").length>0) && !Modernizr.touch ){
			$.notify({
				// options
				message: 'The Dropdowns of the Main Menu, are now open with click on Parent Items. Click "Home" to checkout this behavior.'
			},{
				// settings
				type: 'info',
				delay: 10000,
				offset : {
					y: 150,
					x: 20
				}
			});
		};
		if (!($(".main-navigation.animated").length>0) && !Modernizr.touch && $(".main-navigation").length>0){
			$.notify({
				// options
				message: 'The animations of main menu are disabled.'
			},{
				// settings
				type: 'info',
				delay: 10000,
				offset : {
					y: 150,
					x: 20
				}
			}); // End Notify Plugin - The above code (from line 14) is used for demonstration purposes only

		};

                $('.disabledOnClick').on('click', function(e) {
                    $(this).attr('disabled', 'disabled');
                    var form = $(this).closest('form');
                    if(form) {
                        form.submit();
                    }
                });
                
                $('.gameEnum').change(function() {
                    if (document.location.href.indexOf('?') < 0) {
                        if(document.location.href.indexOf('game') < 0) {
                            var hf = document.location.href;
                            window.location = hf + "?game=" + $(this).val();
                        }
                        else {
                            var hf = document.location.href;
                            hf = hf.replace(/game=\d{0,4}/g,"game="+$(this).val());
                            window.location = hf;
                        }
                    }
                    else {
                        if(document.location.href.indexOf('game') < 0) {
                            var hf = document.location.href;
                            window.location = hf + "&game=" + $(this).val();
                        }
                        else {
                            var hf = document.location.href;
                            hf = hf.replace(/game=\d{0,4}/g,"game="+ $(this).val());
                            window.location = hf;
                        }
                    }
                });

                $('.platformEnum').change(function() {
                    if (document.location.href.indexOf('?') < 0) {
                        if(document.location.href.indexOf('platform') < 0) {
                            var hf = document.location.href;
                            window.location = hf + "?platform=" + $(this).val();
                        }
                        else {
                            var hf = document.location.href;
                            hf = hf.replace(/platform=\d{0,4}/g,"platform="+$(this).val());
                            window.location = hf;
                        }
                    }
                    else {
                        if(document.location.href.indexOf('platform') < 0) {
                            var hf = document.location.href;
                            window.location = hf + "&platform=" + $(this).val();
                        }
                        else {
                            var hf = document.location.href;
                            hf = hf.replace(/platform=\d{0,4}/g,"platform="+ $(this).val());
                            window.location = hf;
                        }
                    }
                });

                function setSelectedOptions() {
                    var game = getParam('game');
                    var platform = getParam('platform');

                    if(game)
                        $(".gameEnum").val(game);
                    if(platform)
                        $(".platformEnum").val(platform);
                }
                
                function getParam(name, url) {
                    if (!url) url = window.location.href;
                    name = name.replace(/[\[\]]/g, "\\$&");
                    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
                        results = regex.exec(url);
                    if (!results) return null;
                    if (!results[2]) return '';
                    return decodeURIComponent(results[2].replace(/\+/g, " "));
                }

                setSelectedOptions();
                

            $('#loginForm1 button, #loginForm2 button').on('click', function(e) {

                var username = $("#loginForm1 input[name*='username']").val();
                var isChecked = $("#loginForm1 input[name*='remember-me']").is(":checked");
                
                if(isChecked) {
                    setCookie('xms_username', username);
                }
            });


            function setCookie(key, value) {
                var expires = new Date();
                expires.setTime(expires.getTime() + (48 * 60 * 60));
                document.cookie = key + '=' + value + ';expires=' + expires.toUTCString();
            }

            function getCookiebyName(name){
                var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
                return !!pair ? pair[1] : null;
            };

            var cookieUsername = getCookiebyName("xms_username");
            if(cookieUsername)
                $("#loginForm1 input[name*='username'], #loginForm2 input[name*='username']").val(cookieUsername);


            

	}); // End document ready

})(this.jQuery);
