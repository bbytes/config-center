'use strict';

/* Services */

ccenterApp.factory('CCProject', ['$resource',
 function ($resource) {
     return $resource('app/rest/ccprojects/:id', {}, {
         'query': { method: 'GET', isArray: true},
         'get': { method: 'GET'},
         'update': {method: 'PUT'}
     });
  
 }]);

ccenterApp.factory('CCProperty', ['$resource',
  function ($resource) {
      return $resource('app/rest/ccpropertys/:id', {}, {
      'query': { method: 'GET', isArray: true},
      'get': { method: 'GET'},
      'update': {method: 'PUT'}
      });
  }]);

ccenterApp.factory('CCProjProperty', ['$resource',
  function ($resource) {
      return $resource('app/rest/ccprojpropertys/:projectId', {}, {
      'query': { method: 'GET', isArray: true}
      });
  }]);

ccenterApp.factory('userService', ['$resource',
  function ($resource) {
      return $resource('app/rest/users/:login', {}, {
    	  'get': { method: 'GET'}
      });
  }]);

ccenterApp.factory('Account', ['$resource',
	function ($resource) {
	    return $resource('app/rest/account', {}, {
	    });
	}]);

ccenterApp.factory('Password', ['$resource',
    function ($resource) {
        return $resource('app/rest/account/change_password', {}, {
        });
    }]);

ccenterApp.factory('Sessions', ['$resource',
    function ($resource) {
        return $resource('app/rest/account/sessions/:series', {}, {
            'get': { method: 'GET', isArray: true}
        });
    }]);

ccenterApp.factory('MetricsService', ['$resource',
    function ($resource) {
        return $resource('metrics/metrics', {}, {
            'get': { method: 'GET'}
        });
    }]);

ccenterApp.factory('ThreadDumpService', ['$http',
    function ($http) {
        return {
            dump: function() {
                var promise = $http.get('dump').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

ccenterApp.factory('HealthCheckService', ['$rootScope', '$http',
    function ($rootScope, $http) {
        return {
            check: function() {
                var promise = $http.get('health').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

ccenterApp.factory('LogsService', ['$resource',
    function ($resource) {
        return $resource('app/rest/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel':  { method: 'PUT'}
        });
    }]);

ccenterApp.factory('AuditsService', ['$http',
    function ($http) {
        return {
            findAll: function() {
                var promise = $http.get('app/rest/audits/all').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findByDates: function(fromDate, toDate) {
                var promise = $http.get('app/rest/audits/byDates', {params: {fromDate: fromDate, toDate: toDate}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    }]);


ccenterApp.factory('growlService', ['$growl', function($growl) {
 
	var growlService = {};
	growlService.show = function (gclass, message, time) {
		var title = 'INFO!!';
		switch (gclass) {
    		case 'warning': title = 'WARNING!!'; break;
    		case 'success': title = 'SUCCESS!!'; break;
    		case 'danger': title = 'DANGER!!'; break;
    		case 'info': title = 'INFO!!'; break;
    		case 'inverse': title = 'INFO!!'; break;
    		case 'primary': title = 'INFO!!'; break;
    		default: title = 'INFO!!';
		}
		$growl.box(title, message, {
	        class: gclass,
	        sticky: time < 0 ? true : false,
	        timeout: 10000
	    }).open();
	}
	 
    return growlService;
}]);


ccenterApp.factory('Session', [
    function () {
        this.create = function (login, firstName, lastName, email, userRoles) {
            this.login = login;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.userRoles = userRoles;
        };
        this.invalidate = function () {
            this.login = null;
            this.firstName = null;
            this.lastName = null;
            this.email = null;
            this.userRoles = null;
        };
        return this;
    }]);

ccenterApp.constant('USER_ROLES', {
        all: '*',
        admin: 'ROLE_ADMIN',
        user: 'ROLE_USER'
    });

ccenterApp.factory('AuthenticationSharedService', ['$rootScope', '$http', 'authService', 'Session', 'Account',
    function ($rootScope, $http, authService, Session, Account) {
        return {
            login: function (param) {
                var data ="j_username=" + param.username +"&j_password=" + param.password +"&_spring_security_remember_me=" + param.rememberMe +"&submit=Login";
                $http.post('app/authentication', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    Account.get(function(data) {
                        Session.create(data.login, data.firstName, data.lastName, data.email, data.roles);
                        $rootScope.account = Session;
                        authService.loginConfirmed(data);
                    });
                }).error(function (data, status, headers, config) {
                    $rootScope.authenticationError = true;
                    Session.invalidate();
                });
            },
            valid: function (authorizedRoles) {

                $http.get('protected/transparent.gif', {
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    if (!!Session.login) {
                        Account.get(function(data) {
                            Session.create(data.login, data.firstName, data.lastName, data.email, data.roles);
                            $rootScope.account = Session;

                            if (!$rootScope.isAuthorized(authorizedRoles)) {
                                event.preventDefault();
                                // user is not allowed
                                $rootScope.$broadcast("event:auth-notAuthorized");
                            }

                            $rootScope.authenticated = true;
                        });
                    }
                    $rootScope.authenticated = !!Session.login;
                }).error(function (data, status, headers, config) {
                    $rootScope.authenticated = false;
                });
            },
            isAuthorized: function (authorizedRoles) {
                if (!angular.isArray(authorizedRoles)) {
                    if (authorizedRoles == '*') {
                        return true;
                    }

                    authorizedRoles = [authorizedRoles];
                }

                var isAuthorized = false;
                angular.forEach(authorizedRoles, function(authorizedRole) {
                    var authorized = (!!Session.login &&
                        Session.userRoles.indexOf(authorizedRole) !== -1);

                    if (authorized || authorizedRole == '*') {
                        isAuthorized = true;
                    }
                });

                return isAuthorized;
            },
            logout: function () {
                $rootScope.authenticationError = false;
                $rootScope.authenticated = false;
                $rootScope.account = null;

                $http.get('app/logout');
                Session.invalidate();
                authService.loginCancelled();
            },
            authenticate : function() {
				var promise = $http.get(
						'app/rest/account').success(
						function(response) {
							return response.data;
						});
				return promise;
			}
        };
    }]);