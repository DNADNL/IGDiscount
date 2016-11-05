app.controller('registration', function($scope, $http, $window, facebookServices) {

    $scope.enableSimpleUserForm = false
    $scope.enableSellerCompanyForm = false
    $scope.disableEmail = false
    $scope.user = {}
    $scope.simpleUser = {}

    facebookServices.initialize()

    $scope.showError = false

    $scope.submitForm = function() {
        $scope.enableSimpleUserForm = true
        $scope.enableSellerCompanyForm = false
    }

    $scope.clickCreateSellerAccount = function() {
        $scope.enableSimpleUserForm = false
        $scope.enableSellerCompanyForm = true
    }

    $scope.linkFacebook = function() {
        facebookServices.connectFacebook().then(function() {
            if (facebookServices.isReady()) {
                facebookServices.getInformation().then(function(data) {
                    $scope.user.email = data.email
                    $scope.disableEmail = true
                    $scope.simpleUser.firstName = data.name.split(" ")[0]
                    $scope.simpleUser.lastName = data.name.split(" ")[1]
                });
            }
        })
        facebookServices.clearCache()
    }

    $scope.submitSimpleUserForm = function() {
        var rqt = {
            method : 'POST',
            url : '/simpleUser/create',
            data : $.param({
                email: $scope.user.email,
                password: $scope.user.password,
                firstName : $scope.simpleUser.firstName,
                lastName : $scope.simpleUser.lastName,
                street : $scope.simpleUser.adress,
                streetNumber : $scope.simpleUser.streetNumber,
                postalCode : $scope.simpleUser.postalCode,
                city : $scope.simpleUser.city
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };
        $http(rqt)
            .success(function(data){
                facebookServices.clearCache()
                $window.location.href = '/';
            })
            .error(function(data){
                $scope.showError = true
            })
    };

    $scope.submitSellerCompanyForm = function() {
            var rqt = {
                method : 'POST',
                url : '/sellerCompany/create',
                data : $.param({
                    email: $scope.user.email,
                    password: $scope.user.password,
                    siret :$scope.sellerCompany.siret,
                    companyName : $scope.sellerCompany.companyName,
                    street : $scope.sellerCompany.adress,
                    streetNumber : $scope.sellerCompany.streetNumber,
                    postalCode : $scope.sellerCompany.postalCode,
                    city : $scope.sellerCompany.city
                }),
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };
            $http(rqt)
                .success(function(data){
                    facebookServices.clearCache()
                    $window.location.href = '/';
                })
                .error(function(data){
                    $scope.showError = true
                })
        };
})