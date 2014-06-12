'use strict';

/* App Module */

var ccenterApp = angular.module('ccenterApp', ['http-auth-interceptor', 'tmh.dynamicLocale', 'ui.bootstrap', 'ui.bootstrap.datetimepicker',
    'ngResource', 'ngRoute', 'ngCookies', 'ccenterAppUtils', 'pascalprecht.translate', 'truncate', 'ui.growl', 'ngClipboard']);

ccenterApp.config(['ngClipProvider', function(ngClipProvider) {
    ngClipProvider.setPath("../bower_components/zeroclipboard/ZeroClipboard.swf");
  }]);

/*ccenterApp.config(['$httpProvider', function($httpProvider) {
	 $httpProvider.interceptors.push('noCacheInterceptor');
	}]).factory('noCacheInterceptor', function () {
	            return {
	                request: function (config) {
	                    console.log(config.method);
	                    console.log(config.url);
	                    if(config.method=='GET'){
	                        var separator = config.url.indexOf('?') === -1 ? '?' : '&';
	                        config.url = config.url+separator+'noCache=' + new Date().getTime();
	                    }
	                    console.log(config.method);
	                    console.log(config.url);
	                    if (config.url.indexOf('logout') !== -1) {
	                    	var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                        	config.url = config.url+separator+'noCache=' + new Date().getTime();
	                    }
	                    return config;
	               }
	           };
	    });*/


ccenterApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider',  'tmhDynamicLocaleProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider, USER_ROLES) {
            $routeProvider  
	            .when('/', {
	                templateUrl: 'views/login.html',
	                controller: 'LoginController',
	                access: {
	                    authorizedRoles: [USER_ROLES.all]
	                }
	            })
            	.when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/error', {
                    templateUrl: 'views/error.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/settings', {
                    templateUrl: 'views/settings.html',
                    controller: 'SettingsController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/password', {
                    templateUrl: 'views/password.html',
                    controller: 'PasswordController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/sessions', {
                    templateUrl: 'views/sessions.html',
                    controller: 'SessionsController',
                    resolve:{
                        resolvedSessions:['Sessions', function (Sessions) {
                            return Sessions.get();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/metrics', {
                    templateUrl: 'views/metrics.html',
                    controller: 'MetricsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logs', {
                    templateUrl: 'views/logs.html',
                    controller: 'LogsController',
                    resolve:{
                        resolvedLogs:['LogsService', function (LogsService) {
                            return LogsService.findAll();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/audits', {
                    templateUrl: 'views/audits.html',
                    controller: 'AuditsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logout', {
                    templateUrl: 'views/main.html',
                    controller: 'LogoutController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/docs', {
                    templateUrl: 'views/docs.html',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/ccproject', {
                    templateUrl: 'views/ccprojects.html',
                    controller: 'CCProjectController',
                    resolve:{
                        resolvedCCProject: ['CCProject', function (CCProject) {
                            return CCProject.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/ccproperty/:id', {
                    templateUrl: 'views/ccpropertys.html',
                    controller: 'CCPropertyController',
                    resolve: {
                    	project:['CCProject', '$route', function (CCProject, $route) {
                    		return CCProject.get({id: $route.current.params.id});
                    	}],
                        resolvedCCProperty: ['CCProjProperty', '$route', function (CCProjProperty, $route) {
                        	var id = $route.current.params.id;
                            return CCProjProperty.query({projectId: id});
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/usage/:id', {
                    templateUrl: 'views/usage.html',
                    controller: 'UsageController',
                    resolve: {
                    	projectId:['$route', function ($route) {
                    		return $route.current.params.id;
                    	}]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                /*.when('/main', {
                    templateUrl: 'views/main.html',
                    controller: 'MainController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })*/
                .otherwise({
                    templateUrl: 'views/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                });

            // Initialize angular-translate
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });

            $translateProvider.preferredLanguage('en');

            $translateProvider.useCookieStorage();

            tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js')
            tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
            
        }])
        .run(['$rootScope', '$location', '$http', 'AuthenticationSharedService', 'Session', 'USER_ROLES', 'Account', 'authService',
            function($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES, Account, authService) {
                $rootScope.$on('$routeChangeStart', function (event, next) {
                	
                	AuthenticationSharedService.authenticate().then(function(response) {
	                    if (response.data != '') {
	                    	
	                    	Account.get(function(data) {
	                            Session.create(data.login, data.firstName, data.lastName, data.email, data.roles);
	                            $rootScope.account = Session;
	                            authService.loginConfirmed(data);
	                        });
	                    	
	                    	$rootScope.authenticated = true;
		                    $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
		                    $rootScope.userRoles = USER_ROLES;
		                    AuthenticationSharedService.valid(next.access.authorizedRoles);
		                    
		                    if (!$rootScope.authenticated) {
		                    	$location.path('/login').replace();
		                    }
	                    } else {
	                    	$rootScope.$broadcast('event:auth-loginRequired');
	                    }
	                });

                });

                // Call when the the client is confirmed
                $rootScope.$on('event:auth-loginConfirmed', function(data) {
                    $rootScope.authenticated = true;
//                    $location.path('/ccproject').replace();
                    if ($location.path() === "/login" || $location.path() === "/") {
                        $location.path('/ccproject').replace();
                    }
                });

                // Call when the 401 response is returned by the server
                $rootScope.$on('event:auth-loginRequired', function(rejection) {
                    Session.invalidate();
                    $rootScope.authenticated = false;
                    if ($location.path() !== "/" && $location.path() !== "") {
                        $location.path('/login').replace();
                    }
                });

                // Call when the 403 response is returned by the server
                $rootScope.$on('event:auth-notAuthorized', function(rejection) {
                    $rootScope.errorMessage = 'errors.403';
                    $location.path('/error').replace();
                });

                // Call when the user logs out
                $rootScope.$on('event:auth-loginCancelled', function() {
                    $location.path('/login');
                });
        }]);
