app.controller('connection', function($scope, $http, $window, facebookServices, Notification) {

    facebookServices.initialize()

    $scope.linkFacebook = function() {
        facebookServices.connectFacebook().then(function(dataToken) {
            if (facebookServices.isReady()) {
                facebookServices.getInformation().then(function(data) {
                    var rqt = {
                        method : 'POST',
                        url : '/signinFacebook',
                        data : $.param({
                            email: data.email,
                            tokenFacebook: dataToken.access_token
                        }),
                        headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                    };
                    $http(rqt)
                        .success(function(data){
                            $.cookie("kindofuser", "su");
                            $window.location.href = '/';
                        })
                        .error(function(data){
                            facebookServices.clearCache()
                            Notification.error("Email or password incorrect")
                        })
                });
            }
        })
    }

    $scope.submitForm = function() {
        var rqt = {
            method : 'POST',
            url : '/signin',
            data : $.param({
                email: $scope.user.email,
                password: $scope.user.password
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt)
            .success(function(data){
                if (data.kindOfUser == "Simple User") {
                    $.cookie("kindofuser", "su");
                }
                else if (data.kindOfUser == "Admin") {
                    $.cookie("kindofuser", "a");
                }
                else {
                    $.cookie("kindofuser", "sc");
                }
                $window.location.href = '/';
            })
            .error(function(data){
                Notification.error("Email or password incorrect")
            })
    };

})

