/**
 * 
 */
require.config({
	paths: {
        'jquery': '//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min',
        'bootstrap': 'https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min'
    },
    shim: {
    	'bootstrapValidator.min': ['jquery']
    }
});

require(
	['jquery', 'bootstrap', 'bootstrapValidator.min'],
	function( $, bootstrap, bootstrapValidator ){
    	
		$(document).ready(function() {
    		$('#formLogin').bootstrapValidator({
    			fields: {
    				login: {
    					validators: {
    						blank: {}
    					}    						
    				},
    				password: {
    					validators: {
    						blank: {}
    					}    						
    				}
    			}
    		})
    		.on('success.form.bv', function(e) {
    			e.preventDefault();
	            var $form = $(e.target),
	            	bv = $form.data('bootstrapValidator'),
	            	data = {login: 1, password: 1};
	            
	            $.ajax({
                    url: "./services/auth/login",
                    type: "POST",
                    data: data,
                    cache: false,
                    datatype: 'json',
                         
                    success: function (data, textStatus, jqXHR){
                        //alert("success");
                        switch (data.status) {
                        case "SUCCESS" :
                            window.location.replace("https://"+window.location.host+"<%=request.getContextPath() %>/secure/index.jsp");
                        	break;
                        case "NOTEXISTS":
                        	bv.updateStatus("login","INVALID","blank")
                    		bv.updateMessage("login","blank",data.errorMsg)
                        	break;
                        default:
                        	break;
                        }
                    },
                         
                    error: function (jqXHR, textStatus, errorThrown){
                        alert("error - HTTP STATUS: "+jqXHR.status);
                    },
                         
                    complete: function(jqXHR, textStatus){
                        //alert("complete");
                    }                    
                });
    		});
    		/*
    		var bv = $('#formLogin').data("bootstrapValidator");
    		;
    		*/
    		$('#formRegistration').bootstrapValidator({
    			fields : {
    				login : {
    					validators: {
    	                    stringLength: {
    	                        min: 6
    	                    },
    	                    regexp: {
    	                        regexp: /^[a-zA-Z0-9_\.]+$/,
    	                        message: 'The login can only consist of alphabetical, number, dot and underscore'
    	                    },	                    
    	                    different: {
    	                        field: 'password,confirmPassword',
    	                        message: 'The login and password cannot be the same as each other'
    	                    }
    	                }
    				},
    				password : {
    					validators: {
    	                    identical: {
    	                        field: 'confirmPassword',
    	                        message: 'The password and its confirm are not the same'
    	                    },
    	                    different: {
    	                        field: 'login',
    	                        message: 'The password cannot be the same as login'
    	                    }
    	                }
    				},
    				confirmPassword: {
    	                validators: {
    	                    identical: {
    	                        field: 'password',
    	                        message: 'The password and its confirm are not the same'
    	                    },
    	                    different: {
    	                        field: 'login',
    	                        message: 'The password cannot be the same as login'
    	                    }
    	                }
    	            }
    			}
    		});
    		
    		$('#formEditItem')
			.bootstrapValidator({
				fields : {
					startPrice : {
						validators: {
							numeric: {},
							greaterThan: {
		                        inclusive: false
		                    }
		                }
					},
					bidIncrement : {
						validators: {
							numeric: {},
							greaterThan: {
		                        inclusive: false
		                    }
		                }
					}
				}
			})
			.on('change', 'input[type="checkbox"][name="buyItNow"]', function() {
				var isBuyItNow = $(this).is(':checked'),
					bootstrapValidator = $('#formEditItem').data('bootstrapValidator');
				bootstrapValidator.enableFieldValidators('bidIncrement', !isBuyItNow);
				if (isBuyItNow) {
					$('#bidIncrement')
						.val('')
						.prop('disabled',true)
						.parents('.form-group').hide();
				} else {
					$('#bidIncrement')
						.prop('disabled',false)
						.parents('.form-group').show();
				}
			})
			
			$('#btnReset').click(function() {
    	        $(this).parents('form').data('bootstrapValidator').resetForm(true);
    	        $('#buyItNow').prop('checked', false)
    	        $('#bidIncrement')
    	        	.prop('disabled',false)
					.parents('.form-group').show();
    	    });
    	})
    }
);