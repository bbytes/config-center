'use strict';

/* Controllers */

ccenterApp.controller('CCProjectController', ['$scope', '$http', '$location', 'resolvedCCProject', 'CCProject', 'growlService',
  function ($scope, $http, $location, resolvedCCProject, CCProject, growlService) {

      $scope.ccprojects = resolvedCCProject;
      $scope.environments = [];
      $scope.getenvs = function() {
    	  $http.get('app/rest/ccprojects/getenvs').success(function(data) {
    		  $scope.environments = data;
//    		  $scope.ccproject = {environment : $scope.environments[0]};
    	  });
      };
	  $scope.getenvs();

      $scope.create = function () {
          CCProject.save($scope.ccproject, function () {
	          $scope.ccprojects = CCProject.query();
	          $('#saveCCProjectModal').modal('hide');
	          $scope.clear();
          });
      };

      $scope.update = function () {
    	  CCProject.update($scope.ccproject, function () {
	          $scope.ccprojects = CCProject.query();
	          $('#saveCCProjectModal').modal('hide');
	          $scope.clear();
          });
      };
      
      $scope.confirmDelete = function(projectName, projId) {
    	  $scope.delProj = {delProjName : projectName, delProjId :  projId};
    	  $('#deleteCCProjectModal').modal('show');  
      }
      
      $scope.close = function() {
    	  $scope.delProj = {};
    	  $('#deleteCCProjectModal').modal('hide');
      }
      
      $scope.delete = function (id) {
          CCProject.delete({id: id},
              function () {
                  $scope.ccprojects = CCProject.query();
                  $('#deleteCCProjectModal').modal('hide');
              });
      };
      
      $scope.configure = function (id) {        
          $location.path('/ccproperty/' + id).replace();
      };
      
      $scope.usage = function (id) {
//          $scope.ccproject = CCProject.get({id: id});
          //write to show div component to display
          $location.path('/usage/' + id).replace();
      };
      
      $scope.download = function(id, propertys) {
    	  
    	  if (!propertys || propertys.length < 1) {
    		  growlService.show('info', 'No properties added to this project', 1);
    	  } else {    	  
    		  window.location.href = '/app/ccprojects/propertiesDownload/'+id;
    	  }
      }

      $scope.clear = function () {
          $scope.ccproject = {id: null, projectName: null, environment: $scope.environments[0], creationDate: null, updateDate: null};
      };
      
      $scope.load = function(id) {
    	  $scope.ccproject = CCProject.get({id: id});
    	  $('#saveCCProjectModal').modal('show');
      }
      
     var sendFileToServer = function(formData, projId){
//    	 $("#progressBar").show();
    	$scope.dynamic = 60;
  		var url = document.URL;
  		var url1 = url.split("#")[0];
  	    var uploadURL = "app/ccprojects/uploadFile"; // Upload
  																	// URL
  	    var jqXHR=$.ajax({
  	            xhr: function() {
  	            var xhrobj = $.ajaxSettings.xhr();
  	            if (xhrobj.upload) {
  	                    xhrobj.upload.addEventListener('progress', function(event) {
  	                        var percent = 0;
  	                        var position = event.loaded || event.position;
  	                        var total = event.total;
  	                        if (event.lengthComputable) {
  	                            percent = Math.ceil(position / total * 100);
  	                        }
  	                        $scope.dynamic = 70;
  	                        // Set progress
//	                        status.setProgress(percent);
  	                    }, false);
  	                }
  	            return xhrobj;
  	        },
  	    url: uploadURL,
  	    type: "POST",
  	    contentType:false,
  	    processData: false,
  	        cache: false,
  	        data: formData,
  	        success: function(data){
  	        	
//  	        	status.setProgress(100);
//  	        	$("#uploadStatus").show();
  	        	if(data == "success"){
  	        		$scope.dynamic = 100;
//  	        		status.setProgress(100);
//  	        		$("#uploadStatus").html(data.message);
//  	        		$('#deploymentScriptModal').modal('hide');
//                      $scope.deploymentScripts = DeploymentScript.query();
                      
                      $location.path('/ccproperty/' + projId).replace();
//                      $scope.clear();
  	        	} else {
//  	        		$("#uploadStatus").html(data.error);
  	        	}
  	        }
  	    });
  	}
  	
  	$scope.import = function (projId) {
  		$scope.ccprojid  = projId;
  		$("#fileUpload").click();
    };
       
    $scope.dynamic = 0;
    $scope.fileChange = function(files){
//    	var status = new createStatusbar(); 
    	$scope.dynamic = 20;
    	$scope.files = files;
		/*$("#uploadStatus").hide();
		$("#uploadStatus").html("");*/
		
		for (var i = 0; i < $scope.files.length; i++) {
   	        var fd = new FormData();
   	        fd.append('file', $scope.files[i]);

   	        fd.append('projId', $scope.ccprojid);
   	        sendFileToServer(fd, $scope.ccprojid);
		}
		
		/*for (var i = 0; i < ArticleTab.files.length; i++){
			setFileNameSize(ArticleTab.files[i].name,
			ArticleTab.files[i].size); 
		}*/
		 
   };
       
     /* var createStatusbar = function(){
   	    this.setProgress = function(progress){      
   	        var progressBarWidth = progress * $("#progressBar").width()/ 100; 
   	        $("#progressBar").find('div').animate({ width: progressBarWidth }, 10).html(progress + "% ");
   	        if(parseInt(progress) >= 100)
   	        {
   	        	$("#progressBar").hide();
   	        }
   	    };
   	}*/
      
  }]);

