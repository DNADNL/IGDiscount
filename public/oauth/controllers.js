//inject the twitterService into the controller
app.controller('TwitterController', function($scope, $q) {

    console.log(OAuth.initialize('kwpfPLWbfwLrVCtpuJNpJPpxKf0', {cache:true}));
    authorizationResult = OAuth.create('facebook');

    //when the user clicks the connect twitter button, the popup authorization window opens
    $scope.connectButton = function() {
         console.log("toto")
         OAuth.popup('facebook', {cache:true}, function(error, result) {
            var promise = authorizationResult.get('/me?fields=id,name,email').done(function(data) { //https://dev.twitter.com/docs/api/1.1/get/statuses/home_timeline
                console.log(data)
            });
            console.log(result);
         })
    }

});