ccenterApp.controller('CCPropertyController', ['$scope', '$http', '$timeout', '$filter', 'resolvedCCProperty', 'CCProperty', 'CCProjProperty', 'project',
   function ($scope, $http, $timeout, $filter, resolvedCCProperty, CCProperty, CCProjProperty, project) {

	/*$("datetimepicker div.datetimepicker-wrapper > input.form-control").on('click', function() {
		$("div.datetimepicker-wrapper > .dpddmenu").css('display', 'block');
	});*/
	
	$scope.dateTimeNow = function() {
	    $scope.date = new Date();
	  };
	  $scope.dateTimeNow();
	  
	  $scope.toggleMinDate = function() {
	    $scope.minDate = $scope.minDate ? null : new Date();
	  };
	  $scope.maxDate = new Date('2014-06-22');
	  $scope.toggleMinDate();

	  $scope.dateOptions = {
	    'starting-day': 1
	  };
	  
	  // Disable weekend selection
	  $scope.disabled = function(calendarDate, mode) {
	    return mode === 'day' && ( calendarDate.getDay() === 0 || calendarDate.getDay() === 6 );
	  };
	  
	  $scope.showWeeks = true;
	  $scope.toggleWeeks = function () {
	    $scope.showWeeks = !$scope.showWeeks;
	  };
	  
	  $scope.hourStep = 1;
	  $scope.minuteStep = 1;

	  $scope.timeOptions = {
	    hourStep: [1, 2, 3],
	    minuteStep: [1, 5, 10, 15, 25, 30]
	  };

	  $scope.showMeridian = true;
	  $scope.timeToggleMode = function() {
	    $scope.showMeridian = !$scope.showMeridian;
	  };
	  
	
	
	$scope.dataTypes = [];
    $scope.getDataTypes = function() {
  	  $http.get('app/rest/ccpropertys/getdatatypes').success(function(data) {
  		  $scope.dataTypes = data;
  		  $scope.strIndex = 0;
  		  
  		  for(var i= 0; i < $scope.dataTypes.length ; i++) {
  			  if ($scope.dataTypes[i].displayName == "String") {
  				$scope.strIndex = i;
  				break;
  			  }
  		  }
  		  $scope.ccproperty = {dataType : $scope.dataTypes[$scope.strIndex]};
  	  });
    };
    $scope.getDataTypes();
	
    $scope.ccpropertys = resolvedCCProperty;
    $scope.ccproject = project;  
       
	$scope.create = function () {
		if ($scope.ccproperty.dataType.name == "DATE") {
			var d = $filter('date')($scope.date, "MM-dd-yyyy");
			$scope.ccproperty.propertyValue = d;
		} else if($scope.ccproperty.dataType.name=="DATETIME") {
			var d = $filter('date')($scope.date, "MM-dd-yyyy");
			var t = $filter('date')($scope.date, "shortTime");
			$scope.ccproperty.propertyValue = d + "#" + t;
		}
		CCProperty.save($scope.ccproperty,  
		function () {
			$scope.ccpropertys = CCProjProperty.query({projectId: project.id});
			$('#saveCCPropertyModal').modal('hide');
			$scope.clear();
		});
	};

	$scope.update = function () {
		
		if ($scope.ccproperty.dataType.name == "DATE") {
			var d = $filter('date')($scope.date, "MM-dd-yyyy");
			$scope.ccproperty.propertyValue = d;
		} else if($scope.ccproperty.dataType.name=="DATETIME") {
			var d = $filter('date')($scope.date, "MM-dd-yyyy");
			var t = $filter('date')($scope.date, "shortTime");
			$scope.ccproperty.propertyValue = d + "#" + t;
		}
		CCProperty.update($scope.ccproperty,  
				function () {
					$scope.ccpropertys = CCProjProperty.query({projectId: project.id});
					$('#saveCCPropertyModal').modal('hide');
					$scope.clear();
				});
	};

	$scope.confirmDelete = function(propertyName, propId) {
  	  $scope.delProp = {delPropName : propertyName, delPropId :  propId};
  	  $('#deleteCCPropertyModal').modal('show');  
    }
    
    $scope.close = function() {
  	  $scope.delProp = {};
  	  $('#deleteCCPropertyModal').modal('hide');
    }
	
	$scope.delete = function (id) {
		CCProperty.delete({id: id},	function () {
			$scope.ccpropertys = CCProjProperty.query({projectId: project.id});
			$('#deleteCCPropertyModal').modal('hide');
		});
	};

	$scope.load = function(id) {
  	  CCProperty.get({id: id}, function (data) {
  		$scope.ccproperty = data; 	
  		
  		
  		if($scope.ccproperty.dataType.displayName === "Date") {
  			$scope.date = new Date($scope.ccproperty.propertyValue);
  			$(".dttime").addClass('hide').removeClass('show');
			$(".dtdate > input").css("width", "455px");
  		} else if ($scope.ccproperty.dataType.displayName === "Date Time") {
			
	  		$scope.date = new Date($scope.ccproperty.propertyValue.replace("#", " "));
	  		
			$(".dttime").addClass('show').removeClass('hide');
			$(".dtdate > input").css("width", "300px");
  		}
  			
  		$scope.ccproperty = {id: data.id, propertyName: data.propertyName, propertyValue: data.propertyValue,  
  				creationDate: data.creationDate, updateDate: data.updateDate, dataType: data.dataType, 
  				projectName: data.projectName, environment: data.environment};
  		
  		for(var i= 0; i < $scope.dataTypes.length ; i++) {
			  if ($scope.dataTypes[i].label == data.dataType.label) {
				  $scope.ccproperty.dataType = $scope.dataTypes[i];
				  break;
			  }
		  }
		  
  		$('#saveCCPropertyModal').modal('show');
  	  });
    }
	
	$scope.clear = function () {
		$("#dataTypeVal").attr('type', "text");
		$scope.ccproperty = {id: null, propertyName: null, propertyValue: null, creationDate: null, updateDate: null, 
			dataType: $scope.dataTypes[$scope.strIndex], projectName: $scope.ccproject.projectName, 
			environment: $scope.ccproject.environment};
	};
	
	$scope.options = ["true", "false"];
	
}]);

ccenterApp.controller('UsageController', ['$scope', '$rootScope', '$location', 'userService', 'CCProject', 'projectId',
    function ($scope, $rootScope, $location, userService, CCProject, projectId) {
	
	$scope.ccproject = {};
	$scope.user = {};
	$scope.usage = {mavenDep : '', springCfg1: '', springCfg2: '', java: '', init: '', getProp: '' };
	
	$scope.getProj = function() {
		CCProject.get({id: projectId},
				function(data){
			$scope.ccproject = data;
			$scope.getUser();
			
		});
	}
	$scope.getUser = function() {
		userService.get({login: $rootScope.account.login},
				function(data){
			$scope.user = data;
			$scope.setUsageProps();
		});
	}
	$scope.getProj();
	
	$scope.getMvnToCopy = function() {
		return $scope.usage.mavenDep;
	}
	$scope.getSpringCfg1ToCopy = function() {
		return $scope.usage.springCfg1;
	}
	$scope.getSpringCfg2ToCopy = function() {
		return $scope.usage.springCfg2;
	}
	$scope.getJavaToCopy = function() {
		return $scope.usage.java;
	}
	$scope.getInitToCopy = function() {
		return $scope.usage.init;
	}
	$scope.getPropToCopy = function() {
		return $scope.usage.getProp;
	}
	
		
	$scope.setUsageProps = function(){
		$scope.usage.mavenDep = '<dependency> \n' +
			'	<groupId>com.bbytes</groupId> \n' +
	        '	<artifactId>config-center-client</artifactId> \n' +
	        '	<version>1.0.0</version> \n' +
	      '</dependency>'; 
	         				
	      $scope.usage.springCfg1 = '<!-- Config loading via config center for PropertyPlaceholderConfigurer--> \n' +
			    '<bean id="placeholderConfig" \n' +
			    '	class="com.bbytes.config.spring.ConfigCenterPropertyPlaceholderConfigurer"> \n' +
			    '	<!-- cloud config server host & port --> \n' +
			    '	<property name="host" value="'+ $location.host() +'" /> \n' +
			    '	<property name="port" value="'+ $location.port() +'" /> \n' +
			    '	<property name="project" value="'+ $scope.ccproject.projectName +'" /> \n' +
			    '	<property name="environment" value="'+ $scope.ccproject.environment.label +'" /> \n' +
			    '	<property name="clientId" value="'+ $scope.user.clientId +'" /> \n' +
			    '	<property name="secretKey" value="'+ $scope.user.secretKey +'" /> \n' +
			    '	<!-- how freq the properties should be checked for update --> \n' +
			    '	<property name="pollDelay" value="10" /> \n' +
			    '	<property name="locations"> \n' +
			    '   	<list> \n' +
			    '        	<value>classpath:META-INF/app.properties \n' +
			    '        	</value> \n' +
			    '        	<value>classpath*:META-INF/rabbitmq-config.properties \n' +
			    '        	</value> \n' +
			    '        	<value>classpath*:META-INF/mongo-config.properties \n' +
			    '        	</value> \n' +
			    '    	</list> \n' +
			    '	</property> \n' +
			    '</bean>';
			
	      	$scope.usage.springCfg2 = '<!-- Config loading via config center for util:properties namespace--> \n' +
		    '<bean id="props" \n' +
		    '	class="com.bbytes.config.spring.ConfigCenterPropertiesFactoryBean"> \n' +
		    '	<!-- cloud config server host & port --> \n' +
		    '	<property name="host" value="'+ $location.host() +'" /> \n' +
		    '	<property name="port" value="'+ $location.port() +'" /> \n' +
		    '	<property name="project" value="'+ $scope.ccproject.projectName +'" /> \n' +
		    '	<property name="environment" value="'+ $scope.ccproject.environment.label +'" /> \n' +
		    '	<property name="clientId" value="'+ $scope.user.clientId +'" /> \n' +
		    '	<property name="secretKey" value="'+ $scope.user.secretKey +'" /> \n' +
		    '	<!-- how freq the properties should be checked for update --> \n' +
		    '	<property name="pollDelay" value="10" /> \n' +
		    '	<property name="location" value="classpath:META-INF/app.properties" /> \n' +
		    '</bean>';
	      
			$scope.usage.java = 'String prop1Val = DynamicPropertyFactory.getInstance().getStringProperty("prop1", "default").get(); // if string \n' +
				'double pollTime = DynamicPropertyFactory.getInstance().getDoubleProperty("pollTime", 1000d).get();  // if double';
			
			$scope.usage.init = 'ConfigCenterPropertyManager.init("'+ $location.host() +'", '+ $location.port() +', "'+ $scope.user.clientId +'", \n' +
			        '"'+ $scope.user.secretKey +'", "'+ $scope.ccproject.projectName +'", "'+ $scope.ccproject.environment.label +'", 10);';
			
			$scope.usage.getProp = 'String testprop = ConfigCenterPropertyManager.getProperty("test-prop")';
	}
 }]);

ccenterApp.controller('MainController', ['$scope', '$location', 'AuthenticationSharedService',
    function ($scope, $location, AuthenticationSharedService) {
	
		$scope.rememberMe = true;
	    $scope.login = function () {
	        AuthenticationSharedService.login({
	            username: $scope.username,
	            password: $scope.password,
	            rememberMe: $scope.rememberMe
	        })
	    }
    
    }]);

ccenterApp.controller('AdminController', ['$scope',
    function ($scope) {
    }]);

ccenterApp.controller('LanguageController', ['$scope', '$translate',
    function ($scope, $translate) {
        $scope.changeLanguage = function (languageKey) {
            $translate.use(languageKey);
        };
    }]);

ccenterApp.controller('MenuController', ['$scope',
    function ($scope) {
    }]);

ccenterApp.controller('LoginController', ['$scope', '$location', 'AuthenticationSharedService',
    function ($scope, $location, AuthenticationSharedService) {
        $scope.rememberMe = true;
        
        $scope.login = function () {
        	var uname = $("#username").val();
    		var passwd = $("#password").val();
            AuthenticationSharedService.login({
                username: uname,
                password: passwd,
                rememberMe: $scope.rememberMe
            })
        }
    }]);

ccenterApp.controller('LogoutController', ['$location', 'AuthenticationSharedService',
    function ($location, AuthenticationSharedService) {
        AuthenticationSharedService.logout();
    }]);

ccenterApp.controller('SettingsController', ['$scope', 'Account',
    function ($scope, Account) {
        $scope.success = null;
        $scope.error = null;
        $scope.settingsAccount = Account.get();

        $scope.save = function () {
            Account.save($scope.settingsAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                    $scope.settingsAccount = Account.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

ccenterApp.controller('PasswordController', ['$scope', 'Password',
    function ($scope, Password) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.changePassword = function () {
            if ($scope.password != $scope.confirmPassword) {
                $scope.doNotMatch = "ERROR";
            } else {
                $scope.doNotMatch = null;
                Password.save($scope.password,
                    function (value, responseHeaders) {
                        $scope.error = null;
                        $scope.success = 'OK';
                    },
                    function (httpResponse) {
                        $scope.success = null;
                        $scope.error = "ERROR";
                    });
            }
        };
    }]);

ccenterApp.controller('SessionsController', ['$scope', 'resolvedSessions', 'Sessions',
    function ($scope, resolvedSessions, Sessions) {
        $scope.success = null;
        $scope.error = null;
        $scope.sessions = resolvedSessions;
        $scope.invalidate = function (series) {
            Sessions.delete({series: encodeURIComponent(series)},
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = "OK";
                    $scope.sessions = Sessions.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

 ccenterApp.controller('MetricsController', ['$scope', 'MetricsService', 'HealthCheckService', 'ThreadDumpService',
    function ($scope, MetricsService, HealthCheckService, ThreadDumpService) {

        $scope.refresh = function() {
            HealthCheckService.check().then(function(data) {
                $scope.healthCheck = data;
            });

            $scope.metrics = MetricsService.get();

            $scope.metrics.$get({}, function(items) {

                $scope.servicesStats = {};
                $scope.cachesStats = {};
                angular.forEach(items.timers, function(value, key) {
                    if (key.indexOf("web.rest") != -1 || key.indexOf("service") != -1) {
                        $scope.servicesStats[key] = value;
                    }

                    if (key.indexOf("net.sf.ehcache.Cache") != -1) {
                        // remove gets or puts
                        var index = key.lastIndexOf(".");
                        var newKey = key.substr(0, index);

                        // Keep the name of the domain
                        index = newKey.lastIndexOf(".");
                        $scope.cachesStats[newKey] = {
                            'name': newKey.substr(index + 1),
                            'value': value
                        };
                    }
                });
            });
        };

        $scope.refresh();

        $scope.threadDump = function() {
            ThreadDumpService.dump().then(function(data) {
                $scope.threadDump = data;

                $scope.threadDumpRunnable = 0;
                $scope.threadDumpWaiting = 0;
                $scope.threadDumpTimedWaiting = 0;
                $scope.threadDumpBlocked = 0;

                angular.forEach(data, function(value, key) {
                    if (value.threadState == 'RUNNABLE') {
                        $scope.threadDumpRunnable += 1;
                    } else if (value.threadState == 'WAITING') {
                        $scope.threadDumpWaiting += 1;
                    } else if (value.threadState == 'TIMED_WAITING') {
                        $scope.threadDumpTimedWaiting += 1;
                    } else if (value.threadState == 'BLOCKED') {
                        $scope.threadDumpBlocked += 1;
                    }
                });

                $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
                    $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

            });
        };

        $scope.getLabelClass = function(threadState) {
            if (threadState == 'RUNNABLE') {
                return "label-success";
            } else if (threadState == 'WAITING') {
                return "label-info";
            } else if (threadState == 'TIMED_WAITING') {
                return "label-warning";
            } else if (threadState == 'BLOCKED') {
                return "label-danger";
            }
        };
    }]);

ccenterApp.controller('LogsController', ['$scope', 'resolvedLogs', 'LogsService',
    function ($scope, resolvedLogs, LogsService) {
        $scope.loggers = resolvedLogs;

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        }
    }]);

ccenterApp.controller('AuditsController', ['$scope', '$translate', '$filter', 'AuditsService',
    function ($scope, $translate, $filter, AuditsService) {
        $scope.onChangeDate = function() {
            AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function(data){
                $scope.audits = data;
            });
        };

        // Date picker configuration
        $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            var tomorrow = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1); // create new increased date

            $scope.toDate = $filter('date')(tomorrow, "yyyy-MM-dd");
        };

        $scope.previousMonth = function() {
            var fromDate = new Date();
            if (fromDate.getMonth() == 0) {
                fromDate = new Date(fromDate.getFullYear() - 1, 0, fromDate.getDate());
            } else {
                fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
            }

            $scope.fromDate = $filter('date')(fromDate, "yyyy-MM-dd");
        };

        $scope.today();
        $scope.previousMonth();
        
        AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function(data){
            $scope.audits = data;
        });
    }]);

